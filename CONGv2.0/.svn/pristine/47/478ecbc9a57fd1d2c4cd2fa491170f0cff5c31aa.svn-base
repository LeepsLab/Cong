/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.server;

import edu.ucsc.leeps.fire.FIREServer;
import edu.ucsc.leeps.fire.FIREServerInterface;
import edu.ucsc.leeps.fire.client.ClientControllerInterface;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.config.Configurator;
import edu.ucsc.leeps.fire.logging.LogEvent;
import edu.ucsc.leeps.fire.logging.Logger;
import edu.ucsc.leeps.fire.networking.NetworkUtil;
import edu.ucsc.leeps.fire.reflection.ClassFinder;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 *
 * @author jpettit
 */
public final class ServerController<ServerInterfaceType, ClientInterfaceType, ConfigType extends BaseConfig>
        implements
        FIREServer<ClientInterfaceType, ConfigType>,
        SessionControllerInterface<ServerInterfaceType, ClientInterfaceType, ConfigType>,
        LocalClient.ConnectionListener {

    protected String experimentName;
    private FIREServerInterface server;
    protected Configurator<ConfigType> configurator;
    protected Map<Integer, LocalClient> clients;
    protected Logger logger;
    private Timer eventTimer;
    private TimerTask tickTask, endTask;
    private static final int quickTicksPerSecond = 10;
    private long periodStartTime, periodEndTime;
    private ControlPanel controlPanel;
    private Random random = new Random();

    public ServerController() {
        ClassFinder.initialize();
        server = (FIREServerInterface) ClassFinder.newServer();
        NetworkUtil.attachServerController(this);
        configurator = new Configurator<ConfigType>();
        logger = new Logger();
        clients = new HashMap<Integer, LocalClient>();
        controlPanel = ControlPanel.createControlPanel(this);
    }

    public float getTotalPoints(int id) {
        return clients.get(id).totalPoints;
    }

    public float getPeriodPoints(int id) {
        return clients.get(id).periodPoints;
    }

    public String getName(int id) {
        return clients.get(id).name;
    }

    public void addToPeriodPoints(int id, float points) {
        LocalClient client = clients.get(id);
        client.periodPoints += points;
        client.controller.setPeriodPoints(client.periodPoints);
        controlPanel.clientsChanged();
    }

    public void setPeriodPoints(int id, float points) {
        LocalClient client = clients.get(id);
        client.periodPoints = points;
        client.controller.setPeriodPoints(client.periodPoints);
        controlPanel.clientsChanged();
    }

    public ConfigType getConfig() {
        return configurator.getConfig();
    }

    public ConfigType getConfig(int id) {
        return configurator.getConfig(id);
    }

    public List<ConfigType> getDefinedConfigs() {
        return configurator.getDefinedConfigs();
    }

    public String getConfigSource() {
        return configurator.getConfigSource();
    }

    public List<String> getPeriods() {
        return configurator.getPeriods();
    }

    public Map<String, ConfigType> getDefaultConfigs() {
        return configurator.getDefaultConfigs();
    }

    public void setPeriodListIndex(int i) {
        configurator.setPeriodListIndex(i);
    }

    public synchronized void register(
            ClientControllerInterface<ClientInterfaceType, ConfigType> controller,
            String name) {
        for (LocalClient client : clients.values()) {
            if (client.name.equals(name)) {
                client.controller = controller;
                client.client = controller.getClient();
                client.connected = true;
                Map<Integer, Object> customClients = new HashMap<Integer, Object>();
                for (Integer clientId : clients.keySet()) {
                    customClients.put(clientId, clients.get(clientId).controller.getClient());
                }
                server.setClients(customClients);
                controlPanel.setClients(clients);
                return;
            }
        }
        int id = clients.size() + 1;
        LocalClient client = new LocalClient(id, controller, controller.getClient(), name, this);
        clients.put(id, client);
        client.controller.setID(id);
        Map<Integer, Object> customClients = new HashMap<Integer, Object>();
        for (Integer clientId : clients.keySet()) {
            customClients.put(clientId, clients.get(clientId).controller.getClient());
        }
        server.setClients(customClients);
        if (controlPanel != null) {
            controlPanel.setClients(clients);
        }
    }

    public synchronized void unregister(int id) {
        try {
            clients.get(id).controller.disconnect();
        } catch (Exception ex) {
        }
        clients.remove(id);
        int i = 1;
        for (LocalClient client : clients.values()) {
            client.id = i++;
            client.controller.setID(id);
        }
        Map<Integer, LocalClient> newClients = new HashMap<Integer, LocalClient>();
        for (LocalClient client : clients.values()) {
            newClients.put(client.id, client);
        }
        clients = newClients;
        Map<Integer, Object> customClients = new HashMap<Integer, Object>();
        for (Integer clientId : clients.keySet()) {
            customClients.put(clientId, clients.get(clientId).controller.getClient());
        }
        server.setClients(customClients);
        controlPanel.setClients(clients);
    }

    public ServerInterfaceType getServerInterface() {
        return (ServerInterfaceType) server;
    }

    /**
     * Kicks off the period Configures the server, then the clients Notifies the
     * clients, then the server that that pre-period has started Finally,
     * schedules the real period to begin in number of seconds, specified by the
     * config
     */
    public boolean startPeriod(String period) {

        if (!configurator.setPeriod(period)) {
            return false;
        }

        server.configurePeriod();
        for (LocalClient client : clients.values()) {
            // ensure config exists for this client, configurator will create and store
            // if one does not already exist
            configurator.getConfig(client.id);
            client.controller.setConfigs(configurator.getConfigs());
            client.periodPoints = 0;
            client.controller.setPeriodPoints(client.periodPoints);
        }

        eventTimer = new Timer();
        for (LocalClient client : clients.values()) {
            client.controller.startPrePeriod();
        }
        eventTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (server.readyToEndPrePeriod()) {
                    startRealPeriod();
                    cancel();
                }
            }
        }, configurator.getConfig().preLength * 1000, 1000);
        return true;
    }

    public void startRealPeriod() {

        periodStartTime = System.currentTimeMillis();
        server.startPeriod(periodStartTime);
        for (LocalClient client : clients.values()) {
            client.controller.startPeriod();
        }
        controlPanel.periodStarted(configurator.getPeriod(), configurator.getConfig().length);
        tickTask = new TimerTask() {

            @Override
            public void run() {
                long timestamp = System.currentTimeMillis();
                long elapsedTime = timestamp - periodStartTime;
                int millisLeft = (int) ((getConfig().length * 1000) - elapsedTime);
                server.tick(millisLeft);
                for (LocalClient client : clients.values()) {
                    client.controller.tick(millisLeft);
                }
            }
        };
        eventTimer.scheduleAtFixedRate(tickTask, 0, 1000 / quickTicksPerSecond);
        if (getConfig().length > 0) {
            periodEndTime = periodStartTime + (getConfig().length * 1000);
            endTask = new TimerTask() {

                @Override
                public void run() {
                    endPeriod();
                }
            };
            eventTimer.schedule(endTask, periodEndTime - System.currentTimeMillis());
        }
    }

    public void endPeriod() {
        eventTimer.cancel();
        server.endPeriod();
        if (configurator.getConfig().paid) {
            for (LocalClient client : clients.values()) {
                client.totalPoints += client.periodPoints;
                client.controller.setTotalPoints(client.totalPoints);
                System.err.println(client.name + "," + client.totalPoints);
            }
            controlPanel.clientsChanged();
        }
        for (LocalClient client : clients.values()) {
            client.controller.endPeriod();
        }
        logger.endPeriod();
        controlPanel.periodEnded(configurator.getPeriod());
        configurator.nextPeriod();
    }

    public void loadConfig(Configurator<ConfigType> newConfigurator) throws BaseConfig.ConfigException {
        configurator.load(newConfigurator);
    }

    public void commit(LogEvent event) {
        logger.commit(event);
    }

    public Timer getTimer() {
        return eventTimer;
    }

    public void endSession() {
        for (LocalClient client : clients.values()) {
            try {
                client.controller.disconnect();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                System.exit(0);
            }
        }.start();
    }

    public Map<Integer, LocalClient> getClients() {
        return clients;
    }

    public Configurator<ConfigType> getConfigurator() {
        return configurator;
    }

    public void Disconnected(LocalClient c) {
        controlPanel.clientsChanged();
    }

    public Random getRandom() {
        return random;
    }

    static {
        try {
            System.setErr(new PrintStream(Logger.getCurrentWorkingDir() + "server_errors.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
