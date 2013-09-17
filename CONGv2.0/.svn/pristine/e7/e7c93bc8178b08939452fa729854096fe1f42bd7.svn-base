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
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import edu.ucsc.leeps.fire.cong.server.ThreeStrategyPayoffFunction;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class ThreeStrategySelector extends Sprite implements Configurable<Config>, MouseListener, Selector {

    private final int CORNER_MARKER_R = 10;
    private final int BASE_MARKER_R = 7;
    private final int R = 0;
    private final int P = 1;
    private final int S = 2;
    private float sideLength;
    private Marker rock, paper, scissors;
    private float maxDist;
    private Marker current, target, hover, opponent;
    private float[] axisDistance;
    private float[] hoverStrat;
    private boolean mouseInTriangle;
    private boolean enabled;
    private HeatmapHelper heatmap;
    private Client client;
    private Config config;
    // Markers for droplines
    private Marker rDrop, pDrop, sDrop;
    private Marker hoverRDrop, hoverPDrop, hoverSDrop;
    private boolean periodStarted;

    public ThreeStrategySelector(
            Sprite parent, float x, float y, int width, int height,
            Client applet) {
        super(parent, x, y, width, height);
        this.client = applet;
        mouseInTriangle = false;
        axisDistance = new float[3];
        hoverStrat = new float[3];
        for (int i = R; i <= S; i++) {
            axisDistance[i] = 0f;
            hoverStrat[i] = 0f;
        }

        sideLength = width - 10;
        maxDist = (float) (Math.sqrt(3.0) * (sideLength / 2.0));

        rock = new Marker(this, 5, height / 3, true, CORNER_MARKER_R);
        rock.setLabel("R");
        rock.setLabelMode(Marker.LabelMode.Bottom);
        rock.setDrawMode(Marker.DrawMode.FillOutline);
        paper = new Marker(this, rock.origin.x + sideLength, rock.origin.y, true, CORNER_MARKER_R);
        paper.setLabel("P");
        paper.setLabelMode(Marker.LabelMode.Bottom);
        paper.setDrawMode(Marker.DrawMode.FillOutline);
        scissors = new Marker(this, rock.origin.x + sideLength / 2,
                rock.origin.y - (int) maxDist, true, CORNER_MARKER_R);
        scissors.setLabel("S");
        scissors.setLabelMode(Marker.LabelMode.Top);
        scissors.setDrawMode(Marker.DrawMode.FillOutline);

        // set up strategy markers
        current = new Marker(this, 0, 0, false, BASE_MARKER_R + 2);
        current.setColor(50, 255, 50);
        current.setLabel("$$");
        current.setLabelMode(Marker.LabelMode.Bottom);
        current.setDrawMode(Marker.DrawMode.FillOutline);
        target = new Marker(this, 0, 0, false, BASE_MARKER_R + 2);
        target.setLabel("$$");
        target.setLabelMode(Marker.LabelMode.Bottom);
        target.setDrawMode(Marker.DrawMode.Target);
        hover = new Marker(this, 0, 0, false, BASE_MARKER_R - 2);
        hover.setColor(0, 0, 0, 0);
        opponent = new Marker(this, 0, 0, false, BASE_MARKER_R);
        opponent.setColor(200, 40, 40);
        opponent.setDrawMode(Marker.DrawMode.FillOutline);
        opponent.setShape(Marker.Shape.Square);

        // set up dropline Markers
        rDrop = new Marker(this, 0, 0, true, BASE_MARKER_R);
        rDrop.setLabel("R");
        rDrop.setLabelMode(Marker.LabelMode.Right);
        rDrop.setDrawMode(Marker.DrawMode.FillOutline);

        hoverRDrop = new Marker(this, 0, 0, true, BASE_MARKER_R);
        hoverRDrop.setAlpha(150);
        hoverRDrop.setLabel("R");
        hoverRDrop.setLabelMode(Marker.LabelMode.Right);
        hoverRDrop.setDrawMode(Marker.DrawMode.FillOutline);

        pDrop = new Marker(this, 0, 0, true, BASE_MARKER_R);
        pDrop.setLabel("P");
        pDrop.setLabelMode(Marker.LabelMode.Left);
        pDrop.setDrawMode(Marker.DrawMode.FillOutline);

        hoverPDrop = new Marker(this, 0, 0, true, BASE_MARKER_R);
        hoverPDrop.setAlpha(150);
        hoverPDrop.setLabel("P");
        hoverPDrop.setLabelMode(Marker.LabelMode.Left);
        hoverPDrop.setDrawMode(Marker.DrawMode.FillOutline);

        sDrop = new Marker(this, 0, rock.origin.y, true, BASE_MARKER_R);
        sDrop.setLabel("S");
        sDrop.setLabelMode(Marker.LabelMode.Bottom);
        sDrop.setDrawMode(Marker.DrawMode.FillOutline);

        hoverSDrop = new Marker(this, 0, rock.origin.y, true, BASE_MARKER_R);
        hoverSDrop.setAlpha(150);
        hoverSDrop.setLabel("S");
        hoverSDrop.setLabelMode(Marker.LabelMode.Bottom);
        hoverSDrop.setDrawMode(Marker.DrawMode.FillOutline);

        setEnabled(false);

        applet.addMouseListener(this);
        heatmap = new HeatmapHelper(
                this, (int) (origin.x + rock.origin.x), (int) (origin.y + scissors.origin.y),
                (int) (paper.origin.x - rock.origin.x), (int) (rock.origin.y - scissors.origin.y),
                true, applet);
        heatmap.setVisible(false);

        periodStarted = false;

        FIRE.client.addConfigListener(this);
    }

    public void updateHeatmap() {
        if (visible) {
            float[] average = PayoffUtils.getAverageStrategy(Client.state.id, Client.state.matchStrategies);
            heatmap.updateThreeStrategyHeatmap(
                    average[0], average[1], average[2],
                    this);
        }
    }

    @Override
    public void draw(Client applet) {
        if (applet.frameCount % applet.framesPerUpdate == 0 && config.subperiods == 0) {
            updateHeatmap();
        }
        if (visible || !Client.state.matchStrategies.isEmpty()) {
            heatmap.draw(applet);
        }

        applet.pushMatrix();
        applet.translate(origin.x, origin.y);

        float mouseX = applet.mouseX - origin.x;
        float mouseY = applet.mouseY - origin.y + 1;

        applet.stroke(0);
        applet.strokeWeight(2);
        applet.line(rock.origin.x, rock.origin.y, paper.origin.x, paper.origin.y);
        applet.line(rock.origin.x, rock.origin.y, scissors.origin.x, scissors.origin.y);
        applet.line(scissors.origin.x, scissors.origin.y, paper.origin.x, paper.origin.y);
        rock.draw(applet);
        paper.draw(applet);
        scissors.draw(applet);

        if (config.showMatrix) {
            drawMatrix(applet);
        }

        if (enabled) {
            rock.shrink();
            paper.shrink();
            scissors.shrink();

            if (rock.circularIsHit(applet.mouseX, applet.mouseY)) {
                rock.enlarge();
                if (target.isGrabbed() && !config.mixed) {
                    Client.state.target[R] = 1;
                    Client.state.target[P] = 0;
                    Client.state.target[S] = 0;
                }
            } else if (paper.circularIsHit(applet.mouseX, applet.mouseY)) {
                paper.enlarge();
                if (target.isGrabbed() && !config.mixed) {
                    Client.state.target[R] = 0;
                    Client.state.target[P] = 1;
                    Client.state.target[S] = 0;
                }
            } else if (scissors.circularIsHit(applet.mouseX, applet.mouseY)) {
                scissors.enlarge();
                if (target.isGrabbed() && !config.mixed) {
                    Client.state.target[R] = 0;
                    Client.state.target[P] = 0;
                    Client.state.target[S] = 1;
                }
            }

            calculateAxisDistance(mouseX - rock.origin.x, rock.origin.y - mouseY);

            if (axisDistance[R] <= maxDist && axisDistance[R] >= 0 && axisDistance[P] <= maxDist && axisDistance[P] >= 0 && axisDistance[S] <= maxDist && axisDistance[S] >= 0) {
                mouseInTriangle = true;
            } else {
                mouseInTriangle = false;
            }

            if (mouseInTriangle) {
                if (target.isGrabbed() && config.mixed) {
                    Client.state.target[S] = axisDistance[S] / maxDist;
                    Client.state.target[P] = axisDistance[P] / maxDist;
                    Client.state.target[R] = 1 - Client.state.target[S] - Client.state.target[P];
                }

                hoverStrat[S] = axisDistance[S] / maxDist;
                hoverStrat[P] = axisDistance[P] / maxDist;
                hoverStrat[R] = 1 - hoverStrat[S] - hoverStrat[P];
                hover.setVisible(true);
                hover.update(mouseX, mouseY);
            } else {
                if (target.isGrabbed()) {
                    target.release();
                }
                hover.setVisible(false);
                applet.cursor();
            }
            if (Client.state.target != null) {
                float[] coords = calculateStratCoords(Client.state.target[R], Client.state.target[P], Client.state.target[S]);
                target.update(coords[0], coords[1]);
            }
        }

        if (hover.visible) {
            updateDropLines(hover, hoverStrat, hoverRDrop, hoverPDrop, hoverSDrop);
            applet.strokeWeight(1);
            applet.stroke(0, 255, 255, 150);
            applet.line(hover.origin.x, hover.origin.y, hoverRDrop.origin.x, hoverRDrop.origin.y);
            applet.line(hover.origin.x, hover.origin.y, hoverPDrop.origin.x, hoverPDrop.origin.y);
            applet.line(hover.origin.x, hover.origin.y, hoverSDrop.origin.x, hoverSDrop.origin.y);

            hover.setLabel(PayoffUtils.getPayoff(hoverStrat));

            hoverRDrop.setLabel(hoverStrat[R]);
            hoverPDrop.setLabel(hoverStrat[P]);
            hoverSDrop.setLabel(hoverStrat[S]);

            adjustLabels(hoverStrat, hover, hoverPDrop, hoverRDrop);

            hoverRDrop.draw(applet);
            hoverPDrop.draw(applet);
            hoverSDrop.draw(applet);
        }

        if (target.visible && Client.state.target != null) {
            target.setLabel(PayoffUtils.getPayoff(Client.state.target));
            adjustLabels(Client.state.target, target, null, null);
        }

        float[] myStrategy;
        if (config.subperiods != 0 && Client.state.subperiod == 0) {
            myStrategy = Client.state.target;
        } else {
            myStrategy = Client.state.getMyStrategy();
        }
        if (myStrategy != null) {
            float[] coords = calculateStratCoords(myStrategy[R], myStrategy[P], myStrategy[S]);
            current.update(coords[0], coords[1]);
        }

        if (current.visible) {
            updateDropLines(current, myStrategy, rDrop, pDrop, sDrop);
            applet.strokeWeight(1);
            applet.stroke(0, 255, 255);
            applet.line(current.origin.x, current.origin.y, rDrop.origin.x, rDrop.origin.y);
            applet.line(current.origin.x, current.origin.y, pDrop.origin.x, pDrop.origin.y);
            applet.line(current.origin.x, current.origin.y, sDrop.origin.x, sDrop.origin.y);
        }

        current.setLabel(PayoffUtils.getPayoff());

        adjustLabels(myStrategy, current, pDrop, rDrop);

        rDrop.setLabel(myStrategy[R]);
        pDrop.setLabel(myStrategy[P]);
        sDrop.setLabel(myStrategy[S]);

        if (config.subperiods == 0) {
            float[] average = PayoffUtils.getAverageStrategy(Client.state.id, Client.state.matchStrategies);
            if (average.length == 3) {
                float[] coords = calculateStratCoords(average[R], average[P], average[S]);
                opponent.update(coords[0], coords[1]);
            }
        }

        rDrop.draw(applet);
        pDrop.draw(applet);
        sDrop.draw(applet);

        if (config.subperiods != 0 && Client.state.subperiod == 0) {
            target.setLabel("");
            current.setLabel("");
            hover.setLabel("");
            target.draw(applet);
            current.draw(applet);
            hover.draw(applet);
        } else {
            target.draw(applet);
            current.draw(applet);
            hover.draw(applet);
            opponent.draw(applet);
        }

        applet.popMatrix();
    }

    private void drawMatrix(Client applet) {
        ThreeStrategyPayoffFunction pf = ((ThreeStrategyPayoffFunction) config.payoffFunction);
        float[][] table = new float[][]{
            {pf.Rr, pf.Rp, pf.Rs},
            {pf.Pr, pf.Pp, pf.Ps},
            {pf.Sr, pf.Sp, pf.Ss}
        };
        String[] rowLabels = new String[]{"A", "B", "C"};
        String[] colLabels = new String[]{"a", "b", "c"};
        float tableX = 45;
        float tableY = -origin.y + 25;
        float textWidth = applet.textWidth("00");
        float cellWidth = 11 + textWidth + 11;
        float cellHeight = 5 + textWidth + 5;
        applet.stroke(0);
        applet.strokeWeight(2);
        int cols = table.length + 1;
        int rows = table.length + 1;
        float tableWidth = cols * cellWidth;
        float tableHeight = rows * cellHeight;
        applet.pushMatrix();
        applet.translate(tableX - tableWidth / 2, tableY);
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
                applet.fill(0);
                if (col == 0) {
                    applet.text(rowLabels[row - 1], Math.round(cellWidth / 2f), Math.round(row * cellHeight + cellHeight / 2f));
                } else if (row == 0) {
                    applet.text(colLabels[col - 1], Math.round(col * cellWidth + cellWidth / 2f), Math.round(cellHeight / 2f));
                } else {
                    float payoff = table[row - 1][col - 1];
                    String s = String.format("%.0f", payoff);
                    applet.text(s, Math.round(col * cellWidth + cellWidth / 2f), Math.round(row * cellHeight + cellHeight / 2f));
                }
            }
        }
        applet.text("Others Choices", Math.round(tableWidth - (cellWidth * (cols - 1)) / 2f), -10);
        applet.pushMatrix();
        applet.translate(-10, tableHeight - (cellHeight * (rows - 1)) / 2f);
        applet.rotate(3 * Client.PI / 2);
        applet.text("Your Choices", 0, 0);
        applet.popMatrix();
        applet.popMatrix();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (enabled) {
            if (mouseInTriangle) {
                adjustLabels(Client.state.getMyStrategy(), current, pDrop, rDrop);
                target.grab();
            } else if (rock.isEnlarged()) {
                setTargetRPS(1, 0, 0);
            } else if (paper.isEnlarged()) {
                setTargetRPS(0, 1, 0);
            } else if (scissors.isEnlarged()) {
                setTargetRPS(0, 0, 1);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (mouseInTriangle) {
            if (target.isGrabbed()) {
                target.release();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        client.cursor();
        if (enabled) {
            current.setVisible(true);
            target.setVisible(true);
            opponent.setVisible(true);
            rDrop.setVisible(true);
            pDrop.setVisible(true);
            sDrop.setVisible(true);
            if (!periodStarted) {
                periodStarted = true;
                heatmap.setVisible(true);
            }
        } else {
            current.setVisible(false);
            target.setVisible(false);
            opponent.setVisible(false);
            rDrop.setVisible(false);
            pDrop.setVisible(false);
            sDrop.setVisible(false);
        }
    }

    public void startPrePeriod() {
        enabled = true;
        client.cursor();
        target.setVisible(true);
        current.setVisible(false);
        opponent.setVisible(false);
        rDrop.setVisible(false);
        pDrop.setVisible(false);
        sDrop.setVisible(false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            client.removeMouseListener(this);
        } else {
            client.addMouseListener(this);
        }
    }

    // adjust the labeling mode of given marker based on strats in given
    // array. also adjust labeling mode of given droplines
    private void adjustLabels(float[] strats, Marker stratMarker,
            Marker pDropMarker, Marker rDropMarker) {
        if (!periodStarted) {
            stratMarker.setLabel(null);
        }
        if (strats[S] < 0.2f) {
            if (strats[R] > strats[P]) {
                stratMarker.setLabelMode(Marker.LabelMode.Right);
                if (pDropMarker != null) {
                    pDropMarker.setLabelMode(Marker.LabelMode.Top);
                }
                if (rDropMarker != null) {
                    rDropMarker.setLabelMode(Marker.LabelMode.Right);
                }
            } else {
                stratMarker.setLabelMode(Marker.LabelMode.Left);
                if (pDropMarker != null) {
                    pDropMarker.setLabelMode(Marker.LabelMode.Left);
                }
                if (rDropMarker != null) {
                    rDropMarker.setLabelMode(Marker.LabelMode.Top);
                }
            }
        } else {
            stratMarker.setLabelMode(Marker.LabelMode.Bottom);
            if (pDropMarker != null) {
                pDropMarker.setLabelMode(Marker.LabelMode.Left);
            }
            if (rDropMarker != null) {
                rDropMarker.setLabelMode(Marker.LabelMode.Right);
            }
        }
    }

    public float[] translate(float x, float y) {
        float newS = y / maxDist;

        // constant factors for determining distances
        float epsilon1 = y + (1 / Client.sqrt(3)) * x;
        float epsilon2 = y - (1 / Client.sqrt(3)) * x;

        // calculate distance from paper 3D axis
        float deltaX = x - (Client.sqrt(3) / 4) * epsilon1;
        float deltaY = y - .75f * epsilon1;
        float newP;
        if (deltaX < 0 && deltaY > 0) {
            newP = -1;
        } else {
            float distP = Client.sqrt(Client.sq(deltaX) + Client.sq(deltaY));
            newP = distP / maxDist;
        }

        // calculate distance from rock 3D axis
        deltaX = x - .75f * sideLength + (Client.sqrt(3) / 4) * epsilon2;
        deltaY = y - (Client.sqrt(3) / 4) * sideLength - .75f * epsilon2;
        float newR;
        if (deltaX > 0 && deltaY > 0) {
            newR = -1;
        } else {
            newR = 1 - newS - newP;
        }

        return new float[]{newR, newP, newS};
    }

    // calculate axisDistance entries
    /*
     * Modifies axisDistance array. Array is invalid if any of the entries are
     * -1.
     */
    private void calculateAxisDistance(float x, float y) {
        axisDistance[S] = y;

        // constant factors for determining distances
        float epsilon1 = y + (1 / Client.sqrt(3)) * x;
        float epsilon2 = y - (1 / Client.sqrt(3)) * x;

        float deltaX, deltaY;

        deltaX = x - (Client.sqrt(3) / 4) * epsilon1;
        deltaY = y - .75f * epsilon1;
        if (deltaX < 0 && deltaY > 0) {
            axisDistance[P] = -1;
        } else {
            axisDistance[P] = Client.sqrt(Client.sq(deltaX) + Client.sq(deltaY));
        }

        deltaX = x - .75f * sideLength + (Client.sqrt(3) / 4) * epsilon2;
        deltaY = y - (Client.sqrt(3) / 4) * sideLength - .75f * epsilon2;
        if (deltaX > 0 && deltaY > 0) {
            axisDistance[R] = -1;
        } else {
            axisDistance[R] = Client.sqrt(Client.sq(deltaX) + Client.sq(deltaY));
        }
    }

    // calculate x, y coordinates given r, p, s values
    private float[] calculateStratCoords(float r, float p, float s) {
        float[] coords = new float[2];

        coords[0] = (float) (rock.origin.x + (maxDist * p) / Math.sin(Math.PI / 3.0) + maxDist * s * Math.tan(Math.PI / 6.0));
        coords[1] = rock.origin.y - maxDist * s;

        return coords;
    }

    private void updateDropLines(Marker stratMarker, float[] strats,
            Marker rDropMarker, Marker pDropMarker, Marker sDropMarker) {
        sDropMarker.update(stratMarker.origin.x, rock.origin.y);

        float x, y;
        x = stratMarker.origin.x - maxDist * strats[P] * Client.cos(Client.PI / 6);
        y = stratMarker.origin.y - maxDist * strats[P] * Client.sin(Client.PI / 6);
        pDropMarker.update(x, y);

        x = stratMarker.origin.x + maxDist * strats[R] * Client.cos(Client.PI / 6);
        y = stratMarker.origin.y - maxDist * strats[R] * Client.sin(Client.PI / 6);
        rDropMarker.update(x, y);
    }

    private void setTargetRPS(float targetR, float targetP, float targetS) {
        Client.state.setTarget(new float[]{targetR, targetP, targetS}, config);
        float[] coords = calculateStratCoords(Client.state.target[R], Client.state.target[P], Client.state.target[S]);
        target.update(coords[0], coords[1]);
    }

    public void configChanged(Config config) {
        this.config = config;
        if (config.selector == Config.StrategySelector.simplex) {
            rock.setLabel(config.shortRLabel);
            rock.setColor(config.rColor);
            paper.setLabel(config.shortPLabel);
            paper.setColor(config.pColor);
            scissors.setLabel(config.shortSLabel);
            scissors.setColor(config.sColor);
            rDrop.setColor(config.rColor);
            hoverRDrop.setColor(config.rColor);
            pDrop.setColor(config.pColor);
            hoverPDrop.setColor(config.pColor);
            sDrop.setColor(config.sColor);
            hoverSDrop.setColor(config.sColor);
            current.setColor(
                    config.yourStrategy.r,
                    config.yourStrategy.g,
                    config.yourStrategy.b);
            opponent.setColor(
                    config.matchStrategy.r,
                    config.matchStrategy.g,
                    config.matchStrategy.b);
            hover.setColor(
                    config.yourStrategy.r,
                    config.yourStrategy.g,
                    config.yourStrategy.b);

            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public void startPeriod() {
        heatmap.reset();
    }

    public void endSubperiod(int subperiod) {
        updateHeatmap();
        float[] average = PayoffUtils.getAverageStrategy(Client.state.id, Client.state.matchStrategies);
        if (average.length == 3) {
            float[] coords = calculateStratCoords(average[R], average[P], average[S]);
            opponent.update(coords[0], coords[1]);
        }
    }
}
