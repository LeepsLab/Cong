/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client.gui;

/**
 * Convenience class for holding coordinate data.
 * @author Alex
 */
public class FPoint {
    /**x and y coordinates*/
    public float x, y;
    public boolean visible;
    
    /**
     * Constructor.  Initializes point to (0,0)
     */
    public FPoint() {
        x = 0;
        y = 0;
        visible = true;
    }
    
    /**
     * Constructor.
     * @param X x coordinate
     * @param Y y coordinate
     */
    public FPoint(float X, float Y) {
        x = X;
        y = Y;
        visible = true;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
