package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import java.awt.Color;
import processing.core.PImage;

public class Slider {
    /**
     * Creates an enum of types of alignment. Can be either horizontal or vertical.
     */
    public static enum Alignment { Horizontal, Vertical };

    private final int HANDLE_WIDTH = 10;
    private final int HANDLE_HEIGHT = 27;
    private final String FORMAT = "%4.2f";
    private final float ERROR_MARGIN = .01f;
    private boolean visible;
    private Alignment align;
    private float sliderStart, sliderEnd, sliderLine;
    private float length;
    private float sliderPos;
    private float stratValue;
    private boolean ghosting;
    private float ghostPos;
    private float ghostValue;
    private float R, G, B;
    private boolean colorChanged;
    private PImage texture, ghostTexture;
    private String label, stratLabel, ghostLabel;
    private boolean grabbed;
    private boolean ghostGrabbed;
    private float maxValue;

    // Constructor ////////////////
    public Slider(Client applet, Alignment align, float start, float end, float line, Color C,
            String label, float maxValue) {
        if (end < start) {
            throw new IllegalArgumentException("Invalid Slider coordinates " + "(end < start)");
        }

        this.align = align;

        this.sliderStart = start;
        this.sliderEnd = end;
        this.sliderLine = line;

        R = C.getRed();
        G = C.getGreen();
        B = C.getBlue();
        colorChanged = false;
        textureSetup(applet);
        length = end - start;
        if (align == Alignment.Horizontal) {
            sliderPos = start;
            ghostPos = start;
        } else {
            sliderPos = end;
            ghostPos = end;
        }
        stratValue = 0;

        ghosting = false;
        ghostValue = 0;

        this.label = label;
        stratLabel = String.format(FORMAT, stratValue);
        ghostLabel = String.format(FORMAT, ghostValue);

        grabbed = false;
        ghostGrabbed = false;
        this.maxValue = maxValue;
    }

    // Methods ///////////////////
    // Access ///////
    /**
     * Gets strategy value as based on position of slider.
     *
     * @return  stratValue
     */
    public float getStratValue() {
        return stratValue;
    }

    /**
     * Gets value of ghost.
     *
     * @return ghostValue
     */
    public float getGhostValue() {
        return ghostValue;
    }

    /**
     * Gets slider position.
     *
     * @return sliderPos
     */
    public float getSliderPos() {
        return sliderPos;
    }

    /**
     * Gets ghost position
     * @return ghostPos
     */
    public float getGhostPos() {
        return ghostPos;
    }

    /**
     * Gets length of slider.
     *
     * @return length
     */
    public float getLength() {
        return length;
    }

    /**
     * Determines if slider is grabbed.
     *
     * @return grabbed
     */
    public boolean isGrabbed() {
        return grabbed;
    }

    /**
     * Determines if ghost is grabbed.
     * @return ghostGrabbed
     */
    public boolean isGhostGrabbed() {
        return ghostGrabbed;
    }

    /**
     * determines whether the alignment is horizontal or vertical.
     * @return align
     */
    public Alignment getAlignment() {
        return align;
    }

    // Manipulation ///////
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setColor(Color c) {
        R = c.getRed();
        G = c.getGreen();
        B = c.getBlue();
        colorChanged = true;
    }

    public void setStratValue(float newStrat) {
        if (newStrat > maxValue + ERROR_MARGIN || newStrat < -ERROR_MARGIN) {
            throw new RuntimeException("Error: strategy value out of range.");
        }
        
        stratValue = newStrat;

        if (align == Alignment.Horizontal) {
            sliderPos = sliderStart + length * stratValue / maxValue;
        } else {
            sliderPos = sliderEnd - length * stratValue / maxValue;
        }
        stratLabel = String.format(FORMAT, stratValue);
    }

