/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

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
    public int numStrategies = 1;

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
        return numStrategies;
    }

    /*
     * proportional: (A * s / sum ) - C * s + D linear: (A - B * sum) * s - C *
     * s + D public goods: 100 * ((A * sum) + (1 - s))
     */
    @Override
    public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config) {
        float sum = 0;
        for (int i : popStrategies.keySet()) {
            float s = popStrategies.get(i)[0];
            boolean outOfGame = numStrategies == 2 && popStrategies.get(i)[1] == 0;
            if (i != id && !outOfGame) {
                sum += smin + s * (smax - smin);
            }
        }
        float s = smin + (popStrategies.get(id)[0] * (smax - smin));
        sum += s;
        float u = 0;
        float n = popStrategies.size();
        switch (type) {
            case proportional:
                u = (A * s / sum) - C * s + D;
                break;
            case linear:
                u = ((A - B * sum) * s - C * s) + D;
                break;
            case public_goods:
                if (numStrategies == 2 && popStrategies.get(id)[1] == 0) {
                    u = smax;
                } else {
                    u = (smax - s) + (A / n) * sum + C;
                }
                break;
        }
        return u;
    }

    public float getContributions(Map<Integer, float[]> popStrategies) {
        float sum = 0;
        for (int i : popStrategies.keySet()) {
            float s = popStrategies.get(i)[0];
            if (numStrategies == 2 && popStrategies.get(i)[1] == 0) {
                s = 0;
            }
            sum += smin + s * (smax - smin);
        }
        return sum;
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
