package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.client.Client;
import java.awt.Color;
import processing.core.PApplet;

public class Marker extends Sprite {

    protected float R, G, B, alpha;
    protected float outline;
    protected float diameter;
    protected float largeDiameter;
    protected String label1;
    protected String label2;
    protected FPoint labelOrigin;
    protected boolean grabbed;
    protected boolean enlarged;
    protected LabelMode labelMode;
    protected DrawMode drawMode;
    protected Shape shape;

    /**
     * Creates a list of possible modes to set label position.
     */
    public enum LabelMode {

        Center, Top, Right, Bottom, Left
    };

    public enum DrawMode {

        Filled, Outline, FillOutline, Target,
    }

    public enum Shape {

        Circle, Square,
    }

    public Marker(Sprite parent, float x, float y, boolean visible, float diameter) {
        super(parent, x, y, (int) diameter, (int) diameter);
        this.visible = visible;
        this.diameter = diameter;
        largeDiameter = diameter * 1.5f;

        R = 0;
        G = 0;
        B = 0;
        outline = 0;
        alpha = 255;

        labelMode = LabelMode.Center;
        drawMode = DrawMode.Filled;
        shape = Shape.Circle;

        label1 = null;
        label2 = null;

        grabbed = false;
        enlarged = false;

        labelOrigin = getTranslation(origin);
    }

    /**
     * Sets transparency.
     * @param alpha transparency
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * Sets color using red, green, blue color scheme.Uses floating points to
     * determine values. Doesn't include transparency.
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    public void setColor(float r, float g, float b) {
        R = r;
        G = g;
        B = b;
        if (drawMode == DrawMode.FillOutline) {
            if (R > 200 || G > 200 || B > 200
                    || R + G + B > 300) {
                outline = 0;
            } else {
                outline = 255;
            }
        }
    }

    /**
     * Uses floating point values to set color and transparency. Uses red, green,
     * blue color scheme.
     *
     * @param r red value
     * @param g green value
     * @param b blue value
     * @param a transparency value
     */
    public void setColor(float r, float g, float b, int a) {
        R = r;
        G = g;
        B = b;
        alpha = a;
        if (drawMode == DrawMode.FillOutline) {
            if (R > 200 || G > 200 || B > 200
                    || R + G + B > 300) {
                outline = 0;
            } else {
                outline = 255;
            }
        }
    }

    /**
     * Uses a single hexadecimal number to determine color and transparency.
     * @param C hexadecimal value for color and transparency
     */
    public void setColor(Color C) {
        R = C.getRed();
        G = C.getGreen();
        B = C.getBlue();
        alpha = C.getAlpha();
        if (drawMode == DrawMode.FillOutline) {
            if (R > 200 || G > 200 || B > 200
                    || R + G + B > 300) {
                outline = 0;
            } else {
                outline = 255;
            }
        }
    }

    /**
     * Creates a label for Label 1.
     * @param newLabel new string
     */
    public void setLabel(String newLabel) {
        label1 = newLabel;
    }

    /**
     * Sets the value for label 1.Writes to accuracy of 2 decimal places with a
     * minimum of 3 characters.
     *
     * @param newLabel
     */
    public void setLabel(float newLabel) {
        label1 = String.format("%3.2f", newLabel);
    }

    /**
     * Multiplies setLabel by 100 to create percentage. Assumes that setLabel
     * is between zero and one. Has a minimum of three numbers with an accuracy
     * of zero decimal places. In other words, truncates to the nearest percent.
     * Adds a percent sign to the string.
     *
     * @param newLabel creates a new label
     */
    public void setLabelPercent(float newLabel) {
        label1 = String.format("%3.0f%%", newLabel * 100);
    }

    /**
     * Sets values for labels 1 and 2. Writes to an accuracy of two decimal
     * places with a minimum of three characters.
     *
     * @param newLabel1 label 1
     * @param newLabel2 label 2
     */
    public void setLabel(float newLabel1, float newLabel2) {
        label1 = String.format("%3.2f", newLabel1);
        label2 = String.format("%3.2f", newLabel2);
    }

    /**
     * Creates label positions. Center is at the origin of the label. Top is
     * above the origin. Right is to the right of the origin. Bottom is below
     * the origin. Left is to the left. Variances are all by radius + 8.
     * @param position position of label
     */
    public void setLabelMode(LabelMode position) {
        this.labelMode = position;
        switch (position) {
            case Center:
                labelOrigin.x = origin.x;
                labelOrigin.y = origin.y;
                break;
            case Top:
                labelOrigin.x = origin.x;
                labelOrigin.y = origin.y - diameter - 8;
                break;
            case Right:
                labelOrigin.x = origin.x + diameter + 8;
                labelOrigin.y = origin.y;
                break;
            case Bottom:
                labelOrigin.x = origin.x;
                labelOrigin.y = origin.y + diameter + 8;
                break;
            case Left:
                labelOrigin.x = origin.x - diameter - 8;
                labelOrigin.y = origin.y;
                break;
        }
    }

