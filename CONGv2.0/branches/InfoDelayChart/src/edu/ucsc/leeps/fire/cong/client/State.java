package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author jpettit
 */
public class State {

    public int id;
    public int subperiod;
    public volatile float currentPercent;
    public ConcurrentHashMap<Integer, float[]> strategies, matchStrategies;
    public final List<Strategy> strategiesTime;
    public volatile float subperiodPayoff, subperiodMatchPayoff;
    public float[] target;
    public StrategyChanger strategyChanger;

    public State(StrategyChanger changer) {
        this.strategyChanger = changer;
        strategiesTime = new LinkedList<Strategy>();
        strategies = new ConcurrentHashMap<Integer, float[]>();
        matchStrategies = new ConcurrentHashMap<Integer, float[]>();
    }

    public void startPeriod() {
        subperiod = 0;
        currentPercent = 0;
        synchronized (strategiesTime) {
            strategiesTime.clear();
        }
        strategies.clear();
        matchStrategies.clear();
        setMyStrategy(FIRE.client.getConfig().initialStrategy);
    }

    public void endPeriod() {
        currentPercent = 1f;
    }

    public void setMyStrategy(float[] strategy) {
        float[] s = new float[strategy.length];
        System.arraycopy(strategy, 0, s, 0, s.length);
        strategies.put(id, s);
        target = new float[strategy.length];
        System.arraycopy(strategy, 0, target, 0, target.length);
    }

    public float[] getMyStrategy() {
        return strategies.get(id);
    }

    public void setTarget(float[] target, Config config) {
        Client.state.target = target;
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
    }

    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies, long timestamp) {
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(timestamp, copyMap(strategies), copyMap(matchStrategies)));
        }
        this.strategies = new ConcurrentHashMap<Integer, float[]>(strategies);
    }

    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies, long timestamp) {
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(timestamp, copyMap(strategies), copyMap(matchStrategies)));
        }
        this.matchStrategies = new ConcurrentHashMap<Integer, float[]>(matchStrategies);
    }

    public void endSubperiod(int subperiod, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies) {
        this.subperiod = subperiod;
        this.strategies = new ConcurrentHashMap<Integer, float[]>(strategies);
        this.matchStrategies = new ConcurrentHashMap<Integer, float[]>(matchStrategies);
        Config config = FIRE.client.getConfig();
        this.subperiodPayoff = config.payoffFunction.getPayoff(id, subperiod, strategies, matchStrategies, config);
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(subperiod, copyMap(strategies), copyMap(matchStrategies)));
        }
    }

    public Map<Integer, float[]> getFictitiousStrategies(int id, float[] strategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            if (i == id) {
                fake.put(i, strategy);
            } else {
                fake.put(i, strategies.get(i));
            }
        }
        return fake;
    }

    public Map<Integer, float[]> getFictitiousStrategies(float[] strategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            if (i == id) {
                fake.put(i, strategy);
            } else {
                fake.put(i, strategies.get(i));
            }
        }
        return fake;
    }

    public Map<Integer, float[]> getFictitiousMatchStrategies(float[] matchStrategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            fake.put(i, matchStrategy);
        }
        return fake;
    }

    public static class Strategy {

        public final long timestamp;
        public final Map<Integer, float[]> strategies;
        public final Map<Integer, float[]> matchStrategies;

        public Strategy(long timestamp, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies) {
            this.timestamp = timestamp;
            this.strategies = strategies;
            this.matchStrategies = matchStrategies;
        }

        public boolean delayed() {
            Config config = FIRE.client.getConfig();
            if (config.subperiods == 0) {
                return Client.state.currentPercent < 1 && ((1e9 * (Client.state.currentPercent * config.length)) - timestamp) < 1e9 * config.infoDelay;
            } else {
                return Client.state.subperiod < config.subperiods && Client.state.subperiod - timestamp < config.infoDelay;
            }
        }
    }

    public static Map<Integer, float[]> copyMap(Map<Integer, float[]> m) {
        Map<Integer, float[]> copy = new HashMap<Integer, float[]>();
        for (int id : m.keySet()) {
            float[] a = m.get(id);
            if (a != null) {
                float[] f = new float[a.length];
                System.arraycopy(a, 0, f, 0, f.length);
                copy.put(id, f);
            }
        }
        return copy;
    }
}
