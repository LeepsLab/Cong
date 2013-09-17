package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class TwoStrategyPayoffFunction implements PayoffFunction {

    public String name;
    public float Aa;
    public float AaStart, AaEnd;
    public float Ab, Ba, Bb;
    public boolean isCounterpart;
    public float min, max;
    public float subperiodBonus;

    public TwoStrategyPayoffFunction() {
        AaStart = Float.NaN;
        AaEnd = Float.NaN;
        isCounterpart = false;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public int getNumStrategies() {
        return 2;
    }

    public boolean reverseXAxis() {
        return false;
    }

    public float getPayoff(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        if (!Float.isNaN(AaStart) && !Float.isNaN(AaEnd)) {
            Aa = AaStart + (percent * (AaEnd - AaStart));
        }
        float A, B, a, b;
        A = popStrategies.get(id)[0];
        B = 1 - A;
        a = PayoffUtils.getAverageMatchStrategy(id, popStrategies, matchPopStrategies)[0];
        b = 1 - a;
        if (isCounterpart) {
            return A * (a * Aa + b * Ba) + B * (a * Ab + b * Bb);
        } else {
            return A * (a * Aa + b * Ab) + B * (a * Ba + b * Bb);
        }
    }

    public float getSubperiodBonus(int subperiod, Config config) {
        return subperiodBonus;
    }

    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return popStrategies.get(id);
    }

    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return PayoffUtils.getAverageMatchStrategy(id, popStrategies, matchPopStrategies);
    }

    public void configure() {
    }
}
