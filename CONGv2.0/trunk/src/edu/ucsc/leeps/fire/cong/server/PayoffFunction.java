/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.server;

import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.io.Serializable;
import java.util.Map;

/**
 * This interface describes classes that can be used to define how sessions are
 * scored
 * @author dev
 */
public interface PayoffFunction extends Serializable {
    
    /**
     * Returns the minimum payoff from this payoff function
     * @return the minimum payoff from this payoff function
     */
    public float getMin();
    
    /**
     * Returns the maximum payoff from this payoff function
     * @return the maximum payoff from this payoff function
     */
    public float getMax();
    
    /**
     * Returns the number of strategies that this payoff function uses
     * @return 
     */
    public int getNumStrategies();
    
    /**
     * Returns possible bonus points to be added to each client's score at the end
     * of a subperiod.
     * @param subperiod the number of the subperiod
     * @param config the current config
     * @return possible bonus points for the end of a sub period
     */
    public float getSubperiodBonus(int subperiod, Config config);
    
    /**
    * Returns the payoff the the client whose id is passed
    * @param id id of client whose payoff is to be returned
    * @param percent -1 if homotopy is specified, otherwise the amount of time to calculate the payoff for
    * @param popStrategies the strategies for this client's group
    * @param matchPopStrategies the match strategies for this client's group
    * @param config the current config
    * @return the payoff the the client whose id is passed
    */
    public float getPayoff(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config);
    
    /**
     * Can return null
     * @param id
     * @param percent
     * @param popStrategies
     * @param matchPopStrategies
     * @return 
     */
    public float[] getPopStrategySummary(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies);
     /**
      * Can return null
      * @param id
      * @param percent
      * @param popStrategies
      * @param matchPopStrategies
      * @return 
      */
    public float[] getMatchStrategySummary(
            int id, float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies);
     /**
      * Called to configure this payoff function with the current config
      * @param config
      * @throws edu.ucsc.leeps.fire.config.BaseConfig.ConfigException 
      */
    public void configure(Config config) throws BaseConfig.ConfigException;
}
