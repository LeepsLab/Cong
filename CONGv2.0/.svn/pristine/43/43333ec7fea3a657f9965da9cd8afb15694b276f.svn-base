/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.ThresholdPayoffFunction;
import processing.core.PApplet;

/**
 *
 * @author jpettit
 */
public class ChartLegend extends Sprite implements Configurable<Config> {

    private Config config;
    private Line youLine, otherLine, threshold;

    /**
     * Creates the chart legend.
     * @param parent
     * @param x x-coordinate
     * @param y y-coordinate
     * @param width width of legend
     * @param height height of legend
     */
    public ChartLegend(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        FIRE.client.addConfigListener(this);
    }

    /**
     * If not null, label lines. Your line is labeled "You" and counterpart is
     * labeled "Other". THe threshold is labeled "Threshold". Width is 40 plus
     * width of text "You" and "Other". If a threshold payoff function is being
     * used,add another 18 and width of text "Threshold".
     *
     * Translate the applet to negative width.
     *
     * Align the text at the left of the screen in the middle, using a stroke
     * weight of 10.
     *
     * Set colors for youLine. Draw a line from 
     * @param applet
     */
    @Override
    public void draw(Client applet) {

        if (!visible) {
            return;
        }

        applet.pushMatrix();
        applet.translate(origin.x, origin.y);

        if (youLine != null && otherLine != null) {
            String youLabel = "You";
            String otherLabel = "Other";
            String threshLabel = "Threshold";
            float w1 = applet.textWidth(youLabel);
            float w2 = applet.textWidth(otherLabel);
            float w3 = applet.textWidth(threshLabel);
            width = (int) (4 + 10 + 4 + w1 + 4 + 10 + 4 + w2 + 4);
            if (config.payoffFunction instanceof ThresholdPayoffFunction) { //payoff function dependent
                width += (int) (4 + 10 + 4 + w3);
            }
            applet.translate(-width, 0);

            applet.strokeWeight(10);
            applet.textAlign(PApplet.LEFT, PApplet.CENTER);

            applet.stroke(youLine.r, youLine.g, youLine.b, youLine.alpha);
            applet.line(4, height / 2f, 4 + 10, height / 2f);
            applet.text(youLabel,
                    width + 4 + 10 + 4 - width,
                    height / 2f);

            applet.stroke(otherLine.r, otherLine.g, otherLine.b, otherLine.alpha);
            applet.line(4 + 10 + 4 + w1 + 4, height / 2f, 4 + 10 + 4 + w1 + 4 + 10, height / 2f);
            applet.text(otherLabel,
                    width + 4 + 10 + 4 + w1 + 4 + 10 + 4 - width,
                    height / 2f);

            if (config.payoffFunction instanceof ThresholdPayoffFunction) { //payoff function dependent
                applet.stroke(threshold.r, threshold.g, threshold.b);
                applet.line(4 + 10 + 4 + w1 + 4 + 10 + 4 + w2 + 4, height / 2f,
                        4 + 10 + 4 + w1 + 4 + 10 + 4 + w2 + 4 + 10, height / 2f);
                applet.text(threshLabel,
                        width + 4 + 10 + 4 + w1 + 4 + 10 + 4 + w2 + 4 + 10 + 4 - width,
                        height / 2f);
            }

            applet.strokeWeight(2);
        } else {
            width = 100;
            applet.translate(-width, 0);
        }

        height = (int) (9 + applet.textAscent() + applet.textDescent());
        applet.noFill();
        applet.stroke(0);
        applet.rect(0, 0, width, height);

        applet.popMatrix();
    }

    public void configChanged(Config config) {
        this.config = config;
        youLine = config.yourPayoff;
        otherLine = config.matchPayoff;
        threshold = config.thresholdLine;
    }
}
