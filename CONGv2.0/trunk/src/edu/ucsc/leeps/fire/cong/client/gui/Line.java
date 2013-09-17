/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Sprite describing a line on a graph with a set of points.  Used by the chart 
 * sprite as the lines in the chart.
 * @see edu.ucsc.leeps.fire.cong.client.gui.Chart
 * @see edu.ucsc.leeps.fire.cong.client.gui.Sprite
 */
public class Line extends Sprite implements Serializable {
    
    /**
     * Enumerated type describing the different ways a Line can be drawn.  Solid
     * draws and solid line, Dashed draw a dashed line, Shaded shades is all of 
     * the area underneath the line, and EndPoint is unsupported.
     */
    public enum Mode {
        Solid, EndPoint, Dashed, Shaded,
    };
    /**the thickness or weight of this line*/
    public float weight;
    /**rgba values used when drawing this line*/
    public int r, g, b, alpha;
    /**an enumerated type describing how this line is to be drawn*/
    public Mode mode;
    /**whether or not there are gaps in between the lines points, as in a discrete treatment*/
    public boolean stepFunction;
    private transient ArrayList<Integer> ypoints;
    private transient int xMax;
    private transient Config config;
    private transient int maxLength;
    
    /**
     * Constructor.  Initializes line to solid mode, rgba values of 0, and a weight
     * of 1.
     */
    public Line() {
        super(null, 0, 0, 0, 0);
        r = g = b = 0;
        alpha = 255;
        weight = 1.0f;
        mode = Mode.Solid;
    }
    
    /**
     * Constructor
     * @param parent
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public Line(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        visible = false;
        ypoints = new ArrayList<Integer>();
        xMax = 0;
    }
    
    /**
     * Sets this lines values to the values of the line passed
     * @param config
     * @param lconfig 
     */
    public void configure(Config config, Line lconfig) {
        this.visible = true;
        this.r = lconfig.r;
        this.g = lconfig.g;
        this.b = lconfig.b;
        this.alpha = lconfig.alpha;
        this.weight = lconfig.weight;
        this.mode = lconfig.mode;
        stepFunction = FIRE.client.getConfig().subperiods != 0;
        this.config = config;
    }
    
    /**
     * Configures this line
     * @param red r rgba value
     * @param green g rgba value
     * @param blue b rgba value
     * @param alpha alpha value
     * @param weight the width or weight of the line
     * @param mode the draw mode of the line
     */
    public void configure(int red, int green, int blue, int alpha, float weight, Mode mode ) {
        this.visible = true;
        this.r = red;
        this.g = green;
        this.b = blue;
        this.alpha = alpha;
        this.weight = weight;
        this.mode = mode;
    }
    
    /**
     * Adds a point to the line
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setPoint(int x, int y) {
        int maxWidth;
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            maxWidth = (int) (FIRE.client.getConfig().indefiniteEnd.percentToDisplay * width);
        } else {
            maxWidth = width;
        }

        while (xMax < x) {
            ypoints.add(y);
            if (ypoints.size() > maxWidth) {
                ypoints.remove(0);
            }
            xMax++;
        }
    }

    private void drawSolidLine(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        for (int x = 1; x < ypoints.size(); x++) {
            if (stepFunction) {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x - 1));
                applet.line(x, ypoints.get(x - 1), x, ypoints.get(x));
                applet.line(x - 1, ypoints.get(x), x, ypoints.get(x));
            } else {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x));
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x - 1));
            }
        }
    }

    private void drawDashedLine(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        for (int x = 1; x < ypoints.size(); x++) {
            if (x % 4 != 0) {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x));
            }
        }
    }
    
    /**
     * Unsupported.
     * @param applet
     * @throws UnsupportedOperationException
     */
    private void drawLineEndPoint(Client applet) {
        throw new UnsupportedOperationException();
    }

    private void drawShadedArea(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        if (stepFunction) {
            applet.beginShape();
            for (int x = 0; x < ypoints.size(); x++) {
                if (x > 0 && Math.abs(ypoints.get(x) - ypoints.get(x - 1)) > 1) {
                    applet.vertex(x, ypoints.get(x - 1));
                }
                applet.vertex(x, ypoints.get(x));
            }
            if (ypoints.size() > 0) {
                applet.vertex(ypoints.size(), ypoints.get(ypoints.size() - 1));
            }
            applet.vertex(ypoints.size(), height);
            applet.vertex(0, height);
            applet.endShape();
        } else {
            for (int x = 0; x < ypoints.size(); x++) {
                applet.line(x, ypoints.get(x), x, height);
            }
        }
    }
    
    /**
     * Unsupported
     * @param applet
     * @param cost 
     * @throws UnsupportedOperationException
     */
    public synchronized void drawCostArea(Client applet, float cost) {
        if (cost == 0) {
            return;
        }
        throw new UnsupportedOperationException();
    }
    
    public synchronized void draw(Client applet) {
        if (!visible) {
            return;
        }
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        switch (mode) {
            case Solid:
                drawSolidLine(applet);
                break;
            case Dashed:
                drawDashedLine(applet);
                break;
            case EndPoint:
                drawLineEndPoint(applet);
                break;
            case Shaded:
                drawShadedArea(applet);
                break;
        }
        applet.popMatrix();
    }
    
    /**
     * Clears this lines points.
     */
    public synchronized void clear() {
        xMax = 0;
        ypoints.clear();
    }
    
    /**
     * adds a point to the line using x and y payoff values
     * @param x
     * @param y 
     */
    public synchronized void addPayoffPoint(float x, float y) {
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            x = (x * FIRE.client.getConfig().length)
                    / (FIRE.client.getConfig().indefiniteEnd.displayLength / FIRE.client.getConfig().indefiniteEnd.percentToDisplay);
        }
        setPoint(
                Math.round(width * x),
                Math.round(height * (1 - ((y - FIRE.client.getConfig().payoffFunction.getMin()) / (FIRE.client.getConfig().payoffFunction.getMax() - FIRE.client.getConfig().payoffFunction.getMin())))));
    }
    
    /**
     * adds a point to the line using x and y strategy values
     * @param x
     * @param y 
     */
    public synchronized void addStrategyPoint(float x, float y) {
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            x = (x * FIRE.client.getConfig().length)
                    / (FIRE.client.getConfig().indefiniteEnd.displayLength / FIRE.client.getConfig().indefiniteEnd.percentToDisplay);
        }
        setPoint(
                Math.round(width * x),
                Math.round(height * (1 - y)));
    }
}