    /**
     * Moves slider.
     * The slider remains at the start if the position is less than the start
     * value. Likewise, the slider remains at the end if the slider position is
     * greater than the end value. Otherwise ,the slider is at some position
     * between the starting and ending values.
     *
     * If the slider is aligned horizontally, then the strategy value is the max
     * value multiplied by the quotient of the difference of the slider position
     * and the start value and the length. If the slider is vertically aligned,
     * the strategy value is the maximum value multiplied by the quotient of
     * the difference  of the end value of the slider and the slider position and
     * the length.
     * @param pos position of the slider
     */
    public void moveSlider(float pos) {
        if (pos < sliderStart) {
            sliderPos = sliderStart;
        } else if (pos > sliderEnd) {
            sliderPos = sliderEnd;
        } else {
            sliderPos = pos;
        }

        if (align == Alignment.Horizontal) {
            stratValue = maxValue * (sliderPos - sliderStart) / length;
        } else {
            stratValue = maxValue * (sliderEnd - sliderPos) / length;
        }
        stratLabel = String.format(FORMAT, stratValue);
    }

    /**
     * Sets ghosting to true.
     */
    public void showGhost() {
        ghosting = true;
    }

    /**
     * Sets ghosting to false.
     */
    public void hideGhost() {
        ghosting = false;
    }

    /**
     * Sets ghost value. If the alignment is horizontal, the ghost position is
     * the starting value of the slider plus the quotient of the product of the
     * length and the ghost value and the max value. Likewise, if the slider is
     * vertically aligned, the ghost position is the ending value of the slider
     * minus the above quotient.
     * The ghost is labeled with the ghost value with some text formatting. 
     * @param ghostVal value of the ghost
     */
    public void setGhostValue(float ghostVal) {
        ghostValue = ghostVal;

        if (align == Alignment.Horizontal) {
            ghostPos = sliderStart + length * ghostValue / maxValue;
        } else {
            ghostPos = sliderEnd - length * ghostValue / maxValue;
        }
        ghostLabel = String.format(FORMAT, ghostValue);
    }

    /**
     * If position is less than slider start, then the ghost remains at slider start.
     * If the position is greater than the slider end, then the ghost position is
     * slider end. otherwise, the ghost value is at some position between the
     * start and end values.
     * If alignment is horizontal, ghost value is max value multiplied by the
     * difference of the ghost position and the slider start divided by the length.
     * If alignment is vertical, the ghost value is the maximum value multiplied
     * by the difference of slider end and the ghost position  and divided by the
     * length.
     * Ghost is labeled with the ghost value using some text format.
     *
     * @param pos position of ghost.
     */
    public void moveGhost(float pos) {
        if (pos < sliderStart) {
            ghostPos = sliderStart;
        } else if (pos > sliderEnd) {
            ghostPos = sliderEnd;
        } else {
            ghostPos = pos;
        }

        if (align == Alignment.Horizontal) {
            ghostValue = maxValue * (ghostPos - sliderStart) / length;
        } else {
            ghostValue = maxValue * (sliderEnd - ghostPos) / length;
        }
        ghostLabel = String.format(FORMAT, ghostValue);
    }

    /**
     * Sets grabbed to true
     */
    public void grab() {
        grabbed = true;
    }

    /**
     * Sets grabbed to false
     */
    public void release() {
        grabbed = false;
    }

    /**
     * Sets ghost grabbed to true.
     */
    public void grabGhost() {
        ghostGrabbed = true;
    }

    /**
     * Sets ghost grabbed to false.
     */
    public void releaseGhost() {
        ghostGrabbed = false;
    }

    /**
     * Sets label
     *
     * @param label slider or ghost label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Uses mouse position to determine if mouse is on handle.
     * @param mouseX() mouse's x-coordinate
     * @param mouseY mouse's y-coordinate
     * @return
     */
    public boolean mouseOnHandle(float mouseX, float mouseY) {
        if (align == Alignment.Horizontal) {
            return (mouseX < sliderPos + HANDLE_WIDTH / 2 &&
                    mouseX > sliderPos - HANDLE_WIDTH / 2 &&
                    mouseY < sliderLine + HANDLE_HEIGHT / 2 &&
                    mouseY > sliderLine - HANDLE_HEIGHT / 2);
        } else {
            return (mouseY < sliderPos + HANDLE_WIDTH / 2 &&
                    mouseY > sliderPos - HANDLE_WIDTH / 2 &&
                    mouseX < sliderLine + HANDLE_HEIGHT / 2 &&
                    mouseX > sliderLine - HANDLE_HEIGHT / 2);
        }
    }

