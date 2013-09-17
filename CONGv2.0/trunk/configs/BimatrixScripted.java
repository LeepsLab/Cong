/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/


import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.gui.HeatmapHelper;
import edu.ucsc.leeps.fire.cong.client.gui.Marker;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import edu.ucsc.leeps.fire.cong.server.ScriptedPayoffFunction.PayoffScriptInterface;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

public class BimatrixScripted implements PayoffScriptInterface, MouseListener, KeyListener {

    private Config config;
    private boolean setup;
    
    private HeatmapHelper heatmap, counterpartHeatmap;
    private Marker myHeatmapAa;
    private Marker myHeatmapAb;
    private Marker myHeatmapBa;
    private Marker myHeatmapBb;
    private Marker counterpartHeatmapAa;
    private Marker counterpartHeatmapAb;
    private Marker counterpartHeatmapBa;
    private Marker counterpartHeatmapBb;
    private Marker current, planned, dragged, hover, counterpart;
    private long hoverTimestamp;
    private long hoverTimeMillis = 0;
    
    // Matrix size vars
    int matrixSize, counterpartMatrixSize;

    public BimatrixScripted() {
        if (FIRE.client != null) {
            config = FIRE.client.getConfig();
        } else if (FIRE.server != null) {
            config = FIRE.server.getConfig();
        }
    }

    public float getPayoff(
            int id,
            float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        return 0;
    }

    public void draw(Client a) {
        
        if (!setup && Client.state != null && Client.state.getMyStrategy() != null) {
            heatmap = new HeatmapHelper(
                    this, 0, 0, matrixSize, matrixSize,
                    true,
                    a);
            counterpartHeatmap = new HeatmapHelper(
                    this, 0, -(counterpartMatrixSize + 30), counterpartMatrixSize, counterpartMatrixSize,
                    false,
                    a);

            myHeatmapAa = new Marker(this, 10, 0, true, 0);
            myHeatmapAa.setLabelMode(Marker.LabelMode.Top);

            myHeatmapAb = new Marker(this, a.width, 0, true, 0);
            myHeatmapAb.setLabelMode(Marker.LabelMode.Top);

            myHeatmapBa = new Marker(this, 10, a.height, true, 0);
            myHeatmapBa.setLabelMode(Marker.LabelMode.Bottom);

            myHeatmapBb = new Marker(this, a.width, a.height, true, 0);
            myHeatmapBb.setLabelMode(Marker.LabelMode.Bottom);

            counterpartHeatmapAa = new Marker(this, counterpartHeatmap.origin.x + 10, counterpartHeatmap.origin.y, true, 0);
            counterpartHeatmapAa.setLabelMode(Marker.LabelMode.Top);

            counterpartHeatmapAb = new Marker(this, counterpartHeatmap.origin.x + counterpartHeatmap.width, counterpartHeatmap.origin.y, true, 0);
            counterpartHeatmapAb.setLabelMode(Marker.LabelMode.Top);

            counterpartHeatmapBa = new Marker(this, counterpartHeatmap.origin.x + 10, counterpartHeatmap.origin.y + counterpartHeatmap.height, true, 0);
            counterpartHeatmapBa.setLabelMode(Marker.LabelMode.Bottom);

            counterpartHeatmapBb = new Marker(this, counterpartHeatmap.origin.x + counterpartHeatmap.width, counterpartHeatmap.origin.y + counterpartHeatmap.height, true, 0);
            counterpartHeatmapBb.setLabelMode(Marker.LabelMode.Bottom);

            current = new Marker(this, 0, 0, true, 10);
            current.setLabelMode(Marker.LabelMode.Top);
            dragged = new Marker(this, 0, 0, false, 10);
            dragged.setLabelMode(Marker.LabelMode.Top);
            planned = new Marker(this, 0, 0, false, 10);
            planned.setLabelMode(Marker.LabelMode.Top);
            hover = new Marker(this, 0, 0, false, 10);
            hover.setLabelMode(Marker.LabelMode.Top);
            hoverTimestamp = System.currentTimeMillis();
            counterpart = new Marker(this, 0, 0, false, 10);
            counterpart.setLabelMode(Marker.LabelMode.Top);
            
            setup = true;
        }
        
        if (a.mousePressed) {
            updateTarget(a, a.mouseY);
        }

        if (a.frameCount % a.framesPerUpdate == 0 && FIRE.client.getConfig().subperiods == 0) {
            updateHeatmaps();
        }
        if (!inRect(a, a.mouseX, a.mouseY) || a.pmouseX != a.mouseX || a.pmouseY != a.mouseY) {
            hoverTimestamp = System.currentTimeMillis();
            hover.setVisible(false);
        }
        a.pushMatrix();
        a.translate(0, 0);

        drawHeatmap(a);
        drawStrategyInfo(a);

        a.popMatrix();
    }

