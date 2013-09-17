/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

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
