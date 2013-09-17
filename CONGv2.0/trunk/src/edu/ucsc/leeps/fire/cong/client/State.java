/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class defines the state of the client.  It holds the clients id, strategies,
 * and match strategies as well as the strategy changer instance that the current 
 * configuration calls for.
 * 
 * @see edu.ucsc.leeps.fire.cong.client.Client
 * @author jpettit
 */
public class State {
    
    public int id;
    public int turn=1;
    public boolean turnUpdated=false;
    public int subperiod;
    public volatile float currentPercent;
    public ConcurrentHashMap<Integer, float[]> strategies, matchStrategies;
    public Map<Integer, float[]> previousStrategies;
    public final List<Strategy> strategiesTime;
    public volatile float subperiodPayoff, subperiodMatchPayoff;
    public float[] target;
    public StrategyChanger strategyChanger;
    public long periodStartTime;
    public float totalPoints, periodPoints;
    
    /**
     * Initializer. Initialize the state with the current strategy changer.  Called
     * by the method setup in the class client.
     * @param changer the strategy changer to be used
     */
    public State(StrategyChanger changer) {
        this.strategyChanger = changer;
        strategiesTime = new LinkedList<Strategy>();
        strategies = new ConcurrentHashMap<Integer, float[]>();
        matchStrategies = new ConcurrentHashMap<Integer, float[]>();
    }
    
    /**
     * Called at the start of a new period to initialize the states values.  Clears
     * the data structures, sets a new start time, and resets counters to zero.
     */
    public void startPeriod() {
        periodStartTime = System.nanoTime();
        subperiod = 0;
        currentPercent = 0;
        synchronized (strategiesTime) {
            strategiesTime.clear();
        }
        strategies.clear();
        matchStrategies.clear();
        setMyStrategy(FIRE.client.getConfig().initialStrategy);
        updatePoints();
    }
    
    /**
     * Called at the end of a period.  Ensures that the points are updated at 
     * the end of a period.
     */
    public void endPeriod() {
        currentPercent = 1f;
        updatePoints();
    }

    /**
     * Updates the client's points.
     */
    public void updatePoints() {
        totalPoints = FIRE.client.getTotalPoints();
        if (Client.state.currentPercent >= 1) {
            periodPoints = FIRE.client.getPeriodPoints();
        } else {
            try {
                synchronized (Client.state.strategiesTime) {
                    periodPoints = PayoffUtils.getTotalPayoff(
                            Client.state.id, Client.state.currentPercent, Client.state.strategiesTime, FIRE.client.getConfig());
                }
            } catch (NullPointerException ex) {
            }
        }
    }
    
    /**
     * Sets the current strategy of the client
     * @param strategy the new strategy
     */
    public void setMyStrategy(float[] strategy) {
        if(subperiod==0 || turn==id){
            float[] s = new float[strategy.length];
            System.arraycopy(strategy, 0, s, 0, s.length);
            strategies.put(id, s);
            target = new float[strategy.length];
            System.arraycopy(strategy, 0, target, 0, target.length);
        }
    }
    
    /**
     * Returns the clients current strategy
     * @return the clients current strategy
     */
    public float[] getMyStrategy() {
        return strategies.get(id);
    }

    /**
     * Sets the current target strategy.
     * @param target the new target strategy
     * @param config the current config
     */
    public void setTarget(float[] target, Config config) {
        if(subperiod==0 || turn==id){
            Client.state.target = target;
            if (!Float.isNaN(config.grid)) {
                for (int i = 0; i < Client.state.target.length; i++) {
                    float r = Client.state.target[i] % config.grid;
                    if (r > config.grid / 2f) {
                        Client.state.target[i] -= r;
                        Client.state.target[i] += config.grid;
                    } else {
                        Client.state.target[i] -= r;
                    }
                }
            }
        }
    }
    
    /**
     * Sets the current target.  The target is used by treatments in which strategies
     * don't change instantly
     * @param target 
     */
    public void setTarget(float target){
        if(subperiod==0 || turn==id){
            Client.state.target[0] = target;
        }
    }
    
