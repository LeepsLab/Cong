package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger.Selector;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.config.StrategySelectionDisplayType;
import edu.ucsc.leeps.fire.cong.server.PayoffFunction;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import processing.core.PApplet;

/**
 *
 * @author jpettit
 */
public class TwoStrategySelector extends Sprite implements Configurable<Config>, MouseListener, KeyListener, Selector {

    private Client applet;
    private Config config;
    private float percent_A, percent_a, target_percent_A;
    private boolean enabled;
    private HeatmapHelper heatmap, counterpartHeatmap;
    private float currentPercent;
    private Marker myHeatmapAa;
    private Marker myHeatmapAb;
    private Marker myHeatmapBa;
    private Marker myHeatmapBb;
    private Marker counterpartHeatmapAa;
    private Marker cuonterpartHeatmapAb;
    private Marker counterpartHeatmapBa;
    private Marker counterpartHeatmapBb;
    private Marker current, planned, dragged, hover, counterpart;
    private long hoverTimestamp;
    private long hoverTimeMillis = 0;
    private PayoffFunction payoffFunction, counterpartPayoffFunction;

    public TwoStrategySelector(
            Sprite parent, int x, int y,
            int matrixSize, int counterpartMatrixSize,
            Client applet,
            StrategyChanger strategyChanger) {
        super(parent, x, y, matrixSize, matrixSize);
        this.applet = applet;
        heatmap = new HeatmapHelper(
                this, 0, 0, matrixSize, matrixSize,
                true,
                applet);
        counterpartHeatmap = new HeatmapHelper(
                this, 0, -(counterpartMatrixSize + 30), counterpartMatrixSize, counterpartMatrixSize,
                false,
                applet);

        myHeatmapAa = new Marker(this, 10, 0, true, 0);
        myHeatmapAa.setLabelMode(Marker.LabelMode.Top);

        myHeatmapAb = new Marker(this, width, 0, true, 0);
        myHeatmapAb.setLabelMode(Marker.LabelMode.Top);

        myHeatmapBa = new Marker(this, 10, height, true, 0);
        myHeatmapBa.setLabelMode(Marker.LabelMode.Bottom);

        myHeatmapBb = new Marker(this, width, height, true, 0);
        myHeatmapBb.setLabelMode(Marker.LabelMode.Bottom);

        counterpartHeatmapAa = new Marker(this, counterpartHeatmap.origin.x + 10, counterpartHeatmap.origin.y, true, 0);
        counterpartHeatmapAa.setLabelMode(Marker.LabelMode.Top);

        cuonterpartHeatmapAb = new Marker(this, counterpartHeatmap.origin.x + counterpartHeatmap.width, counterpartHeatmap.origin.y, true, 0);
        cuonterpartHeatmapAb.setLabelMode(Marker.LabelMode.Top);

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

        FIRE.client.addConfigListener(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            applet.removeMouseListener(this);
            applet.removeKeyListener(this);
        } else {
            applet.addMouseListener(this);
            applet.addKeyListener(this);
        }
    }

    public float[] getMyStrategy() {
        return new float[]{percent_A, 0};
    }

    public void setCurrentPercent(float percent) {
        currentPercent = percent;
    }

    public void setCurrent(float[] strategy) {
        percent_A = strategy[0];
    }

    public void setInitial(float[] strategy) {
        target_percent_A = strategy[0];
    }

    public void setCounterpart(float[] strategy) {
        percent_a = strategy[0];
    }

    public float[] getTarget() {
        return new float[]{target_percent_A, 1 - target_percent_A};
    }

    public void update() {
        if (visible) {
            heatmap.updateTwoStrategyHeatmap(currentPercent);
            counterpartHeatmap.updateTwoStrategyHeatmap(currentPercent);
            updateLabels();
        }
    }

