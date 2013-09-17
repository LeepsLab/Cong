package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.ThreeStrategyPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;

/**
 *
 * @author jpettit
 */
public class StrategyChanger extends Thread implements Configurable<Config>, Runnable {

    private Config config;
    private volatile boolean shouldUpdate;
    private float[] previousStrategy;
    private float[] currentStrategy;
    private float[] targetStrategy;
    private float[] deltaStrategy;
    private float[] lastStrategy;
    private int rate;
    private float tickDelta;
    private float strategyDelta;
    private long nextAllowedChangeTime;
    private boolean initialLock;
    public volatile boolean isLocked;
    public Selector selector;

    public StrategyChanger() {
        rate = Integer.parseInt(System.getProperty("fire.client.rate", "200"));
        FIRE.client.addConfigListener(this);
        start();
    }

    public void configChanged(Config config) {
        this.config = config;
        if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            previousStrategy = new float[2];
            currentStrategy = new float[2];
            targetStrategy = new float[2];
            deltaStrategy = new float[2];
            lastStrategy = new float[2];
        } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
            previousStrategy = new float[3];
            currentStrategy = new float[3];
            targetStrategy = new float[3];
            deltaStrategy = new float[3];
            lastStrategy = new float[3];
        }
        tickDelta = config.percentChangePerSecond / (1000f / rate) * 2f;
    }

    private void update() {
        targetStrategy = selector.getTarget();
        if (targetStrategy == null) {
            return;
        }
        boolean same = true;
        for (int i = 0; i < targetStrategy.length; i++) {
            if (Math.abs(targetStrategy[i] - currentStrategy[i]) > Float.MIN_NORMAL) {
                same = false;
            }
        }
        if (same) {
            for (int i = 0; i < targetStrategy.length; i++) {
                targetStrategy[i] = currentStrategy[i];
            }
            return;
        }
        float totalDelta = 0f;
        for (int i = 0; i < currentStrategy.length; i++) {
            deltaStrategy[i] = targetStrategy[i] - currentStrategy[i];
            totalDelta += Math.abs(deltaStrategy[i]);
        }
        if (config.percentChangePerSecond < 1f && totalDelta > tickDelta) {
            for (int i = 0; i < deltaStrategy.length; i++) {
                deltaStrategy[i] = tickDelta * (deltaStrategy[i] / totalDelta);
                currentStrategy[i] += deltaStrategy[i];
            }
        } else {
            for (int i = 0; i < currentStrategy.length; i++) {
                currentStrategy[i] = targetStrategy[i];
            }
        }

        sendUpdate();
        FIRE.client.getClient().setMyStrategy(currentStrategy);
        selector.setCurrent(currentStrategy);
        if (config.delay != null) {
            int delay = config.delay.getDelay();
            nextAllowedChangeTime = System.currentTimeMillis() + Math.round(1000 * delay);
        }
    }

    private void sendUpdate() {
        if (config.subperiods == 0) {
            float total = 0;
            for (int i = 0; i < previousStrategy.length; i++) {
                total += Math.abs(previousStrategy[i] - currentStrategy[i]);
            }
            strategyDelta += total / 2;
        }
        Thread.yield();
        FIRE.client.getServer().strategyChanged(
                currentStrategy,
                targetStrategy,
                FIRE.client.getID());
    }

    @Override
    public void run() {
        long nanoWait = rate * 1000000;
        while (true) {
            long start = System.nanoTime();
            isLocked = decisionDelayed();
            if (shouldUpdate) {
                selector.setEnabled(!isLocked);
            }
            if (!isLocked && shouldUpdate) {
                update();
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

    private boolean decisionDelayed() {
        return System.currentTimeMillis() < nextAllowedChangeTime;
    }

    public void setCurrentStrategy(float[] strategy) {
        for (int i = 0; i < currentStrategy.length; i++) {
            previousStrategy[i] = strategy[i];
            currentStrategy[i] = strategy[i];
        }
        selector.setCurrent(currentStrategy);
    }

    public void setTargetStrategy(float[] strategy) {
        if (config.percentChangePerSecond >= 1.0f
                || !FIRE.client.getClient().haveInitialStrategy()) {
            for (int i = 0; i < strategy.length; i++) {
                currentStrategy[i] = strategy[i];
                targetStrategy[i] = strategy[i];
            }
            FIRE.client.getClient().setMyStrategy(strategy);
            selector.setCurrent(strategy);
            sendUpdate();
            return;
        } else {
            for (int i = 0; i < targetStrategy.length; i++) {
                targetStrategy[i] = strategy[i];
            }
        }
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
    }

    public void setPause(boolean paused) {
        this.shouldUpdate = !paused;
        selector.setEnabled(!paused);
    }

    public void endSubperiod(int subperiod, float[] subperiodStrategy, float[] counterpartSubperiodStrategy) {
        if (subperiod == 1) {
            System.arraycopy(config.initialStrategy, 0, lastStrategy, 0, lastStrategy.length);
        }
        float total = 0;
        for (int i = 0; i < subperiodStrategy.length; i++) {
            total += Math.abs(subperiodStrategy[i] - lastStrategy[i]);
        }
        strategyDelta += total / 2;
        System.arraycopy(subperiodStrategy, 0, lastStrategy, 0, lastStrategy.length);
    }

    public void endPeriod() {
        shouldUpdate = false;
        initialLock = true;
        selector.setEnabled(false);
    }

    public float[] getCurrentStrategy() {
        return currentStrategy;
    }

    public static interface Selector {

        public void startPrePeriod();

        public void startPeriod();

        public void setEnabled(boolean enabled);

        public void setCurrent(float[] strategy);

        public void setInitial(float[] strategy);

        public void setCounterpart(float[] strategy);

        public float[] getTarget();

        public void setCurrentPercent(float percent);

        public void update();
    }
}
