package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.FIREServerInterface;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.logging.StrategyChangeEvent;
import edu.ucsc.leeps.fire.logging.Dialogs;
import edu.ucsc.leeps.fire.server.ServerController.State;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author jpettit
 */
public class Server implements ServerInterface, FIREServerInterface<ClientInterface, Config> {

    private Map<Integer, ClientInterface> clients;
    private Population population;
    private BlockingQueue<StrategyChangeEvent> strategyChangeEvents;
    private StrategyProcessor strategyProcessor;
    private Map<Integer, String> aliases;
    private Map<Integer, Color> colors;
    private int secondsLeft;

    public Server() {
        clients = new HashMap<Integer, ClientInterface>();
        strategyChangeEvents = new LinkedBlockingQueue<StrategyChangeEvent>();
        strategyProcessor = new StrategyProcessor();
        strategyProcessor.start();
    }

    public void strategyChanged(
            final float[] newStrategy,
            final float[] targetStrategy,
            final Integer id) {
        if (FIRE.server.getState() == State.RUNNING_PERIOD) {
            StrategyChangeEvent newEvent = new StrategyChangeEvent(System.nanoTime(), id, newStrategy, targetStrategy);
            strategyChangeEvents.add(newEvent);
        }
    }

    public void configurePeriod() {
        Map<Integer, ClientInterface> members = new HashMap<Integer, ClientInterface>();
        members.clear();
        members.putAll(clients);
        Config config = FIRE.server.getConfig();
        if (config.indefiniteEnd != null) {
            if (config.indefiniteEnd.subperiodLength != 0) {
                config.subperiods = config.indefiniteEnd.length(FIRE.server.getRandom());
                config.length = config.subperiods * config.indefiniteEnd.subperiodLength;
            } else {
                config.length = config.indefiniteEnd.length(FIRE.server.getRandom());
            }
            if (config.length == 0) {
                config.length = 1;
            }
            for (int id : members.keySet()) {
                FIRE.server.getConfig(id).subperiods = config.subperiods;
                FIRE.server.getConfig(id).length = config.length;
            }
        }
        population = new Population();
        aliases = new HashMap<Integer, String>();
        colors = new HashMap<Integer, Color>();
        population.configure(members, aliases, colors);
    }

    public boolean readyToEndPrePeriod() {
        for (Integer id : clients.keySet()) {
            if (!clients.get(id).haveInitialStrategy()) {
                return false;
            }
        }
        return true;
    }

    public void startPeriod(long periodStartTime) {
        secondsLeft = FIRE.server.getConfig().length;
        population.setPeriodStartTime();
        configureSubperiods();
    }

    public void endPeriod() {
        secondsLeft = 0;
        if (FIRE.server.getConfig().subperiods == 0) {
            population.endPeriod();
        } else {
            population.endSubperiod(FIRE.server.getConfig().subperiods);
            population.logTick(FIRE.server.getConfig().subperiods, 0);
        }

        for (int id : clients.keySet()) {
            float points = FIRE.server.getPeriodPoints(id);
            FIRE.server.setPeriodPoints(id, points);
        }
    }

    public void tick(int secondsLeft) {
        this.secondsLeft = secondsLeft;
        if (FIRE.server.getConfig().subperiods == 0) {
            population.logTick(0, secondsLeft);
            population.evaluate(System.nanoTime());
        }
    }

    public void quickTick(int millisLeft) {
    }

    private void configureSubperiods() {
        if (FIRE.server.getConfig().subperiods == 0) {
            return;
        }
        long millisPerSubperiod = Math.round(
                (FIRE.server.getConfig().length / (float) FIRE.server.getConfig().subperiods) * 1000);
        FIRE.server.getTimer().scheduleAtFixedRate(new TimerTask() {

            private int subperiod = 1;

            @Override
            public void run() {
                if (subperiod <= FIRE.server.getConfig().subperiods) {
                    population.endSubperiod(subperiod);
                    population.logTick(subperiod, 0);
                    subperiod++;
                }
            }
        }, millisPerSubperiod, millisPerSubperiod);
    }

    public void newMessage(String message, int senderID) {
        if (message.equals("")) {
        } else {
            String html;
            if (senderID == -1) {
                html = "SERVER: " + message;
            } else {
                Color c = colors.get(senderID);
                html = String.format("<font color=\"rgb(%d, %d, %d)\">", c.getRed(), c.getGreen(), c.getBlue())
                        + aliases.get(senderID)
                        + "</font>" + ": "
                        + message;
            }
            population.newMessage(secondsLeft, message, html, senderID, aliases.get(senderID));
        }
    }

    public void unregister(int id) {
        //disconnect client
        clients.remove(id);
    }

    public static void main(String[] args) {
        FIRE.startServer();
    }

    public boolean register(int id, ClientInterface client) {
        clients.put(id, client);
        return true;
    }

    private class StrategyProcessor extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    StrategyChangeEvent event = strategyChangeEvents.take();
                    population.strategyChanged(event.id, event.newStrategy, event.targetStrategy);
                } catch (InterruptedException ex) {
                    Dialogs.popUpAndExit(ex);
                }
                if (strategyChangeEvents.size() > 10) {
                    System.err.println("WARNING: Input queue depth = " + strategyChangeEvents.size());
                }
            }
        }
    }
}
