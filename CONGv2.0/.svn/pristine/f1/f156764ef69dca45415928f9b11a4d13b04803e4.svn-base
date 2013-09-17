package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger.Selector;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import processing.core.PApplet;

/**
 *
 * @author swolpert
 */
public class PureStrategySelector extends Sprite implements Configurable<Config>, KeyListener, Selector {

    private final int BUTTON_RADIUS = 15;
    private Client applet;
    private Marker matrixTopLeft;
    private Marker matrixTopRight;
    private Marker matrixBotLeft;
    private Marker matrixBotRight;
    private float matrixSideLength;
    private Marker matrixLabel;
    private Marker[][] cellMarkers;
    private Marker[] columnLabels;
    private RadioButtonGroup buttons;

    /**
     *
     * @param parent
     * @param x
     * @param y
     * @param size
     * @param applet
     * @param strategyChanger
     */
    public PureStrategySelector(Sprite parent, int x, int y, int size,
            Client applet) {
        super(parent, x, y, size, size);
        visible = false;
        this.applet = applet;

        matrixTopLeft = new Marker(this, width / 4, width / 8, true, 0);
        matrixTopRight = new Marker(this, width, width / 8, true, 0);
        matrixBotLeft = new Marker(this, width / 4, 7 * (width / 8), true, 0);
        matrixBotRight = new Marker(this, width, 7 * (width / 8), true, 0);

        matrixSideLength = matrixTopRight.origin.x - matrixTopLeft.origin.x;

        matrixLabel = new Marker(this, matrixTopLeft.origin.x + matrixSideLength / 2,
                matrixTopLeft.origin.y - (applet.textAscent() + applet.textDescent()),
                true, 0);
        matrixLabel.setLabelMode(Marker.LabelMode.Top);
        matrixLabel.setLabel("Matrix");

        FIRE.client.addConfigListener(this);
    }

    @Override
    public void draw(Client applet) {
        if (visible) {

            updateLabels();

            int selection = buttons.getSelection();

            if (Client.state.target != null) {
                if (selection == 0) {
                    Client.state.target[0] = 1;
                } else {
                    Client.state.target[0] = 0;
                }
            }

            applet.pushMatrix();
            applet.translate(origin.x, origin.y);

            String groupSizeString = String.format(
                    "%d players are in your group",
                    FIRE.client.getConfig().playersInGroup);
            applet.text(groupSizeString, 0, 0);

            if (FIRE.client.getConfig().showMatrix) {

                int numStrategies = FIRE.client.getConfig().payoffFunction.getNumStrategies();

                matrixLabel.draw(applet);
                for (int i = 0; i < columnLabels.length; ++i) {
                    columnLabels[i].draw(applet);
                }

                float interval = matrixSideLength / (float) numStrategies;

                applet.noStroke();
                applet.fill(0, 95, 205, 125);
                applet.rectMode(PApplet.CORNER);

                int playedStrat = buttons.getSelection();
                applet.rect(matrixTopLeft.origin.x, matrixTopLeft.origin.y + playedStrat * interval, matrixSideLength, interval);
                int cpStrat = PayoffUtils.getAverageMatchStrategy()[0] == 1 ? 0 : 1;
                applet.rect(matrixTopLeft.origin.x + cpStrat * interval, matrixTopLeft.origin.y, interval, matrixSideLength);
                /*
                if (payoffFunction instanceof ThresholdPayoffFunction) { //payoff function dependent
                if (((ThresholdPayoffFunction) payoffFunction).thresholdMet(Client.state.currentPercent, myStrategy, averageMatch)) {
                applet.rect(matrixTopLeft.origin.x, matrixTopLeft.origin.y, interval, matrixSideLength);
                } else {
                applet.rect(matrixTopLeft.origin.x + interval, matrixTopLeft.origin.y, interval, matrixSideLength);
                }
                } else {
                int i;
                for (i = 0; i < numStrategies.length; ++i) {
                if (counterpart[i] > 0) {
                break;
                }
                }
                // FIXME Only do when playing versus a pure strategy
                //applet.rect(matrixTopLeft.origin.x + i * interval, matrixTopLeft.origin.y, interval, matrixSideLength);
                }
                 */

                applet.stroke(0);
                applet.strokeWeight(2);

                applet.line(matrixTopLeft.origin.x, matrixTopLeft.origin.y, matrixTopRight.origin.x, matrixTopRight.origin.y);
                applet.line(matrixTopRight.origin.x, matrixTopRight.origin.y, matrixBotRight.origin.x, matrixBotRight.origin.y);
                applet.line(matrixBotLeft.origin.x, matrixBotLeft.origin.y, matrixBotRight.origin.x, matrixBotRight.origin.y);
                applet.line(matrixTopLeft.origin.x, matrixTopLeft.origin.y, matrixBotLeft.origin.x, matrixBotLeft.origin.y);

                for (int i = 1; i < numStrategies; ++i) {
                    float x = matrixTopLeft.origin.x + i * interval;
                    float y = matrixTopLeft.origin.y + i * interval;
                    applet.line(x, matrixTopLeft.origin.y, x, matrixBotLeft.origin.y);
                    applet.line(matrixTopLeft.origin.x, y, matrixTopRight.origin.x, y);
                }

                for (int i = 0; i < cellMarkers.length; ++i) {
                    for (int j = 0; j < cellMarkers[i].length; ++j) {
                        cellMarkers[i][j].draw(applet);
                    }
                }
            }

            buttons.draw(applet);

            applet.popMatrix();
        }
    }

