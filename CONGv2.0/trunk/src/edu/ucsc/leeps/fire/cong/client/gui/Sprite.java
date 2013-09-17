/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.client.Client;
import java.io.Serializable;

/**
 * Abstract class for all drawable elements in the client gui.
 */
public abstract class Sprite implements Serializable {
    
    /**the origin of the sprite as its upper left hand corner*/
    public transient FPoint origin;
    /**the dimensions of this sprite*/
    public transient int width, height;
    /**this sprite parent if it has one*/
    public transient Sprite parent;
    /**whether or not this sprite is visisble*/
    public boolean visible;
    
    /**
     * Constructor.  Initializes origin to (0,0)
     */
    public Sprite() {
        origin = new FPoint(0, 0);
    }
    
    /**
     * Constructor
     * @param parent a parent sprite
     * @param x starting x coordinate relative to the parent's origin
     * @param y starting y coordinate relative to the parent's origin
     * @param width the width of this sprite
     * @param height the height of this sprite
     */
    public Sprite(Sprite parent, float x, float y, int width, int height) {
        origin = new FPoint(x, y);
        this.parent = parent;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Draw function for this sprite
     * @param applet the client.
     */
    public abstract void draw(Client applet);
    
    /**
     * Returns whether or not the passed coordinates are within this sprite's bounding
     * box
     * @param x x coordinate
     * @param y y coordinate
     * @return whether or not the passed coordinates are within this sprite's bounding
     * box
     */
    public boolean isHit(float x, float y) {
        FPoint screenLocation = getTranslation(origin);
        return x >= screenLocation.x && x <= screenLocation.x + width
                && y >= screenLocation.y && y <= screenLocation.y + height;
    }
    
    /**
     * Returns whether or not the passed coordinates are within this sprite's bounding
     * box
     * @param x x coordinate
     * @param y y coordinate
     * @return whether or not the passed coordinates are within this sprite's bounding
     * box
     */
    public boolean circularIsHit(float x, float y) {
        FPoint screenLocation = getTranslation(origin);
        return Client.dist(x, y, screenLocation.x, screenLocation.y) <= width;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Returns a new FPoint with the values of the passed one translated to absolute
     * coordinates, assuming that the passed point is relative to this sprite's parent's
     * origin.  If this sprite has no parent the passed point is returned.
     * @param newOrigin the point to be translated;
     * @return 
     */
    public FPoint getTranslation(FPoint newOrigin) {
        if (parent == null) {
            return newOrigin;
        } else {
            FPoint translation = new FPoint(parent.origin.x + newOrigin.x, parent.origin.y + newOrigin.y);
            return parent.getTranslation(translation);
        }
    }
}