    public float getMin() {
        return config.payoffFunction.getMin();
    }

    public float getMax() {
        return config.payoffFunction.getMax();
    }
    
    ///////////////////
    
    private void updateTarget(Client a, int mouseY) {
        float target;
        if (aboveRect(mouseY)) {
            target = 1;
        } else if (belowRect(a, mouseY)) {
            target = 0;
        } else {
            target = 1 - ((mouseY) / a.height);
        }
        if (Client.state.target != null) {
            Client.state.target[0] = target;
        }
    }
    
    public void updateHeatmaps() {
        heatmap.updateTwoStrategyHeatmap();
        counterpartHeatmap.updateTwoStrategyHeatmap();
        updateLabels();
    }
    
    private void drawHeatmap(Client a) {
        heatmap.draw(a);

        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
            counterpartHeatmap.draw(a);
        }

        myHeatmapAa.draw(a);
        myHeatmapAb.draw(a);
        myHeatmapBa.draw(a);
        myHeatmapBb.draw(a);

        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
            counterpartHeatmapAa.draw(a);
            counterpartHeatmapAb.draw(a);
            counterpartHeatmapBa.draw(a);
            counterpartHeatmapBb.draw(a);
        }
    }
    
    private void drawStrategyInfo(Client a) {
        float percent_A;
        if (config.subperiods != 0) {
            percent_A = Client.state.target[0];
        } else if (Client.state.getMyStrategy() != null) {
            percent_A = Client.state.getMyStrategy()[0];
        } else if (config.initialStrategy != null) {
            percent_A = config.initialStrategy[0];
        } else {
            percent_A = config.initial;
        }
        float percent_a = PayoffUtils.getAverageStrategy(Client.state.id, Client.state.matchStrategies)[0];
        if (((TwoStrategyPayoffFunction) config.payoffFunction).reverseXAxis()) {
            percent_a = 1 - percent_a;
        }
        a.stroke(0);
        a.noFill();
        a.line(0, (1 - percent_A) * a.height, a.width, (1 - percent_A) * a.height);
        a.line((1 - percent_a) * a.width, 0, (1 - percent_a) * a.width, a.height);
        a.fill(0);

        current.update((1 - percent_a) * a.width, (1 - percent_A) * a.height);
        if (config.showHeatmap) {
            current.setLabel(PayoffUtils.getPayoff());
        }

        if (a.mousePressed) {
            dragged.setVisible(true);
            float target;
            if (aboveRect(a.mouseY)) {
                target = 1;
            } else if (belowRect(a, a.mouseY)) {
                target = 0;
            } else {
                target = 1 - ((a.mouseY) / a.height);
            }
            dragged.update((1 - percent_a) * a.width, (1 - target) * a.height);
            float hoverPercent_A = 1 - ((a.mouseY) / a.height);
            if (config.showHeatmap) {
                dragged.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}));
            }
        } else {
            dragged.setVisible(false);
            dragged.update((1 - percent_a) * a.width, dragged.origin.y);
        }

        if (Client.state.target != null && percent_A == Client.state.target[0]) {
            planned.setVisible(true);
            planned.update(
                    (1 - percent_a) * a.width,
                    (1 - Client.state.target[0]) * a.height);
            if (config.showHeatmap) {
                planned.setLabel(PayoffUtils.getPayoff(new float[]{Client.state.target[0]}));
            }
        } else {
            planned.setVisible(false);
        }

        current.draw(a);
        dragged.draw(a);
        planned.draw(a);
        if (System.currentTimeMillis() - hoverTimestamp >= hoverTimeMillis) {
            float hoverPercent_A = 1 - ((a.mouseY) / a.height);
            float hoverPercent_a = 1 - ((a.mouseX) / a.height);
            if (hoverPercent_A >= 0 && hoverPercent_A <= 1.0
                    && hoverPercent_a >= 0 && hoverPercent_a <= 1.0) {
                if (config.showHeatmap) {
                    if (((TwoStrategyPayoffFunction) config.payoffFunction).reverseXAxis()) {
                        hover.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}, new float[]{1 - hoverPercent_a}));
                    } else {
                        hover.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}, new float[]{hoverPercent_a}));
                    }
                }
                hover.update((1 - hoverPercent_a) * a.width, (1 - hoverPercent_A) * a.height);
                hover.setVisible(true);
                hover.draw(a);
            }
        }
        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
            float x, y, w, h;
            x = counterpartHeatmap.origin.x;
            y = counterpartHeatmap.origin.y;
            w = counterpartHeatmap.width;
            h = counterpartHeatmap.height;

            a.stroke(0);
            a.strokeWeight(2);
            a.line(x + w * (1 - percent_a), y, x + w * (1 - percent_a), y + h);
            a.line(x, y + h * (1 - percent_A), x + w, y + h * (1 - percent_A));

            counterpart.setVisible(true);
            if (config.showHeatmap) {
                counterpart.setLabel(PayoffUtils.getMatchPayoff());
            }
            counterpart.update(
                    counterpartHeatmap.origin.x + (1 - percent_a) * counterpartHeatmap.width,
                    counterpartHeatmap.origin.y + (1 - percent_A) * counterpartHeatmap.height);
            counterpart.draw(a);
        }
    }
    
    private void updateLabels() {
        float myAa, counterAa, myAb, counterAb, myBa, counterBa, myBb, counterBb;
        myAa = PayoffUtils.getPayoff(new float[]{1}, new float[]{1});
        counterAa = PayoffUtils.getMatchPayoff(new float[]{1}, new float[]{1});
        myAb = PayoffUtils.getPayoff(new float[]{1}, new float[]{0});
        counterAb = PayoffUtils.getMatchPayoff(new float[]{1}, new float[]{0});
        myBa = PayoffUtils.getPayoff(new float[]{0}, new float[]{1});
        counterBa = PayoffUtils.getMatchPayoff(new float[]{0}, new float[]{1});
        myBb = PayoffUtils.getPayoff(new float[]{0}, new float[]{0});
        counterBb = PayoffUtils.getMatchPayoff(new float[]{0}, new float[]{0});

        if (((TwoStrategyPayoffFunction) config.payoffFunction).reverseXAxis()) {
            myHeatmapAa.setLabel(myAb);
            myHeatmapAb.setLabel(myAa);
            myHeatmapBa.setLabel(myBb);
            myHeatmapBb.setLabel(myBa);

            counterpartHeatmapAa.setLabel(counterAb);
            counterpartHeatmapAb.setLabel(counterAa);
            counterpartHeatmapBa.setLabel(counterBb);
            counterpartHeatmapBb.setLabel(counterBa);
        } else {
            myHeatmapAa.setLabel(myAa);
            myHeatmapAb.setLabel(myAb);
            myHeatmapBa.setLabel(myBa);
            myHeatmapBb.setLabel(myBb);

            counterpartHeatmapAa.setLabel(counterAa);
            counterpartHeatmapAb.setLabel(counterAb);
            counterpartHeatmapBa.setLabel(counterBa);
            counterpartHeatmapBb.setLabel(counterBb);
        }
    }
    
    private boolean inRect(Client a, int x, int y) {
        return x >= 0 && x <= a.width && y >= 0 && y <= a.height;
    }
    
    private boolean aboveRect(int y) {
        return y < 0;
    }

    private boolean belowRect(Client a, int y) {
        return y > a.height;
    }
    
    
    
    
    
    
    
    
    ////////////////////

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
    
    ////////////////////////
    
    
    
    
    
    
    

    
    
    
    
    //////////////////////////
    
    
}