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
 * This interface is used by the server to send a get data from the client
 * @author jpettit
 */
public interface ClientInterface {
    
    /**
     * should return whether an initial strategy has been selected yet during a 
     * pre-period
     * @return whether an initial strategy has been selected yet
     */
    public boolean haveInitialStrategy();
    
    /**
     * Called when a new message is added to the chatroom
     * @param s 
     */
    public void newMessage(String s);
    
    /**
     * Called to update the client's strategies
     * @param whoChanged the id of the client who's strategy changed 
     * @param strategies the new strategies
     * @param timestamp the time the change occurred
     */
    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies, long timestamp);

    /**
     * Called to update the client's match strategies
     * @param whoChanged the id of the client who's strategy changed 
     * @param strategies the new match strategies
     * @param timestamp the time the change occurred
     */
    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies, long timestamp);

    /**
     * Called to tell the client to end the current sub-period
     * @param subperiod the number of this sup-period
     * @param strategies the strategies at the end of this sub-period
     * @param matchStrategies the match strategies at the end of this sub-period
     * @param payoff the payoff for this client
     * @param matchPayoff the match payoff for this client
     * @param turn whose turn it is
     * @param group the group number of the client
     */
    public void endSubperiod(
            int subperiod,
            Map<Integer, float[]> strategies,
            Map<Integer, float[]> matchStrategies,
            float payoff, float matchPayoff,int turn,int group);
    
}