    /**
     * Updates the current strategies.  Call passed down from the server.
     * @param whoChanged the id of the client that changed the strategies
     * @param strategies the new strategies
     * @param timestamp the time of this update
     */
    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies, long timestamp) {
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(timestamp, copyMap(strategies), copyMap(matchStrategies)));
        }
        this.strategies = new ConcurrentHashMap<Integer, float[]>(strategies);
    }
    
    /**
     * Updates the current match strategies. Call passed down from the server.
     * @param whoChanged id of the client that changed its strategy
     * @param matchStrategies the new match strategies
     * @param timestamp the time this update occurred
     */
    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies, long timestamp) {
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(timestamp, copyMap(strategies), copyMap(matchStrategies)));
        }
        this.matchStrategies = new ConcurrentHashMap<Integer, float[]>(matchStrategies);
    }
    
    /**
     * Ends the current sub-period, updating the strategies and the match strategies.
     * Call passed down from the server.
     * @param subperiod the ending sub-period
     * @param strategies the sub-period's strategies
     * @param matchStrategies the sub-period's match strategies
     */
    public void endSubperiod(int subperiod, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies) {
        this.subperiod = subperiod;
        this.strategies = new ConcurrentHashMap<Integer, float[]>(strategies);
        this.matchStrategies = new ConcurrentHashMap<Integer, float[]>(matchStrategies);
        synchronized (strategiesTime) {
            strategiesTime.add(new Strategy(subperiod, copyMap(strategies), copyMap(matchStrategies)));
        }
        Config config = FIRE.client.getConfig();
        this.subperiodPayoff = config.payoffFunction.getPayoff(id, subperiod, strategies, matchStrategies, config);
    }
    
    /**
     * Returns a strategy map with the passed fake strategy instead of that of the 
     * client whose id was passed
     * @param id the clients whose strategy is to be replaced with the fake one
     * @param strategy the fake strategy
     * @return a strategy map with the fake strategy
     */
    public Map<Integer, float[]> getFictitiousStrategies(int id, float[] strategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            if (i == id) {
                fake.put(i, strategy);
            } else {
                fake.put(i, strategies.get(i));
            }
        }
        return fake;
    }
    
    /**
     * Returns a strategy map with the passed fake strategy instead of that of this
     * client
     * @param strategy the fake strategy
     * @return a strategy map with the fake strategy
     */
    public Map<Integer, float[]> getFictitiousStrategies(float[] strategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            if (i == id) {
                fake.put(i, strategy);
            } else {
                fake.put(i, strategies.get(i));
            }
        }
        return fake;
    }

    /**
     * Returns a map with the id of each client in this client's group mapped to
     * the passed strategy
     * @param matchStrategy the strategy mapped to all the id's 
     * @return a map with the id of each client in this client's group mapped to
     * the passed strategy
     */
    public Map<Integer, float[]> getFictitiousMatchStrategies(float[] matchStrategy) {
        Map<Integer, float[]> fake = new HashMap<Integer, float[]>();
        for (int i : strategies.keySet()) {
            fake.put(i, matchStrategy);
        }
        return fake;
    }
    
    /**
     * Returns whether it is this clients turn or not
     * @return 
     */
    public boolean hasTurn(){
        return (subperiod==0 || turn == id );
    }
    
    /**
     * Convenience class to hold all of the data for the strategies at a given time.
     */
    public static class Strategy {

        public final long timestamp;
        public final Map<Integer, float[]> strategies;
        public final Map<Integer, float[]> matchStrategies;

        public Strategy(long timestamp, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies) {
            this.timestamp = timestamp;
            this.strategies = strategies;
            this.matchStrategies = matchStrategies;
        }
        
        /**
         * Returns whether or not this strategy update was late or delayed
         * @return whether or not this strategy update was late or delayed
         */
        public boolean delayed() {
            Config config = FIRE.client.getConfig();
            if (config.subperiods == 0) {
                return Client.state.currentPercent < 1 && ((1e9 * (Client.state.currentPercent * config.length)) - timestamp) < 1e9 * config.infoDelay;
            } else {
                return Client.state.subperiod < config.subperiods && Client.state.subperiod - timestamp < config.infoDelay;
            }
        }
    }

    /**
     * Method for cloning maps of arrays
     * @param m map to clone
     * @return a new map with a set of new array containing the same values as 
     * those in the original map.
     */
    public static Map<Integer, float[]> copyMap(Map<Integer, float[]> m) {
        Map<Integer, float[]> copy = new HashMap<Integer, float[]>();
        for (int id : m.keySet()) {
            float[] a = m.get(id);
            if (a != null) {
                float[] f = new float[a.length];
                System.arraycopy(a, 0, f, 0, f.length);
                copy.put(id, f);
            }
        }
        return copy;
    }
    
}
