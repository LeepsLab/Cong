/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.client.Client;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import processing.core.PImage;

/**
 *
 * @author swolpert
 */
public class RadioButtonGroup extends Sprite implements MouseListener {

    public static enum Alignment {

        Horizontal, Vertical
    };
    public static final int NO_BUTTON = -1;
    // applet in which button group exists - used to add and remove
    // group as a MouseListener
    private Client applet;
    // number of buttons in group
    private int numButtons;
    // how the buttons are aligned
    private Alignment alignment;
    // array storing buttons themselves
    private RadioButton[] buttons;
    // button currently pressed
    private int selectedButton;
    // distance between buttons - buttons are evenly spaced
    private float spacing;
    // whether or not group is enabled
    private boolean enabled;

    public RadioButtonGroup(Sprite parent, float x, float y, int length,
            int numButtons, Alignment alignment, int buttonRadius, Client applet) {
        super(parent, x, y, 0, 0);
        this.numButtons = numButtons;
        this.alignment = alignment;
        this.applet = applet;
        // group has only a height or a width, depending on alignment
        if (alignment == Alignment.Horizontal) {
            width = length;
        } else {
            height = length;
        }

        buttons = new RadioButton[numButtons];

        selectedButton = NO_BUTTON;

        spacing = (float) length / (float) numButtons;

        // buffer space at beginning and end of row of buttons
        float buffer = spacing / 2f;

        // initialize buttons
        if (alignment == Alignment.Vertical) {
            for (int i = 0; i < numButtons; ++i) {
                buttons[i] = new RadioButton(this, 0, buffer + i * spacing, buttonRadius, this);
            }
        } else {
            for (int i = 0; i < numButtons; ++i) {
                buttons[i] = new RadioButton(this, buffer + i * spacing, 0, buttonRadius, this);
            }
        }

        enabled = false;
    }