    private void updateLabels() {
        float myAa, counterAa, myAb, counterAb, myBa, counterBa, myBb, counterBb;
        myAa = payoffFunction.getPayoff(currentPercent, new float[]{1}, new float[]{1});
        counterAa = counterpartPayoffFunction.getPayoff(currentPercent, new float[]{1}, new float[]{1});
        myAb = payoffFunction.getPayoff(currentPercent, new float[]{1}, new float[]{0});
        counterAb = counterpartPayoffFunction.getPayoff(currentPercent, new float[]{0}, new float[]{1});
        myBa = payoffFunction.getPayoff(currentPercent, new float[]{0}, new float[]{1});
        counterBa = counterpartPayoffFunction.getPayoff(currentPercent, new float[]{1}, new float[]{0});
        myBb = payoffFunction.getPayoff(currentPercent, new float[]{0}, new float[]{0});
        counterBb = counterpartPayoffFunction.getPayoff(currentPercent, new float[]{0}, new float[]{0});

        myHeatmapAa.setLabel(myAa);
        myHeatmapAb.setLabel(myAb);
        myHeatmapBa.setLabel(myBa);
        myHeatmapBb.setLabel(myBb);

        counterpartHeatmapAa.setLabel(counterAa);
        cuonterpartHeatmapAb.setLabel(counterAb);
        counterpartHeatmapBa.setLabel(counterBa);
        counterpartHeatmapBb.setLabel(counterBb);
    }

    private void setModeHeatmapSingle() {
        heatmap.setVisible(true);
        counterpartHeatmap.setVisible(false);
        myHeatmapAa.setVisible(true);
        myHeatmapAb.setVisible(true);
        myHeatmapBa.setVisible(true);
        myHeatmapBb.setVisible(true);
        counterpartHeatmapAa.setVisible(false);
        cuonterpartHeatmapAb.setVisible(false);
        counterpartHeatmapBa.setVisible(false);
        counterpartHeatmapBb.setVisible(false);
    }

    private void setModeHeatmapBoth() {
        heatmap.setVisible(true);
        counterpartHeatmap.setVisible(true);
        myHeatmapAa.setVisible(true);
        myHeatmapAb.setVisible(true);
        myHeatmapBa.setVisible(true);
        myHeatmapBb.setVisible(true);
        counterpartHeatmapAa.setVisible(true);
        cuonterpartHeatmapAb.setVisible(true);
        counterpartHeatmapBa.setVisible(true);
        counterpartHeatmapBb.setVisible(true);
    }

    private void drawStrategyInfo() {
        applet.stroke(0);
        applet.noFill();
        applet.line(0, (1 - percent_A) * height, width, (1 - percent_A) * height);
        applet.line((1 - percent_a) * width, 0, (1 - percent_a) * width, height);
        applet.fill(0);

        current.update((1 - percent_a) * width, (1 - percent_A) * height);
        current.setLabel(payoffFunction.getPayoff(currentPercent, new float[]{percent_A}, new float[]{percent_a}));

        if (applet.mousePressed && inRect((int) origin.x + width / 2, applet.mouseY)) {
            dragged.setVisible(true);
            dragged.update((1 - percent_a) * width, applet.mouseY - origin.y);
            float hoverPercent_A = 1 - ((applet.mouseY - origin.y) / height);
            dragged.setLabel(payoffFunction.getPayoff(currentPercent, new float[]{hoverPercent_A}, new float[]{percent_a}));
        } else {
            dragged.setVisible(false);
            dragged.update((1 - percent_a) * width, dragged.origin.y);
        }

        if (percent_A == target_percent_A) {
            planned.setVisible(true);
            planned.update(
                    (1 - percent_a) * width,
                    (1 - target_percent_A) * height);
            planned.setLabel(payoffFunction.getPayoff(
                    currentPercent,
                    new float[]{target_percent_A},
                    new float[]{percent_a}));
        } else {
            planned.setVisible(false);
        }

        current.draw(applet);
        dragged.draw(applet);
        planned.draw(applet);
        if (System.currentTimeMillis() - hoverTimestamp >= hoverTimeMillis) {
            float hoverPercent_A = 1 - ((applet.mouseY - origin.y) / height);
            float hoverPercent_a = 1 - ((applet.mouseX - origin.x) / height);
            if (hoverPercent_A >= 0 && hoverPercent_A <= 1.0
                    && hoverPercent_a >= 0 && hoverPercent_a <= 1.0) {
                hover.setLabel(payoffFunction.getPayoff(currentPercent, new float[]{hoverPercent_A}, new float[]{hoverPercent_a}));
                hover.update((1 - hoverPercent_a) * width, (1 - hoverPercent_A) * height);
                hover.setVisible(true);
                hover.draw(applet);
            }
        }
        if (config.strategySelectionDisplayType == StrategySelectionDisplayType.HeatmapBoth) {
            float x, y, w, h;
            x = counterpartHeatmap.origin.x;
            y = counterpartHeatmap.origin.y;
            w = counterpartHeatmap.width;
            h = counterpartHeatmap.height;

            applet.stroke(0);
            applet.strokeWeight(2);
            applet.line(x + w * (1 - percent_a), y, x + w * (1 - percent_a), y + h);
            applet.line(x, y + h * (1 - percent_A), x + w, y + h * (1 - percent_A));

            counterpart.setVisible(true);
            counterpart.setLabel(counterpartPayoffFunction.getPayoff(
                    currentPercent, new float[]{percent_a}, new float[]{percent_A}));
            counterpart.update(
                    counterpartHeatmap.origin.x + (1 - percent_a) * counterpartHeatmap.width,
                    counterpartHeatmap.origin.y + (1 - percent_A) * counterpartHeatmap.height);
            counterpart.draw(applet);
        }
    }

