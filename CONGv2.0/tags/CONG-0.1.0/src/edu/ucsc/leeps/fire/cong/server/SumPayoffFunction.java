package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class SumPayoffFunction extends TwoStrategyPayoffFunction {

    public float A, B, C, D, smin, smax;
    public Type type;

    public enum Type {

        proportional, linear, public_goods
    };

    @Override
    public boolean reverseXAxis() {
        return true;
    }

    @Override
    public float getMax() {
        return max;
    }

    @Override
    public float getMin() {
        return min;
    }

    @Override
    public int getNumStrategies() {
        return 1;
    }

    /*
     * proportional: (A * s / sum ) - C * s + D
     * linear: (A - B * sum) * s - C * s + D
     * public goods: 100 * ((A * sum) + (1 - s))
     */
    @Override
    public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config) {
        float sum = 0;
        for (int i : popStrategies.keySet()) {
            if (i != id) {
                sum += smin + popStrategies.get(i)[0] * (smax - smin);
            }
        }
        float s = smin + (popStrategies.get(id)[0] * (smax - smin));
        sum += s;
        float u = 0;
        switch (type) {
            case proportional:
                u = (A * s / sum) - C * s + D;
                break;
            case linear:
                u = ((A - B * sum) * s - C * s) + D;
                break;
            case public_goods:
                u = (smax - s) + (A / popStrategies.size()) * sum;
                break;
        }
        return u;
    }

    @Override
    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        float[] summary = new float[12];
        int i = 0;
        for (int match : popStrategies.keySet()) {
            summary[i++] = popStrategies.get(match)[0];
        }
        for (; i < summary.length; i++) {
            summary[i] = Float.NaN;
        }
        return summary;
    }

    @Override
    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        float[] summary = new float[12];
        int i = 0;
        for (int match : popStrategies.keySet()) {
            if (match != id) {
                summary[i++] = popStrategies.get(match)[0];
            }
        }
        for (; i < summary.length; i++) {
            summary[i] = Float.NaN;
        }
        return summary;
    }
}
