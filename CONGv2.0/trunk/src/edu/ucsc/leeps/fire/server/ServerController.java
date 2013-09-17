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
    
    /**
     * Constructor.  Initializes the class finder and server, then attaches this
     * ServerController to the NetworkUtil which remotes this server controller 
     * and add it to the ItemServer
     */
    public ServerController() {
        ClassFinder.initialize(); //loads in classes
        server = (FIREServerInterface) ClassFinder.newServer();
        NetworkUtil.attachServerController(this);// remotes this instance
        configurator = new Configurator<ConfigType>();
        logger = new Logger();
        clients = new HashMap<Integer, LocalClient>();
        controlPanel = ControlPanel.createControlPanel(this);
    }
    
    /**
     * Returns the total points of the client whose id is passed.
     * @param id id of a client
     * @return the total points of the client whose id is passed.
     */
    public float getTotalPoints(int id) {
        return clients.get(id).totalPoints;
    }
    
    /**
     * Returns the total points for the period of the client whose id is passed.
     * @param id id of a client
     * @return the total points for the period of the client whose id is passed.
     */
    public float getPeriodPoints(int id) {
        return clients.get(id).periodPoints;
    }
    
    /**
     * Returns the name of the client whose id is passed.  This is either the clients
     * fire id or login name.
     * @param id id of a client
     * @return the name of the client whose id is passed
     */
    public String getName(int id) {
        return clients.get(id).name;
    }
    
    /**
     * Adds the passed points to the indicated clients period points
     * @param id id of a clients
     * @param points points to be added to the clients points for the current period
     */
    public void addToPeriodPoints(int id, float points) {
        LocalClient client = clients.get(id);
        client.periodPoints += points;
        client.controller.setPeriodPoints(client.periodPoints);
        controlPanel.clientsChanged();
    }
    
    /**
     * Sets a clients period points
     * @param id id of a client
     * @param points the clients period points
     */
    public void setPeriodPoints(int id, float points) {
        LocalClient client = clients.get(id);
        client.periodPoints = points;
        client.controller.setPeriodPoints(client.periodPoints);
        controlPanel.clientsChanged();
    }
    
    /**
     * Returns the type of the current default config
     * @return the type of the current default config
     */
    public ConfigType getConfig() {
        return configurator.getConfig();
    }
    
    /**
     * Returns the config with the passed id for the current period
     * @param id the id of the config
     * @return the config with the passed id for the current period
     */
    public ConfigType getConfig(int id) {
        return configurator.getConfig(id);
    }
    
    /**
     * returns a list of all of the defined configs for the current period 
     * excluding default configs
     * @return a list of all of the defined configs for the current period
     */
    public List<ConfigType> getDefinedConfigs() {
        return configurator.getDefinedConfigs();
    }
    
    /**
     * Returns the path to the current config file
     * @return the path to the current config file
     */
    public String getConfigSource() {
        return configurator.getConfigSource();
    }
    
     /**
      * Returns a list of all of the period numbers 
      * @return a list of all of the period numbers 
      */
    public List<String> getPeriods() {
        return configurator.getPeriods();
    }

    /**
     * Returns a map containing all of the default configs
     * @return a map containing all of the default configs
     */
    public Map<String, ConfigType> getDefaultConfigs() {
        return configurator.getDefaultConfigs();
    }
    
    /**
     * Moves to the period with the passed index
     * @param i indes of the period to move to
     */
    public void setPeriodListIndex(int i) {
        configurator.setPeriodListIndex(i);
    }
    
    /**
     * Registers the client with the server.  The client is wrapped in an LocalClient
     * instance, assigned and id and, stored is the server.
     * @param controller
     * @param name 
     */
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
    
    /**
     * Removes a client from the server.
     * @param id 
     */
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
    
    /**
     * Returns this servers implementation of ServerInterfaceType
     * @return this servers implementation of ServerInterfaceType
     */
    public ServerInterfaceType getServerInterface() {
        return (ServerInterfaceType) server;
    }

    /**
     * Kicks off the period. Configures the server, then the clients. Notifies the
     * clients, then the server that that pre-period has started. Finally,
     * schedules the real period to begin in number of seconds, specified by the
     * config.
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
    
    /**
     * Called in start period to start the real period after the pre-period ends.
     */
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
    
    /**
     * Ends the current period and adds each clients period points to their total
     * points.
     */
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
    
    /**
     * Loads a new config with the passed configurator
     * @param newConfigurator
     * @throws edu.ucsc.leeps.fire.config.BaseConfig.ConfigException 
     */
    public void loadConfig(Configurator<ConfigType> newConfigurator) throws BaseConfig.ConfigException {
        configurator.load(newConfigurator);
    }
    
    /**
     * Adds an event to the logger 
     * @param event 
     */
    public void commit(LogEvent event) {
        logger.commit(event);
    }
    
    /**
     * Returns the event timer.  The event timer calls tick and checks for when it
     * is time to end a period or just sets the end of a pre-period.
     * @return 
     */
    public Timer getTimer() {
        return eventTimer;
    }
    
    /**
     * Closes the program.  Attempts to disconnect all of the clients , waits 
     * for a second, then calls System.exit().
     */
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
    
    /**
     * Returns a map of all the LocalClient instances.
     * @return a map of all the LocalClient instances
     */
    public Map<Integer, LocalClient> getClients() {
        return clients;
    }
    
    /**
     * Returns the current configurator.
     * @return the current configurator
     */
    public Configurator<ConfigType> getConfigurator() {
        return configurator;
    }
    
    /**
     * Called when a client is disconnected.  Updates the control panel.
     * @param c 
     */
    public void Disconnected(LocalClient c) {
        controlPanel.clientsChanged();
    }
    
    /**
     * returns a semi random number generator stored in the server
     * @return a semi random number generator stored in the server
     */
    public Random getRandom() {
        return random;
    }
    
    /**
     * Redirects all of the error messages to the file server_errors.txt
     */
    static {
        try {
            System.setErr(new PrintStream(Logger.getCurrentWorkingDir() + "server_errors.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
