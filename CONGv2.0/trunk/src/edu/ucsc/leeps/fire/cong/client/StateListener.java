/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client;

/**
 * This is an interface for a class that listens for changes to a client's state
 * @author jpettit
 */
public interface StateListener {

    public void stateChanged(State newState);
}
