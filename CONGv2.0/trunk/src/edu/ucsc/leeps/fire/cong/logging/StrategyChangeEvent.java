/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.logging;

import edu.ucsc.leeps.fire.logging.LogEvent;

/**
 * This class is used to wrap the data concerning a strategy change.  It is used
 * by the server class to put strategy changes into a queue that is read off of by
 * a thread.
 * @see edu.ucsc.leeps.fire.cong.server.Server
 */
public class StrategyChangeEvent {

    public long timestamp;
    public int id;
    public float[] newStrategy;
    public float[] targetStrategy;
    
    /**
     * Constructor
     * @param timestamp
     * @param id
     * @param newStrategy
     * @param targetStrategy 
     */
    public StrategyChangeEvent(long timestamp, int id, float[] newStrategy, float[] targetStrategy) {
        this.timestamp = timestamp;
        this.id = id;
        this.newStrategy = newStrategy;
        this.targetStrategy = targetStrategy;
    }
}
