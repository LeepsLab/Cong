package edu.ucsc.leeps.fire.cong.client;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class State {

    public int id;
    public int subperiod;
    public volatile float currentPercent;
    public volatile Map<Integer, float[]> strategies, matchStrategies;
    public volatile float subperiodPayoff, subperiodMatchPayoff;
    public volatile float[] target;
    public StrategyChanger strategyChanger;

    public State(StrategyChanger changer) {
        this.strategyChanger = changer;
        currentPercent = 0;
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
}
