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
public class QWERTYPayoffFunction extends TwoStrategyPayoffFunction {

    public String platform1;
    public String platform2;
    public float[][] pf1, pf2;
    public float cost1, cost2;

    @Override
    public float getMin() {
        return min;
    }

    @Override
    public float getMax() {
        return max;
    }

    @Override
    public float getPayoff(
            int id, float percent,
            Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies,
            Config config) {
        float[] strategy = popStrategies.get(id);
        float[][] platformPayoffs;
        float platformCost;
        if (strategy[0] < 0.5) {
            platformPayoffs = pf1;
            platformCost = cost1;
        } else {
            platformPayoffs = pf2;
            platformCost = cost2;
        }
        int numSameType = getInSame(id, strategy, popStrategies);
        int numDiffType = getInSame(id, strategy, matchPopStrategies);
        return platformPayoffs[numDiffType][numSameType] - platformCost;
    }

    public static int getInSame(int id, float[] myStrategy, Map<Integer, float[]> strategies) {
        // the number of players with strategy equal to yours
        int count = 0;
        for (float[] strategy : strategies.values()) {
            if (Math.abs(myStrategy[0] - strategy[0]) < Float.MIN_NORMAL) {
                count++;
            }
        }
        return count;
    }

    public static int getNotInSame(int id, float[] myStrategy, Map<Integer, float[]> strategies) {
        // the number of players with strategy not equal to yours
        int count = 0;
        for (float[] strategy : strategies.values()) {
            if (Math.abs(myStrategy[0] - strategy[0]) > Float.MIN_NORMAL) {
                count++;
            }
        }
        return count;
    }

    /*
     * Parse platform1 and platform2 strings into the pf1 and pf2 array.
     * This should be easier... finding a simpler way would be nice.
     */
    @Override
    public void configure(Config config) {
        pf1 = parseArrayString(platform1);
        pf2 = parseArrayString(platform2);
        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
        for (float[] row : pf1) {
            for (float p : row) {
                if (p < min) {
                    min = p;
                }
                if (p > max) {
                    max = p;
                }
            }
        }
        for (float[] row : pf2) {
            for (float p : row) {
                if (p < min) {
                    min = p;
                }
                if (p > max) {
                    max = p;
                }
            }
        }
    }

    private float[][] parseArrayString(String arrayString) {
        String[] rows = arrayString.split("\\},\\{");
        int numCols = rows[0].split(",").length;
        float[][] array = new float[rows.length][numCols];
        for (int row = 0; row < rows.length; row++) {
            String[] values = rows[row].replace("}", "").replace("{", "").split(",");
            for (int col = 0; col < values.length; col++) {
                array[row][col] = Float.parseFloat(values[col]);
            }
        }
        return array;
    }
}
