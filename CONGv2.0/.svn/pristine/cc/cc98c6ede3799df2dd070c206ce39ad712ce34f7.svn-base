/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;

/**
 *
 * @author jpettit
 */
public class StrategyChanger extends Thread implements Configurable<Config>, Runnable {

    private Config config;
    private volatile boolean shouldUpdate;
    private float[] deltaStrategy;
    private long nextAllowedChangeTime;
    private boolean initialLock;
    private float[] current;
    private long updateMillis = 100;
    public Selector selector;

    @SuppressWarnings({"CallToThreadStartDuringObjectConstruction", "LeakingThisInConstructor"})
    public StrategyChanger() {
        FIRE.client.addConfigListener(this);
        start();
    }

    public void configChanged(Config config) {
        this.config = config;
        int size = 0;
        if (config.payoffFunction.getNumStrategies() <= 2) {
            size = 1;
        } else if (config.payoffFunction.getNumStrategies() == 3) {
            size = 3;
        }
    }

    private void update() {
        if (Client.state.target == null) {
            return;
        }
        float tickDelta = config.percentChangePerSecond / (1000f / updateMillis) * 2f;
        if (current == null) {
            current = Client.state.getMyStrategy();
        }
        if (current.length == 1) {
            tickDelta /= 2f;
        }
        if (deltaStrategy == null || deltaStrategy.length != current.length) {
            deltaStrategy = new float[current.length];
        }
        if (!Float.isNaN(config.grid)) {
            for (int i = 0; i < Client.state.target.length; i++) {
                float r = Client.state.target[i] % config.grid;
                if (r > config.grid / 2f) {
                    Client.state.target[i] -= r;
                    Client.state.target[i] += config.grid;
                } else {
                    Client.state.target[i] -= r;
                }
            }
        }
        boolean almostSame = true;
        for (int i = 0; i < Client.state.target.length; i++) {
            if (Math.abs(Client.state.target[i] - current[i]) > Float.MIN_NORMAL) {
                almostSame = false;
            }
        }
        if (almostSame) {
            System.arraycopy(current, 0, Client.state.target, 0, Client.state.target.length);
            return;
        }
        float totalDelta = 0f;
        for (int i = 0; i < current.length; i++) {
            deltaStrategy[i] = Client.state.target[i] - current[i];
            totalDelta += Math.abs(deltaStrategy[i]);
        }
        if (!Float.isNaN(config.percentChangePerSecond) && totalDelta > tickDelta) {
            for (int i = 0; i < deltaStrategy.length; i++) {
                deltaStrategy[i] = tickDelta * (deltaStrategy[i] / totalDelta);
                current[i] += deltaStrategy[i];
            }
        } else {
            System.arraycopy(Client.state.target, 0, current, 0, current.length);
        }

        sendUpdate();
        if (config.delay != null) {
            int delay = config.delay.getDelay();
            nextAllowedChangeTime = System.currentTimeMillis() + Math.round(1000 * delay);
        }
    }

    private void sendUpdate() {
        FIRE.client.getServer().strategyChanged(
                current,
                Client.state.target,
                FIRE.client.getID());
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        while (true) {
            if (config == null) {
                try {
                    Thread.sleep(50);
                    continue;
                } catch (InterruptedException ex) {
                }
            }

            long nanoWait = updateMillis * 1000000;
            long start = System.nanoTime();

            if (shouldUpdate) {
                selector.setEnabled(!isLocked());
                try {
                    update();
                } catch (Exception ex) {
                    System.err.println("Unhandled exception updating strategy");
                    ex.printStackTrace();
                }
            }

            long elapsed = System.nanoTime() - start;
            long sleepNanos = nanoWait - elapsed;
            if (sleepNanos > 0) {
                try {
                    Thread.sleep(sleepNanos / 1000000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public boolean isLocked() {
        return System.currentTimeMillis() < nextAllowedChangeTime || isTurnTakingLocked(Client.state.subperiod);
    }

    public void startPeriod() {
        shouldUpdate = true;
        nextAllowedChangeTime = System.currentTimeMillis();
        if (config.initialDelay != null && initialLock) {
            int delay = config.initialDelay.getDelay();
            nextAllowedChangeTime = System.currentTimeMillis() + Math.round(1000 * delay);
            initialLock = false;
        }
        current = new float[config.initialStrategy.length];
        System.arraycopy(config.initialStrategy, 0, current, 0, current.length);
        FIRE.client.getServer().strategyChanged(current, current, FIRE.client.getID());
    }

    public void setPause(boolean paused) {
        this.shouldUpdate = !paused;
        selector.setEnabled(!paused);
    }

    public void endSubperiod(int subperiod) {
        selector.endSubperiod(subperiod);
        current = null;
    }

    public void endPeriod() {
        shouldUpdate = false;
        initialLock = true;
        selector.setEnabled(false);
    }

    public boolean isTurnTakingLocked(int subperiod) {
        return isTurnTakingLocked(Client.state.id, subperiod, config);
    }

    public static boolean isTurnTakingLocked(int id, int subperiod, Config config) {
        if (config != null && config.turnTaking) {
            if (subperiod == 0) {
                return false;
            }
            int index = -1;
            for (int i = 0; i < config.initiatives.length; i++) {
                if (config.initiatives[i] == id) {
                    index = i;
                }
            }
            if (index == -1) {
                System.err.println("isTurnTakingLocked: couldn't find id in initiatives");
                return false;
            }
            if (subperiod % config.initiatives.length == index) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static interface Selector {

        public void startPrePeriod();

        public void startPeriod();

        public void endSubperiod(int subperiod);

        public void setEnabled(boolean enabled);
    }
}
