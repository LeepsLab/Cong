package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger.Selector;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffFunction;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author jpettit
 */
public class TwoStrategySelector extends Sprite implements Configurable<Config>, MouseListener, KeyListener, Selector {

    private Client applet;
    private Config config;
    private boolean enabled;
    private HeatmapHelper heatmap, counterpartHeatmap;
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

    public TwoStrategySelector(
            Sprite parent, int x, int y,
            int matrixSize, int counterpartMatrixSize,
            Client applet) {
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

    public void updateHeatmaps() {
        if (visible) {
            heatmap.updateTwoStrategyHeatmap();
            counterpartHeatmap.updateTwoStrategyHeatmap();
            updateLabels();
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
            cuonterpartHeatmapAb.setLabel(counterAa);
            counterpartHeatmapBa.setLabel(counterBb);
            counterpartHeatmapBb.setLabel(counterBa);
        } else {
            myHeatmapAa.setLabel(myAa);
            myHeatmapAb.setLabel(myAb);
            myHeatmapBa.setLabel(myBa);
            myHeatmapBb.setLabel(myBb);

            counterpartHeatmapAa.setLabel(counterAa);
            cuonterpartHeatmapAb.setLabel(counterAb);
            counterpartHeatmapBa.setLabel(counterBa);
            counterpartHeatmapBb.setLabel(counterBb);
        }
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
        float percent_A = Client.state.getMyStrategy()[0];
        float percent_a = PayoffUtils.getAverageMatchStrategy()[0];
        if (((TwoStrategyPayoffFunction) config.payoffFunction).reverseXAxis()) {
            percent_a = 1 - percent_a;
        }
        applet.stroke(0);
        applet.noFill();
        applet.line(0, (1 - percent_A) * height, width, (1 - percent_A) * height);
        applet.line((1 - percent_a) * width, 0, (1 - percent_a) * width, height);
        applet.fill(0);

        current.update((1 - percent_a) * width, (1 - percent_A) * height);
        if (config.showHeatmap) {
            current.setLabel(PayoffUtils.getPayoff());
        }

        if (applet.mousePressed) {
            dragged.setVisible(true);
            float target;
            if (aboveRect(applet.mouseY)) {
                target = 1;
            } else if (belowRect(applet.mouseY)) {
                target = 0;
            } else {
                target = 1 - ((applet.mouseY - origin.y) / height);
            }
            dragged.update((1 - percent_a) * width, (1 - target) * height);
            float hoverPercent_A = 1 - ((applet.mouseY - origin.y) / height);
            if (config.showHeatmap) {
                dragged.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}));
            }
        } else {
            dragged.setVisible(false);
            dragged.update((1 - percent_a) * width, dragged.origin.y);
        }

        if (Client.state.target != null && percent_A == Client.state.target[0]) {
            planned.setVisible(true);
            planned.update(
                    (1 - percent_a) * width,
                    (1 - Client.state.target[0]) * height);
            if (config.showHeatmap) {
                planned.setLabel(PayoffUtils.getPayoff(new float[]{Client.state.target[0]}));
            }
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
                if (config.showHeatmap) {
                    if (((TwoStrategyPayoffFunction) config.payoffFunction).reverseXAxis()) {
                        hover.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}, new float[]{1 - hoverPercent_a}));
                    } else {
                        hover.setLabel(PayoffUtils.getPayoff(new float[]{hoverPercent_A}, new float[]{hoverPercent_a}));
                    }
                }
                hover.update((1 - hoverPercent_a) * width, (1 - hoverPercent_A) * height);
                hover.setVisible(true);
                hover.draw(applet);
            }
        }
        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
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
            if (config.showHeatmap) {
                counterpart.setLabel(PayoffUtils.getMatchPayoff());
            }
            counterpart.update(
                    counterpartHeatmap.origin.x + (1 - percent_a) * counterpartHeatmap.width,
                    counterpartHeatmap.origin.y + (1 - percent_A) * counterpartHeatmap.height);
            counterpart.draw(applet);
        }
    }

    private void drawHeatmap() {
        heatmap.draw(applet);

        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
            counterpartHeatmap.draw(applet);
        }

        myHeatmapAa.draw(applet);
        myHeatmapAb.draw(applet);
        myHeatmapBa.draw(applet);
        myHeatmapBb.draw(applet);

        if (config.matrixDisplay == Config.MatrixDisplayType.HeatmapBoth) {
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

        if (applet.mousePressed) {
            updateTarget(applet.mouseY);
        }

        if (applet.frameCount % applet.framesPerUpdate == 0 && FIRE.client.getConfig().subperiods == 0) {
            updateHeatmaps();
        }
        if (!inRect(applet.mouseX, applet.mouseY) || applet.pmouseX != applet.mouseX || applet.pmouseY != applet.mouseY) {
            hoverTimestamp = System.currentTimeMillis();
            hover.setVisible(false);
        }
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);

        drawHeatmap();
        drawStrategyInfo();

        applet.popMatrix();
    }

    private boolean inRect(int x, int y) {
        return x >= origin.x && x <= origin.x + width && y >= origin.y && y <= origin.y + height;
    }

    private boolean aboveRect(int y) {
        return y < origin.y;
    }

    private boolean belowRect(int y) {
        return y > origin.y + height;
    }

    private void updateTarget(int mouseY) {
        float target;
        if (aboveRect(mouseY)) {
            target = 1;
        } else if (belowRect(mouseY)) {
            target = 0;
        } else {
            target = 1 - ((mouseY - origin.y) / height);
        }
        if (Client.state.target != null) {
            Client.state.target[0] = target;
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (!enabled) {
            return;
        }
        updateTarget(e.getY());
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (!enabled) {
            return;
        }
        updateTarget(e.getY());
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (!enabled) {
            return;
        }
        if (e.isActionKey()) {
            float newTarget = Client.state.target[0];
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                newTarget += 0.01f;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                newTarget -= 0.01f;
            }
            if (newTarget >= 0 && newTarget <= 1) {
                Client.state.target[0] = newTarget;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void configChanged(Config config) {
        this.config = config;
        if (config.selector == Config.StrategySelector.heatmap2d) {
            switch (config.matrixDisplay) {
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
        heatmap.reset();
        heatmap.updateTwoStrategyHeatmap();
        updateLabels();
    }

    public void endSubperiod(int subperiod) {
        updateHeatmaps();
    }
}
