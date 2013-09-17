package edu.ucsc.leeps.fire.cong.client;

import java.util.Map;

/**
 *
 * @author jpettit
 */
public interface ClientInterface {

    public boolean haveInitialStrategy();

    public float getCost();

    public void newMessage(String s);

    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies);

    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies);

    public void endSubperiod(
            int subperiod,
            Map<Integer, float[]> strategies,
            Map<Integer, float[]> matchStrategies,
            float payoff, float matchPayoff);
}
