package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.State.Strategy;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class PayoffUtils {

    public static float[] getAverageStrategy(int id, Map<Integer, float[]> strategies) {
        Config config;
        if (FIRE.client != null) {
            config = FIRE.client.getConfig();
        } else {
            config = FIRE.server.getConfig();
        }
        float[] average = null;
        if (strategies.isEmpty()) {
            return new float[]{0};
        }
        for (int match : strategies.keySet()) {
            if (average == null) {
                average = new float[strategies.get(match).length];
            }
            if (!(config.excludeSelf && id == match)) {
                float[] s = strategies.get(match);
                for (int i = 0; i < average.length; i++) {
                    average[i] += s[i];
                }
            }
        }
        for (int i = 0; i < average.length; i++) {
            if (config.excludeSelf) {
                average[i] /= (strategies.size() - 1);
            } else {
                average[i] /= strategies.size();
            }
        }
        return average;
    }

    /**
     *
     * @return My flow payoff using the current state
     */
    public static float getPayoff() {
        return FIRE.client.getConfig().payoffFunction.getPayoff(
                Client.state.id,
                Client.state.currentPercent,
                Client.state.strategies, Client.state.matchStrategies,
                FIRE.client.getConfig());
    }

    /**
     *
     * @param strategy
     * @return The fictional flow payoff if I was playing strategy
     */
    public static float getPayoff(float[] strategy) {
        return FIRE.client.getConfig().payoffFunction.getPayoff(
                Client.state.id,
                Client.state.currentPercent,
                Client.state.getFictitiousStrategies(strategy), Client.state.matchStrategies,
                FIRE.client.getConfig());
    }

    /**
     *
     * @param strategy
     * @return The fictional flow payoff for ID playing strategy
     */
    public static float getPayoff(int id, float[] strategy) {
        return FIRE.client.getConfig().payoffFunction.getPayoff(
                id,
                Client.state.currentPercent,
                Client.state.getFictitiousStrategies(id, strategy), Client.state.matchStrategies,
                FIRE.client.getConfig());
    }

    /**
     *
     * @param strategy
     * @param matchStrategy
     * @return The fictional flow payoff if I was playing strategy and the
     * average of my matched population was matchStrategy
     */
    public static float getPayoff(float[] strategy, float[] matchStrategy) {
        return FIRE.client.getConfig().payoffFunction.getPayoff(
                Client.state.id,
                Client.state.currentPercent,
                Client.state.getFictitiousStrategies(strategy),
                Client.state.getFictitiousMatchStrategies(matchStrategy),
                FIRE.client.getConfig());
    }

    /**
     *
     * @return The average flow payoff of my matched population, using the current state
     */
    public static float getMatchPayoff() {
        float payoff = 0;
        for (Integer matchID : Client.state.matchStrategies.keySet()) {
            payoff += FIRE.client.getConfig().counterpartPayoffFunction.getPayoff(
                    matchID,
                    Client.state.currentPercent,
                    Client.state.matchStrategies, Client.state.strategies,
                    FIRE.client.getConfig());
        }
        return payoff / Client.state.matchStrategies.size();
    }

    /**
     *
     * @param strategy
     * @param matchStrategy
     * @return The average fictitious flow payoff of my matched population
     */
    public static float getMatchPayoff(float[] strategy, float[] matchStrategy) {
        return FIRE.client.getConfig().counterpartPayoffFunction.getPayoff(
                Client.state.id,
                Client.state.currentPercent,
                Client.state.getFictitiousMatchStrategies(matchStrategy),
                Client.state.getFictitiousStrategies(strategy),
                FIRE.client.getConfig());
    }

    public static float getTotalPayoff(int id, float currentPercent, List<Strategy> strategiesTime, Config config) {
        if (config.subperiods == 0) {
            return getContinuousTotalPayoff(id, currentPercent, strategiesTime, config);
        } else {
            return getSubperiodTotalPayoff(id, currentPercent, strategiesTime, config);
        }
    }

    public static float getContinuousTotalPayoff(int id, float currentPercent, List<Strategy> strategiesTime, Config config) {
        float periodPoints = 0;
        float lastPercent = 0;
        Map<Integer, float[]> lastStrategies = null;
        Map<Integer, float[]> lastMatchStrategies = null;
        for (Strategy s : strategiesTime) {
            if (s.delayed()) {
                break;
            }
            float percent;
            percent = s.timestamp / (float) (config.length * 1e9);
            if (lastPercent > 0) {
                float flowPayoff = config.payoffFunction.getPayoff(
                        id, percent, lastStrategies, lastMatchStrategies, config);
                if (config.indefiniteEnd == null) {
                    periodPoints += flowPayoff * (percent - lastPercent);
                } else {
                    periodPoints += flowPayoff * (percent - lastPercent) * config.length;
                }
            }
            lastPercent = percent;
            lastStrategies = s.strategies;
            lastMatchStrategies = s.matchStrategies;
        }
        if (lastStrategies != null && lastMatchStrategies != null) {
            float flowPayoff = config.payoffFunction.getPayoff(
                    id, currentPercent, lastStrategies, lastMatchStrategies, config);
            float delayPercent = config.infoDelay / (float) config.length;
            if (currentPercent - delayPercent - lastPercent > 0) {
                if (config.indefiniteEnd == null) {
                    periodPoints += flowPayoff * (currentPercent - delayPercent - lastPercent);
                } else {
                    periodPoints += flowPayoff * (currentPercent - delayPercent - lastPercent) * config.length;
                }
            }
        }
        return periodPoints;
    }

    public static float getSubperiodTotalPayoff(int id, float currentPercent, List<Strategy> strategiesTime, Config config) {
        float periodPoints = 0;
        for (Strategy s : strategiesTime) {
            if (s.delayed()) {
                break;
            }
            float flowPayoff = config.payoffFunction.getPayoff(
                    id, s.timestamp / (float) config.subperiods, s.strategies, s.matchStrategies, config);
            if (config.indefiniteEnd == null) {
                periodPoints += (1 / (float) config.subperiods) * flowPayoff;
            } else {
                periodPoints += flowPayoff;
            }
        }
        return periodPoints;
    }
}