    @Override
    public void draw(Client applet) {
        if (visible) {
            applet.pushMatrix();
            applet.translate(origin.x, origin.y);

            for (int i = 0; i < numButtons; ++i) {
                buttons[i].draw(applet);
            }

            applet.popMatrix();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (enabled) {
            // check to see if any of the buttons in the group were clicked on
            for (int i = 0; i < numButtons; ++i) {
                if (buttons[i].circularIsHit(e.getX(), e.getY())) {
                    if (selectedButton != NO_BUTTON) {
                        buttons[selectedButton].setSelected(false);
                    }
                    buttons[i].setSelected(true);
                    selectedButton = i;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setVisible(boolean isVisible) {
        visible = isVisible;
        for (int i = 0; i < numButtons; ++i) {
            buttons[i].setVisible(isVisible);
        }

        if (isVisible) {
            applet.addMouseListener(this);
        } else {
            applet.removeMouseListener(this);
        }
    }

    public void setSelection(int selection) {
        if (selectedButton != NO_BUTTON) {
            buttons[selectedButton].setSelected(false);
        }
        selectedButton = selection;
        if (selection != NO_BUTTON) {
            buttons[selection].setSelected(true);
        }
    }

    public int getSelection() {
        return selectedButton;
    }

    public int getNumButtons() {
        return numButtons;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void clearSelections() {
        if (selectedButton == NO_BUTTON) {
            return;
        }

        buttons[selectedButton].setSelected(false);
        selectedButton = NO_BUTTON;
    }

    public void setLabelMode(Marker.LabelMode labelMode) {
        for (RadioButton button : buttons) {
            button.setLabelMode(labelMode);
        }
    }

    public void setLabels(String[] labels) {
        for (int i = 0; i < numButtons; ++i) {
            buttons[i].setLabel(labels[i]);
        }
    }

    private class RadioButton extends Marker {

        private RadioButtonGroup group;
        private boolean selected;
        private PImage idleTexture;
        private PImage selectedTexture;
        private PImage disabledTexture;
        private PImage disabledSelectedTexture;

        public RadioButton(Sprite parent, float x, float y, int radius,
                RadioButtonGroup group) {
            super(parent, x, y, true, radius);
            this.group = group;
            selected = false;
            textureSetup();
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void draw(Client applet) {
            if (visible) {
                if (label1 != null) {
                    drawLabels(applet);
                }

                applet.ellipseMode(Client.CENTER);
                applet.imageMode(Client.CENTER);

                applet.noStroke();
                applet.fill(245, 245, 245);
                applet.ellipse(origin.x, origin.y, width + 3, height + 3);

                applet.fill(0, 0, 0);
                applet.ellipse(origin.x, origin.y, width + 1, height + 1);

                if (!enabled) {
                    if (selected) {
                        applet.image(disabledSelectedTexture, origin.x, origin.y);
                    } else {
                        applet.image(disabledTexture, origin.x, origin.y);
                    }
                } else {
                    if (selected) {
                        applet.image(selectedTexture, origin.x, origin.y);
                    } else {
                        applet.image(idleTexture, origin.x, origin.y);
                    }
                }
            }
        }

        private void textureSetup() {
            idleTexture = applet.createImage(width, height, Client.ARGB);
            idleTexture.loadPixels();
            selectedTexture = applet.createImage(width, height, Client.ARGB);
            selectedTexture.loadPixels();
            disabledTexture = applet.createImage(width, height, Client.ARGB);
            disabledTexture.loadPixels();
            disabledSelectedTexture = applet.createImage(width, height, Client.ARGB);
            disabledSelectedTexture.loadPixels();
            float centerX = width / 2 - 1;
            float centerY = width / 2 - 1;
            for (int i = 0; i < idleTexture.pixels.length; ++i) {
                float x = i % width;
                float y = i / width;
                float distance = Client.dist(x, y, centerX, centerY);
                if (distance < width / 2) {
                    idleTexture.pixels[i] = applet.color(175, 175, 175, 255);
                    selectedTexture.pixels[i] = applet.color(0, 32, 113, 255);
                    disabledTexture.pixels[i] = applet.color(255, 75, 75, 255);
                    disabledSelectedTexture.pixels[i] = applet.color(133, 32, 0, 255);
                } else {
                    idleTexture.pixels[i] = applet.color(255, 255, 255, 0);
                    selectedTexture.pixels[i] = applet.color(255, 255, 255, 0);
                    disabledTexture.pixels[i] = applet.color(255, 255, 255, 0);
                    disabledSelectedTexture.pixels[i] = applet.color(255, 255, 255, 0);
                }
            }

            centerX = width / 2 - 1;
            centerY = 0;
            for (int i = 0; i < idleTexture.pixels.length; ++i) {
                float x = i % width;
                float y = i / width;
                float distance = Client.dist(x, y, centerX, centerY);
                if (distance < width / 2
                        && idleTexture.pixels[i] != applet.color(255, 255, 255, 0)) {
                    float adjustment = distance * 10;
                    idleTexture.pixels[i] = applet.color(237 - adjustment, 237 - adjustment, 237 - adjustment, 255);
                    selectedTexture.pixels[i] = applet.color(50 - adjustment, 140 - adjustment, 250 - adjustment, 255);
                    disabledTexture.pixels[i] = applet.color(255 - adjustment, 137 - adjustment, 137 - adjustment, 255);
                    disabledSelectedTexture.pixels[i] = applet.color(250 - adjustment, 80 - adjustment, 50 - adjustment, 255);
                }
            }

            centerX = width / 2 - 1;
            centerY = height - 1;
            for (int i = 0; i < idleTexture.pixels.length; ++i) {
                float x = i % width;
                float y = i / width;
                float distance = Client.dist(x, y, centerX, centerY);
                if (distance < width / 2
                        && idleTexture.pixels[i] != applet.color(255, 255, 255, 0)) {
                    float adjustment = distance * 10;
                    idleTexture.pixels[i] = applet.color(237 - adjustment, 237 - adjustment, 237 - adjustment, 255);
                    selectedTexture.pixels[i] = applet.color(50 - adjustment, 140 - adjustment, 250 - adjustment, 255);
                    disabledTexture.pixels[i] = applet.color(255 - adjustment, 137 - adjustment, 137 - adjustment, 255);
                    disabledSelectedTexture.pixels[i] = applet.color(250 - adjustment, 80 - adjustment, 50 - adjustment, 255);
                }
            }

            centerX = width / 2 - 1;
            centerY = width / 2 - 1;
            for (int i = 0; i < idleTexture.pixels.length; ++i) {
                float x = i % width;
                float y = i / width;
                if (Client.dist(x, y, centerX, centerY) < width / 4) {
                    idleTexture.pixels[i] = applet.color(175, 175, 175, 255);
                    selectedTexture.pixels[i] = applet.color(0, 0, 0, 255);
                    disabledTexture.pixels[i] = applet.color(255, 75, 75, 255);
                    disabledSelectedTexture.pixels[i] = applet.color(0, 0, 0, 255);
                }
            }

            idleTexture.updatePixels();
            selectedTexture.updatePixels();
        }

        @Override
        protected void drawLabels(Client applet) {
            applet.textFont(applet.size14);
            float textWidth = applet.textWidth(label1);
            if (label2 != null) {
                applet.textFont(applet.size14Bold);
                textWidth += applet.textWidth(label2);
            }
            if (textWidth > 16 && labelMode == LabelMode.Left) {
                labelOrigin.x = -diameter - textWidth / 2;
            } else if (textWidth > 16 && labelMode == LabelMode.Right) {
                labelOrigin.x = diameter + textWidth / 2;
            }
            float textHeight = applet.textAscent() + applet.textDescent();
            applet.rectMode(Client.CENTER);
            applet.fill(255);
            applet.noStroke();
            applet.rect(labelOrigin.x, labelOrigin.y, textWidth, textHeight);
            applet.textAlign(Client.CENTER, Client.CENTER);
            applet.fill(0);
            if (label1 != null && label2 != null) {
                float label1Width = applet.textWidth(label1);
                applet.textFont(applet.size14Bold);
                applet.text(label1, labelOrigin.x - label1Width / 2, labelOrigin.y);
                applet.textFont(applet.size14);
                applet.text("," + label2, labelOrigin.x + label1Width / 2, labelOrigin.y);
            } else if (label1 != null) {
                applet.textFont(applet.size14);
                applet.text(label1, labelOrigin.x, labelOrigin.y);
            }
        }
    }
}
