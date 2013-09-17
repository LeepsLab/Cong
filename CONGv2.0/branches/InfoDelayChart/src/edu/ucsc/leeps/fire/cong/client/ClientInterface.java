package edu.ucsc.leeps.fire.cong.client;

import java.util.Map;

/**
 *
 * @author jpettit
 */
public interface ClientInterface {

    public boolean haveInitialStrategy();

    public void newMessage(String s);

    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies, long timestamp);

    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies, long timestamp);

    public void endSubperiod(
            int subperiod,
            Map<Integer, float[]> strategies,
            Map<Integer, float[]> matchStrategies,
            float payoff, float matchPayoff);
}
