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
public class TwoStrategyPayoffFunction implements PayoffFunction {

    public String name;
    public float Aa;
    public float AaStart, AaEnd;
    public float Ab, Ba, Bb;
    public float min, max;
    public float subperiodBonus;

    public TwoStrategyPayoffFunction() {
        AaStart = Float.NaN;
        AaEnd = Float.NaN;
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
        if (!popStrategies.containsKey(id)) {
            return 0;
        }
        A = popStrategies.get(id)[0];
        B = 1 - A;
        a = PayoffUtils.getAverageStrategy(id, matchPopStrategies)[0];
        b = 1 - a;
        return A * (a * Aa + b * Ab) + B * (a * Ba + b * Bb);
    }

    public float getSubperiodBonus(int subperiod, Config config) {
        return subperiodBonus;
    }

    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return popStrategies.get(id);
    }

    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return PayoffUtils.getAverageStrategy(id, matchPopStrategies);
    }

    public void configure(Config config) {
    }
}
