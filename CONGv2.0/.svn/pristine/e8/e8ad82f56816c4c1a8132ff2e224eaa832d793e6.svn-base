package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jpettit
 */
public class StrategyChanger extends Thread implements Configurable<Config>, Runnable {

    private Config config;
    private volatile boolean shouldUpdate;
    private float[] previousStrategy;
    private float[] deltaStrategy;
    private float[] lastStrategy;
    private float strategyDelta;
    private long nextAllowedChangeTime;
    private boolean initialLock;
    private boolean turnTakingLock;
    public Selector selector;
    private DelayQueue<DelayWrapper> infoDelay;

    public StrategyChanger() {
        infoDelay = new DelayQueue<DelayWrapper>();
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
        previousStrategy = new float[size];
        deltaStrategy = new float[size];
        lastStrategy = new float[size];
    }

    private void update() {
        if (Client.state.target == null) {
            return;
        }
        float tickDelta = config.percentChangePerSecond / (1000f / config.strategyUpdateMillis) * 2f;
        float[] current = Client.state.getMyStrategy();
        if (current.length == 1) {
            tickDelta /= 2f;
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
        boolean same = true;
        for (int i = 0; i < Client.state.target.length; i++) {
            if (Math.abs(Client.state.target[i] - current[i]) > Float.MIN_NORMAL) {
                same = false;
            }
        }
        if (same) {
            for (int i = 0; i < Client.state.target.length; i++) {
                Client.state.target[i] = current[i];
            }
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
            for (int i = 0; i < current.length; i++) {
                current[i] = Client.state.target[i];
            }
        }

        sendUpdate();
        if (config.delay != null) {
            int delay = config.delay.getDelay();
            nextAllowedChangeTime = System.currentTimeMillis() + Math.round(1000 * delay);
        }
    }

    private void sendUpdate() {
        float[] c = Client.state.getMyStrategy();
        float[] current = new float[c.length];
        System.arraycopy(c, 0, current, 0, c.length);
        if (config.subperiods == 0) {
            float total = 0;
            for (int i = 0; i < previousStrategy.length; i++) {
                total += Math.abs(previousStrategy[i] - current[i]);
            }
            strategyDelta += total / 2;
        }
        FIRE.client.getServer().strategyChanged(
                current,
                Client.state.target,
                FIRE.client.getID());
    }

    @Override
    public void run() {
        while (true) {
            if (config == null) {
                try {
                    Thread.sleep(50);
                    continue;
                } catch (InterruptedException ex) {
                }
            }

            long nanoWait = config.strategyUpdateMillis * 1000000;
            long start = System.nanoTime();

            if (shouldUpdate) {
                selector.setEnabled(!isLocked());
                update();
            }
            DelayWrapper delayedUpdate = infoDelay.poll();
            while (delayedUpdate != null) {
                switch (delayedUpdate.type) {
                    case STRATEGIES:
                        Client.state.strategies = delayedUpdate.map;
                        break;
                    case MATCH_STRATEGIES:
                        Client.state.matchStrategies = delayedUpdate.map;
                        break;
                }
                delayedUpdate = infoDelay.poll();
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
        return System.currentTimeMillis() < nextAllowedChangeTime || turnTakingLock;
    }

    public float getCost() {
        if (config == null) {
            return 0f;
        }
        return strategyDelta * config.changeCost;
    }

    public void startPeriod() {
        shouldUpdate = true;
        strategyDelta = 0;
        nextAllowedChangeTime = System.currentTimeMillis();
        if (config.initialDelay != null && initialLock) {
            int delay = config.initialDelay.getDelay();
            nextAllowedChangeTime = System.currentTimeMillis() + Math.round(1000 * delay);
            initialLock = false;
        }
        turnTakingLock = false;
    }

    public void setPause(boolean paused) {
        this.shouldUpdate = !paused;
        selector.setEnabled(!paused);
    }

    public void endSubperiod(int subperiod) {
        if (subperiod == 1) {
            System.arraycopy(config.initialStrategy, 0, lastStrategy, 0, lastStrategy.length);
        }
        float[] subperiodStrategy = Client.state.getMyStrategy();
        float total = 0;
        for (int i = 0; i < subperiodStrategy.length; i++) {
            total += Math.abs(subperiodStrategy[i] - lastStrategy[i]);
        }
        strategyDelta += total / 2;
        System.arraycopy(subperiodStrategy, 0, lastStrategy, 0, lastStrategy.length);
        selector.endSubperiod(subperiod);
        turnTakingLock = isTurnTakingLocked(subperiod);
    }

    public void endPeriod() {
        shouldUpdate = false;
        initialLock = true;
        selector.setEnabled(false);
    }

    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies) {
        if (config == null || config.infoDelay == 0 || Client.state.id == whoChanged) {
            Client.state.strategies = strategies;
        } else {
            DelayWrapper node = new DelayWrapper(strategies, config.infoDelay, DelayWrapper.InfoType.STRATEGIES);
            infoDelay.offer(node);
        }
    }

    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies) {
        if (config == null || config.infoDelay == 0) {
            Client.state.matchStrategies = matchStrategies;
        } else {
            DelayWrapper node = new DelayWrapper(matchStrategies, config.infoDelay, DelayWrapper.InfoType.MATCH_STRATEGIES);
            infoDelay.offer(node);
        }
    }

    public boolean isTurnTakingLocked(int subperiod) {
        return isTurnTakingLocked(Client.state.id, subperiod);
    }

    public boolean isTurnTakingLocked(int id, int subperiod) {
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

    public static class DelayWrapper implements Delayed {

        public static enum InfoType {

            STRATEGIES, MATCH_STRATEGIES
        };
        private Map<Integer, float[]> map;
        private long expiry;
        private InfoType type;

        DelayWrapper(Map<Integer, float[]> mapIn, long delay, InfoType type) {
            this.map = mapIn;
            this.expiry = System.currentTimeMillis() + delay;
            this.type = type;
        }

        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(expiry - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);
        }

        public int compareTo(Delayed delayed) {
            DelayWrapper other = (DelayWrapper) delayed;
            if (this.expiry < other.expiry) {
                return -1;
            } else if (this.expiry > other.expiry) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