    public void setDrawMode(DrawMode mode) {
        drawMode = mode;
        if (drawMode == DrawMode.FillOutline) {
            if (R > 200 || G > 200 || B > 200
                    || R + G + B > 300) {
                outline = 0;
            } else {
                outline = 255;
            }
        }
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void grab() {
        grabbed = true;
    }

    /**
     * Sets grabbed to false.
     */
    public void release() {
        grabbed = false;
    }

    /**
     * If marker is grabbed, return grabbed.
     * @return grabbed
     */
    public boolean isGrabbed() {
        return grabbed;
    }

    /**
     * When enlarged, make radius 1.5 times larger, and set enlarged to true.
     */
    public void enlarge() {
        width = (int) largeDiameter;
        height = (int) largeDiameter;
        enlarged = true;
    }

    /**
     * When shrunk, set radius to radius, and set enlarged to false.
     */
    public void shrink() {
        width = (int) diameter;
        height = (int) diameter;
        enlarged = false;
    }

    /**
     * If the marker is enlarged, return enlarged.
     * @return enlarged
     */
    public boolean isEnlarged() {
        return enlarged;
    }

    /**
     * Update origin based on x and y coordinates, and set labelMode to mode.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void update(float x, float y) {
        origin.x = x;
        origin.y = y;
        setLabelMode(labelMode);
    }

    public void draw(Client applet) {
        if (!visible) {
            return;
        }
        applet.rectMode(PApplet.CENTER);
        if (label1 != null) {
            drawLabels(applet);
        }

        if (drawMode == DrawMode.Filled) {
            applet.noStroke();
        } else {
            applet.stroke(outline);
            applet.strokeWeight(1);
        }

        if (drawMode == DrawMode.Outline
                || drawMode == DrawMode.Target) {
            applet.noFill();
        } else {
            applet.fill(R, G, B, alpha);
        }

        applet.ellipseMode(Client.CENTER);
        if (!enlarged) {
            if (shape == Shape.Circle) {
                applet.ellipse(origin.x, origin.y, diameter, diameter);
            } else if (shape == Shape.Square) {
                applet.rect(origin.x, origin.y, diameter, diameter);
            }
        } else {
            if (shape == Shape.Circle) {
                applet.ellipse(origin.x, origin.y, largeDiameter, largeDiameter);
            } else if (shape == Shape.Square) {
                applet.rect(origin.x, origin.y, largeDiameter, largeDiameter);
            }
            applet.ellipse(origin.x, origin.y, largeDiameter, largeDiameter);
        }
        if (drawMode == DrawMode.Target) {
            applet.strokeWeight(2);
            applet.line(origin.x - diameter, origin.y, origin.x + diameter, origin.y);
            applet.line(origin.x + .5f, origin.y - diameter, origin.x + .5f, origin.y + diameter);
        }
    }

    protected void drawLabels(Client applet) {
        applet.textSize(14);
        applet.textFont(applet.size14);
        float textWidth = applet.textWidth(label1);
        if (label2 != null) {
            applet.textFont(applet.size14Bold);
            textWidth += applet.textWidth(label2);
        }
        if (textWidth > 16 && labelMode == LabelMode.Left) {
            labelOrigin.x = origin.x - diameter - textWidth / 2;
        } else if (textWidth > 16 && labelMode == LabelMode.Right) {
            labelOrigin.x = origin.x + diameter + textWidth / 2;
        }
        float textHeight = applet.textAscent() + applet.textDescent();
        applet.rectMode(Client.CENTER);
        applet.fill(255);
        applet.noStroke();
        applet.rect(labelOrigin.x, labelOrigin.y, textWidth + 15, textHeight);
        applet.textAlign(Client.CENTER, Client.CENTER);
        applet.fill(0);
        if (label1 != null && label2 != null) {
            float label1Width = applet.textWidth(label1);
            applet.textFont(applet.size14Bold);
            applet.text(label1, labelOrigin.x - label1Width / 2, labelOrigin.y);
            applet.textFont(applet.size14);
            applet.text(", " + label2, labelOrigin.x + label1Width / 2, labelOrigin.y);
        } else if (label1 != null) {
            applet.textFont(applet.size14);
            applet.text(label1, labelOrigin.x, labelOrigin.y);
        }
    }
}
