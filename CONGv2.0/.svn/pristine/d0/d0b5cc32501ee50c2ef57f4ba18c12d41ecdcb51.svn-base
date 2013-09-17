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

public abstract class Sprite implements Serializable {

    public transient FPoint origin;
    public transient int width, height;
    public transient Sprite parent;
    public boolean visible;
    
    public Sprite() {
        origin = new FPoint(0, 0);
    }

    public Sprite(Sprite parent, float x, float y, int width, int height) {
        origin = new FPoint(x, y);
        this.parent = parent;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Client applet);

    public boolean isHit(float x, float y) {
        FPoint screenLocation = getTranslation(origin);
        return x >= screenLocation.x && x <= screenLocation.x + width
                && y >= screenLocation.y && y <= screenLocation.y + height;
    }

    public boolean circularIsHit(float x, float y) {
        FPoint screenLocation = getTranslation(origin);
        return Client.dist(x, y, screenLocation.x, screenLocation.y) <= width;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public FPoint getTranslation(FPoint newOrigin) {
        if (parent == null) {
            return newOrigin;
        } else {
            FPoint translation = new FPoint(parent.origin.x + newOrigin.x, parent.origin.y + newOrigin.y);
            return parent.getTranslation(translation);
        }
    }
}