    /**
     * Uses mouse input to determine if mouse is on ghost.
     * @param mouseX() mouse's x-coordinate
     * @param mouseY mouse's y-coordinate
     * @return
     */
    public boolean mouseOnGhost(float mouseX, float mouseY) {
        if (align == Alignment.Horizontal) {
            return (mouseX < ghostPos + HANDLE_WIDTH / 2 &&
                    mouseX > ghostPos - HANDLE_WIDTH / 2 &&
                    mouseY < sliderLine + HANDLE_HEIGHT / 2 &&
                    mouseY > sliderLine - HANDLE_HEIGHT / 2);
        } else {
            return (mouseY < ghostPos + HANDLE_WIDTH / 2 &&
                    mouseY > ghostPos - HANDLE_WIDTH / 2 &&
                    mouseX < sliderLine + HANDLE_HEIGHT / 2 &&
                    mouseX > sliderLine - HANDLE_HEIGHT / 2);
        }
    }

    public void draw(Client applet) {
        if (visible) {
            if (colorChanged) {
                textureSetup(applet);
                colorChanged = false;
            }
            if (align == Alignment.Horizontal) {
                applet.stroke(0);
                applet.strokeWeight(3);
                applet.line(sliderStart, sliderLine, sliderEnd, sliderLine);

                applet.noStroke();
                applet.imageMode(Client.CENTER);
                applet.image(texture, sliderPos, sliderLine);

                applet.fill(0);
                applet.textAlign(Client.LEFT);
                float labelWidth = applet.textWidth(label);
                applet.text(label, sliderStart - labelWidth - 10, sliderLine + 2);
                applet.text(stratLabel, sliderEnd + 10, sliderLine + 2);

                if (ghosting) {
                    applet.image(ghostTexture, ghostPos, sliderLine);

                    applet.fill(120);
                    applet.text(ghostLabel, sliderEnd + 10, sliderLine + 20);
                }
            } else {
                applet.stroke(0);
                applet.strokeWeight(3);
                applet.line(sliderLine, sliderStart, sliderLine, sliderEnd);

                applet.noStroke();
                applet.imageMode(Client.CENTER);
                applet.image(texture, sliderLine, sliderPos);

                applet.fill(0);
                applet.textAlign(Client.CENTER);
                float labelHeight = applet.textAscent() + applet.textDescent();
                applet.text(label, sliderLine,  sliderStart - labelHeight);
                applet.text(stratLabel, sliderLine, sliderEnd + labelHeight);

                if (ghosting) {
                    applet.image(ghostTexture, sliderLine, ghostPos);

                    applet.fill(120);
                    applet.text(ghostLabel, sliderLine, sliderEnd + 2 * labelHeight);
                }
            }
        }
    }

    private void textureSetup(Client applet) {
        int width, height;
        if (align == Alignment.Horizontal) {
            texture = applet.createImage(HANDLE_WIDTH, HANDLE_HEIGHT, Client.ARGB);
            ghostTexture = applet.createImage(HANDLE_WIDTH, HANDLE_HEIGHT, Client.ARGB);
            width = HANDLE_WIDTH;
            height = HANDLE_HEIGHT;
        } else {
            texture = applet.createImage(HANDLE_HEIGHT, HANDLE_WIDTH, Client.ARGB);
            ghostTexture = applet.createImage(HANDLE_HEIGHT, HANDLE_WIDTH, Client.ARGB);
            width = HANDLE_HEIGHT;
            height = HANDLE_WIDTH;
        }
        texture.loadPixels();
        ghostTexture.loadPixels();

        int centerX = width / 2 - 1;
        int centerY = height / 2 - 1;

        float maxDist = Client.sqrt(Client.sq(width / 2) + Client.sq(height / 2));

        for (int i = 0; i < texture.pixels.length; ++i) {
            int x = i % width;
            int y = i / width;

            float distance = Client.dist(x, y, centerX, centerY);
            float percent = 1 - distance / maxDist;

            if (percent > .25f) {
                texture.pixels[i] = applet.color(R * percent, G * percent, B * percent, 255 * percent);
                ghostTexture.pixels[i] = applet.color(R * percent, G * percent, B * percent, 255 * (1 - percent));
            } else {
                texture.pixels[i] = applet.color(0, 0, 0, 0);
                ghostTexture.pixels[i] = applet.color(0, 0, 0, 0);
            }
        }

        texture.updatePixels();
        ghostTexture.updatePixels();
    }
}
