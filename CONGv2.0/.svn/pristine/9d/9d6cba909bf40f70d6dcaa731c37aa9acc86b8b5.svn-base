package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.FIREServerInterface;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.logging.StrategyChangeEvent;
import edu.ucsc.leeps.fire.server.ServerController.State;
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
    private StrategyUpdater strategyUpdater;

    public Server() {
        clients = new HashMap<Integer, ClientInterface>();
        strategyUpdater = new StrategyUpdater();
    }

    public synchronized void strategyChanged(
            float[] newStrategy,
            float[] targetStrategy,
            Integer id) {
        if (FIRE.server.getState() == State.RUNNING_PERIOD) {
            //StrategyChangeEvent event = new StrategyChangeEvent();
            //event.id = id;
            //event.newStrategy = newStrategy;
            //event.targetStrategy = targetStrategy;
            //event.timestamp = System.currentTimeMillis();
            //strategyUpdater.add(event);
            population.strategyChanged(
                    newStrategy,
                    targetStrategy,
                    id,
                    System.currentTimeMillis());
        }
    }

    public void configurePeriod() {
        configurePopulations();
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
        population.setPeriodStartTime(periodStartTime);
        configureImpulses();
        configureSubperiods();
    }

    public void endPeriod() {
        strategyUpdater.endPeriod();
        if (FIRE.server.getConfig().subperiods == 0) {
            population.endPeriod();
        } else {
            population.endSubperiod(FIRE.server.getConfig().subperiods);
            population.logTick(FIRE.server.getConfig().subperiods, 0);
        }

        for (int id : clients.keySet()) {
            float points = FIRE.server.getPeriodPoints(id);
            float cost = clients.get(id).getCost();
            if (cost > points && !FIRE.server.getConfig().negativePayoffs) {
                cost = points;
            }
            FIRE.server.setPeriodPoints(id, points - cost);
        }
    }

    public void tick(int secondsLeft) {
        if (FIRE.server.getConfig().subperiods == 0) {
            population.logTick(0, secondsLeft);
        }
    }

    public void quickTick(int millisLeft) {
    }

    private void configurePopulations() {
        Map<Integer, ClientInterface> members = new HashMap<Integer, ClientInterface>();
        members.clear();
        members.putAll(clients);
        population = new Population();
        population.configure(members);
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

    private void configureImpulses() {
        if (FIRE.server.getConfig().impulse != 0f) {
            long impulseTimeMillis = Math.round(
                    (FIRE.server.getConfig().length * 1000f) * FIRE.server.getConfig().impulse);
            FIRE.server.getTimer().schedule(new TimerTask() {

                @Override
                public void run() {
                    doImpulse();
                }
            }, impulseTimeMillis);
        }
    }

    private void doImpulse() {
        for (Map.Entry<Integer, ClientInterface> entry : clients.entrySet()) {
            int id = entry.getKey();
            ClientInterface client = entry.getValue();
            float r = FIRE.server.getRandom().nextFloat();
            float[] newStrategy = new float[]{r, 1 - r};
            client.setMyStrategy(newStrategy);
            strategyChanged(newStrategy, newStrategy, id);
        }
    }

    public void newMessage(String message, int senderID) {
        for (Map.Entry<Integer, ClientInterface> entry : clients.entrySet()) {
            ClientInterface client = entry.getValue();
            client.newMessage(message, senderID);

        }
    }

    public void unregister(int id) {
        clients.remove(id);
    }

    public static void main(String[] args) {
        FIRE.startServer();
    }

    public boolean register(int id, ClientInterface client) {
        clients.put(id, client);
        return true;
    }

    private class StrategyUpdater extends Thread {

        private BlockingQueue<StrategyChangeEvent> queue;

        public StrategyUpdater() {
            queue = new LinkedBlockingQueue<StrategyChangeEvent>();
            start();
        }

        public void add(StrategyChangeEvent event) {
            try {
                queue.put(event);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void endPeriod() {
            if (queue.size() != 0) {
                System.err.println("still have " + queue.size() + " strategies to process");
            }
            queue.clear();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    StrategyChangeEvent event = queue.take();
                    population.strategyChanged(
                            event.newStrategy,
                            event.targetStrategy,
                            event.id,
                            event.timestamp);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
