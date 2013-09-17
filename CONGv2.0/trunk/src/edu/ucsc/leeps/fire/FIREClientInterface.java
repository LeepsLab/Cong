/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire;

/**
 * This is the interface used by the client controller to interact with the client
 * @author jpettit
 * @see edu.ucsc.leeps.fire.cong.client.Client
 * @see edu.ucsc.leeps.fire.client.ClientController
 */
public interface FIREClientInterface {

    public boolean readyForNextPeriod();

    public void startPrePeriod();

    public void startPeriod();

    public void endPeriod();

    public void tick(int elapsedMillis);
}
