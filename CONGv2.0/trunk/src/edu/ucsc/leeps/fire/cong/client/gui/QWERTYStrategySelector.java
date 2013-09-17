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
import edu.ucsc.leeps.fire.cong.client.StrategyChanger.Selector;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.QWERTYPayoffFunction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author jpettit
 */
public class QWERTYStrategySelector extends Sprite implements Configurable<Config>, KeyListener, Selector {

    private RadioButtonGroup firmButtons;
    private QWERTYPayoffFunction pf;
    private int size;
    private boolean isKeyListenerSet = false;

    public QWERTYStrategySelector(
            Sprite parent, int x, int y,
            int size,
            Client applet) {
        super(parent, x, y, size, size);
        this.size = size;
        firmButtons = new RadioButtonGroup(
                this, size - 50, 0, size, 2,
                RadioButtonGroup.Alignment.Vertical, 15, applet);
        firmButtons.setLabelMode(Marker.LabelMode.Bottom);
        firmButtons.setLabels(new String[]{"Firm 1", "Firm 2"});
        firmButtons.setEnabled(false);
        FIRE.client.addConfigListener(this);
    }

    public void setEnabled(boolean enabled) {
        firmButtons.setEnabled(enabled);
    }

    public void setKeyListener(Client applet) {
        if (!isKeyListenerSet) {
            applet.addKeyListener(this);
            isKeyListenerSet = true;
        } else {
            return;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        firmButtons.setVisible(visible);
    }

    @Override
    public void draw(Client applet) {
        if (!visible) {
            return;
        }
        setKeyListener(applet);
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        drawTable(applet);
        firmButtons.draw(applet);
        applet.popMatrix();
        if (firmButtons.getSelection() == 0) {
            Client.state.target[0] = 0;
        } else {
            Client.state.target[0] = 1;
        }
    }

    private void drawTable(Client applet) {
        float textWidth = applet.textWidth("00");
        float textHeight = applet.textAscent() + applet.textDescent() + 4;
        float cellWidth = 2 + textWidth + 2;
        float cellHeight = 2 + textWidth + 2;
        textWidth = applet.textWidth("AAAAAAAAAA");
        applet.stroke(0);
        applet.strokeWeight(2);
        int cols = 2 + FIRE.client.getConfig().groupSize;
        int rows = 2 + FIRE.client.getConfig().groupSize;
        float tableWidth = cols * cellWidth;
        float tableHeight = rows * cellHeight;
        applet.pushMatrix();
        applet.translate(size / 2f - tableWidth / 2f, size / 4 - tableHeight / 2);

        int ownSame = QWERTYPayoffFunction.getInSame(Client.state.id, Client.state.getMyStrategy(), Client.state.strategies);
        int ownDiff = QWERTYPayoffFunction.getNotInSame(Client.state.id, Client.state.getMyStrategy(), Client.state.strategies);
        int matchSame = QWERTYPayoffFunction.getInSame(Client.state.id, Client.state.getMyStrategy(), Client.state.matchStrategies);
        int matchDiff = QWERTYPayoffFunction.getNotInSame(Client.state.id, Client.state.getMyStrategy(), Client.state.matchStrategies);

        for (int platform = 0; platform <= 1; platform++) {
            applet.translate(0, platform * size / 2);

            //Selection box location
            if (firmButtons.getSelection() == platform) {
                applet.fill(200);
                applet.rect(0 - textWidth * 1.5f, -5 - 2.5f * textHeight, size, size / 2);
                applet.fill(0);
            }

            for (int col = 1; col <= cols; col++) {
                applet.line(col * cellWidth, 0, col * cellWidth, cellHeight * rows);
            }
            for (int row = 1; row <= rows; row++) {
                applet.line(0, row * cellHeight, cellWidth * cols, row * cellHeight);
            }
            applet.textAlign(Client.CENTER, Client.CENTER);
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    if (col == 0 && row == 0) {
                        continue;
                    }
                    if (col == 0) {
                        if (platform == Client.state.getMyStrategy()[0] && matchSame + 1 == row) {
                            applet.fill(255, 169, 68, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        } else if (platform != Client.state.getMyStrategy()[0] && matchDiff + 1 == row) {
                            applet.fill(255, 169, 68, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        }
                        applet.text(row - 1, Math.round(cellWidth / 2f), Math.round(row * cellHeight + cellHeight / 2f));
                    } else if (row == 0) {
                        if (platform == Client.state.getMyStrategy()[0] && ownSame + 1 == col) {
                            applet.fill(255, 169, 68, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        } else if (platform != Client.state.getMyStrategy()[0] && ownDiff + 1 == col) {
                            applet.fill(255, 169, 68, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        }
                        applet.text(col - 1, Math.round(col * cellWidth + cellWidth / 2f), Math.round(cellHeight / 2f));
                    } else {
                        if (platform == Client.state.getMyStrategy()[0] && ownSame + 1 == col && matchSame + 1 == row) {
                            applet.fill(255, 169, 68, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        }
                        if (platform != Client.state.getMyStrategy()[0] && ownDiff + 2 == col && matchDiff + 1 == row) {
                            applet.fill(80, 255, 80, 100);
                            applet.rect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
                            applet.fill(0);
                        }
                        float[][] platformMatrix;
                        if (platform == 0) {
                            platformMatrix = pf.pf1;
                        } else {
                            platformMatrix = pf.pf2;
                        }
                        if (platformMatrix[row - 1][col - 1] != 0f) {
                            String s = String.format("%.0f", platformMatrix[row - 1][col - 1]);
                            applet.text(s, Math.round(col * cellWidth + cellWidth / 2f), Math.round(row * cellHeight + cellHeight / 2f));
                        }
                    }
                }
            }
            String cost;
            if (platform == 0) {
                cost = String.format("%.2f", pf.cost1);
            } else {
                cost = String.format("%.2f", pf.cost2);
            }
            applet.text("Number of your", Math.round((cols / 2 + 0.5) * cellWidth), -1.5f * textHeight - 5);
            applet.text("type in firm " + (platform + 1), Math.round((cols / 2 + 0.5) * cellWidth), -0.5f * textHeight - 5);
            applet.fill(220, 0, 0);
            applet.text("Cost rate: " + cost, Math.round((cols / 2 + 0.5) * cellWidth), cellHeight * rows + 0.5f * textHeight + 15);
            applet.fill(0);
            applet.text("Number of", -1 * textWidth / 2 - 5, Math.round((rows / 2 + 0.5) * cellHeight - 1 * textHeight));
            applet.text("other type", -1 * textWidth / 2 - 5, Math.round((rows / 2 + 0.5) * cellHeight));
            applet.text("in firm " + (platform + 1), -1 * textWidth / 2 - 5, Math.round((rows / 2 + 0.5) * cellHeight + 1 * textHeight));
        }
        applet.popMatrix();
    }

    public void configChanged(Config config) {
        if (config.selector == Config.StrategySelector.qwerty) {
            this.pf = (QWERTYPayoffFunction) config.payoffFunction;
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public void startPrePeriod() {
    }

    public void startPeriod() {
        if (Client.state.getMyStrategy()[0] == 1) {
            firmButtons.setSelection(0);
        } else {
            firmButtons.setSelection(1);
        }
    }

    public void endSubperiod(int subperiod) {
    }

    public void keyPressed(KeyEvent e) {
        if (visible) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (firmButtons.getSelection() != 0 && firmButtons.isEnabled()) {
                    firmButtons.setSelection(0);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && firmButtons.isEnabled()) {
                if (firmButtons.getSelection() != 1) {
                    firmButtons.setSelection(1);
                }
            }
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