    private void drawHeatmap() {
        heatmap.draw(applet);

        if (config.strategySelectionDisplayType == StrategySelectionDisplayType.HeatmapBoth) {
            counterpartHeatmap.draw(applet);
        }

        myHeatmapAa.draw(applet);
        myHeatmapAb.draw(applet);
        myHeatmapBa.draw(applet);
        myHeatmapBb.draw(applet);

        if (config.strategySelectionDisplayType == StrategySelectionDisplayType.HeatmapBoth) {
            counterpartHeatmapAa.draw(applet);
            cuonterpartHeatmapAb.draw(applet);
            counterpartHeatmapBa.draw(applet);
            counterpartHeatmapBb.draw(applet);
        }
    }

    @Override
    public void draw(Client applet) {
        if (!visible) {
            return;
        }
        try {
            if (!inRect(applet.mouseX, applet.mouseY) || applet.pmouseX != applet.mouseX || applet.pmouseY != applet.mouseY) {
                hoverTimestamp = System.currentTimeMillis();
                hover.setVisible(false);
            }
            applet.pushMatrix();
            applet.translate(origin.x, origin.y);

            drawHeatmap();
            drawStrategyInfo();

            applet.popMatrix();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private boolean inRect(int x, int y) {
        return (x > origin.x && x < origin.x + width && y > origin.y && y < origin.y + height);
    }

    public void mouseClicked(MouseEvent me) {
        if (!enabled) {
            return;
        }
        int mouseX = me.getX();
        int mouseY = me.getY();
        if (inRect(mouseX, mouseY)) {
            float targetPercentA = 1 - ((mouseY - origin.y) / height);
            target_percent_A = targetPercentA;
        }
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
        if (!enabled) {
            return;
        }
        int mouseX = me.getX();
        int mouseY = me.getY();
        if (inRect(mouseX, mouseY)) {
            float targetPercentA = 1 - ((mouseY - origin.y) / height);
            target_percent_A = targetPercentA;
        }
    }

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
        if (!enabled) {
            return;
        }
        if (ke.isActionKey()) {
            if (ke.getKeyCode() == KeyEvent.VK_UP) {
                float targetPercentA = target_percent_A;
                targetPercentA += 0.01f;
                targetPercentA = PApplet.constrain(targetPercentA, 0, 1);
                target_percent_A = targetPercentA;
            } else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                float targetPercentA = target_percent_A;
                targetPercentA -= 0.01f;
                targetPercentA = PApplet.constrain(targetPercentA, 0, 1);
                target_percent_A = targetPercentA;
            }
        }
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void configChanged(Config config) {
        this.config = config;
        if (config.mixedStrategySelection && !config.stripStrategySelection
                && config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            this.payoffFunction = config.payoffFunction;
            this.counterpartPayoffFunction = config.counterpartPayoffFunction;
            switch (config.strategySelectionDisplayType) {
                case HeatmapSingle:
                    setModeHeatmapSingle();
                    break;
                case HeatmapBoth:
                    setModeHeatmapBoth();
                    break;
            }
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public void startPrePeriod() {
    }

    public void startPeriod() {
        // FIXME do stuff related to clearing the screen to set up for subperiods here
    }
}