    @Override
    public void setVisible(boolean isVisible) {
        visible = isVisible;
        if (isVisible) {
            applet.addKeyListener(this);
        } else {
            applet.removeKeyListener(this);
        }
        buttons.setVisible(isVisible);
    }

    public void setEnabled(boolean enabled) {
        buttons.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return buttons.isEnabled();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (!visible || !buttons.isEnabled()) {
            return;
        }
        if (e.isActionKey()) {
            int selection = buttons.getSelection();
            if ((buttons.getAlignment() == RadioButtonGroup.Alignment.Vertical
                    && e.getKeyCode() == KeyEvent.VK_DOWN)
                    || (buttons.getAlignment() == RadioButtonGroup.Alignment.Horizontal
                    && e.getKeyCode() == KeyEvent.VK_RIGHT)) {
                if (selection < buttons.getNumButtons() - 1) {
                    selection++;
                } else {
                    return;
                }
            } else if ((buttons.getAlignment() == RadioButtonGroup.Alignment.Vertical
                    && e.getKeyCode() == KeyEvent.VK_UP)
                    || (buttons.getAlignment() == RadioButtonGroup.Alignment.Horizontal
                    && e.getKeyCode() == KeyEvent.VK_LEFT)) {
                if (selection > 0) {
                    selection--;
                } else {
                    return;
                }
            }
            buttons.setSelection(selection);
            if (selection != RadioButtonGroup.NO_BUTTON) {
                if (selection == 0) {
                    Client.state.target[0] = 1;
                } else {
                    Client.state.target[0] = 0;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void configChanged(Config config) {

        int numStrategies = 0;
        if (config.payoffFunction.getNumStrategies() <= 2) {
            numStrategies = 2;
        } else if (config.payoffFunction.getNumStrategies() == 3) {
            numStrategies = 3;
        }

        if (numStrategies != 0) {

            matrixLabel.setVisible(false);

            cellMarkers = new Marker[numStrategies][numStrategies];
            float interval = matrixSideLength / (numStrategies * 2f);
            int offsetX = 1;
            int offsetY = 1;
            for (int i = 0; i < numStrategies; ++i) {
                offsetX = 1;
                for (int j = 0; j < numStrategies; ++j) {
                    cellMarkers[i][j] = new Marker(this, matrixTopLeft.origin.x + (j + offsetX) * interval,
                            matrixTopLeft.origin.y + (i + offsetY) * interval, true, 0);
                    cellMarkers[i][j].setLabelMode(Marker.LabelMode.Bottom);
                    ++offsetX;
                }
                ++offsetY;
            }

            columnLabels = new Marker[numStrategies];
            offsetX = 1;
            for (int i = 0; i < numStrategies; ++i) {
                columnLabels[i] = new Marker(this, matrixBotLeft.origin.x + (i + offsetX) * interval,
                        matrixBotLeft.origin.y + applet.textAscent() + applet.textDescent(),
                        true, 0);
                columnLabels[i].setLabelMode(Marker.LabelMode.Bottom);
                ++offsetX;
            }

            buttons = new RadioButtonGroup(this, BUTTON_RADIUS, matrixTopLeft.origin.y,
                    (int) matrixSideLength, numStrategies,
                    RadioButtonGroup.Alignment.Vertical, BUTTON_RADIUS, applet);
            buttons.setLabelMode(Marker.LabelMode.Right);

            if (numStrategies == 2) {
                buttons.setLabels(new String[]{"A", "B"});
                columnLabels[0].setLabel("a");
                columnLabels[1].setLabel("b");
            } else if (numStrategies == 3) {
                buttons.setLabels(new String[]{"A", "B", "C"});
                columnLabels[0].setLabel("a");
                columnLabels[1].setLabel("b");
                columnLabels[2].setLabel("c");

            }
            buttons.setEnabled(false);
        }

        if (config.selector == Config.StrategySelector.pure) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    private void updateLabels() {
        float[] myStrategy = new float[cellMarkers.length];
        float[] opponentStrategy = new float[cellMarkers.length];

        for (int i = 0; i < cellMarkers.length; ++i) {
            for (int j = 0; j < cellMarkers[i].length; ++j) {
                for (int k = 0; k < cellMarkers.length; ++k) {
                    myStrategy[k] = 0f;
                    opponentStrategy[k] = 0f;
                }

                myStrategy[i] = 1f;
                opponentStrategy[j] = 1f;

                float myPayoff = PayoffUtils.getPayoff(myStrategy, opponentStrategy);
                float opponentPayoff = PayoffUtils.getMatchPayoff(myStrategy, opponentStrategy);

                cellMarkers[i][j].setLabel(myPayoff, opponentPayoff);
            }
        }
    }

    public void startPrePeriod() {
    }

    public void startPeriod() {
        if (Client.state.getMyStrategy()[0] == 1) {
            buttons.setSelection(0);
        } else {
            buttons.setSelection(1);
        }
    }

    public void endSubperiod(int subperiod) {
    }
}
