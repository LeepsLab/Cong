/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jpettit
 */
public class PricingPayoffFunction extends TwoStrategyPayoffFunction {

    public float E;

    public PricingPayoffFunction() {
        min = 0;
        max = 100;
        E = Float.NaN;
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

    @Override
    public float getPayoff(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        float minPrice = Float.POSITIVE_INFINITY;
        Set<Integer> minIDs = new HashSet<Integer>();
        for (int i : popStrategies.keySet()) {
            float price = (popStrategies.get(i)[0] * max) - min;
            if (equalPrices(price, minPrice)) {
                minIDs.add(i);
            } else if (price < minPrice) {
                minIDs.clear();
                minPrice = price;
                minIDs.add(i);
            }
        }
        float profit = 0;
        if (minIDs.contains(id)) {
            if (Float.isNaN(E)) {
                profit = (minPrice - config.marginalCost) / minIDs.size();
            } else {
                profit = ((1 - E * minPrice) / minIDs.size()) * (minPrice - config.marginalCost);
            }
            if (profit < 0) {
                profit = 0;
            }
        }
        return profit;
    }

    private boolean equalPrices(float p1, float p2) {
        return Math.abs(p1 - p2) < 0.5;
    }

    /*
     * Return the min of popStrategies
     */
    @Override
    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        float[] summary = new float[]{Float.POSITIVE_INFINITY};
        for (int match : popStrategies.keySet()) {
            if (popStrategies.get(match)[0] < summary[0]) {
                summary[0] = popStrategies.get(match)[0];
            }
        }
        return summary;
    }

    /*
     * Return an array of the strategies for everyone in popStrategies except id
     */
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
