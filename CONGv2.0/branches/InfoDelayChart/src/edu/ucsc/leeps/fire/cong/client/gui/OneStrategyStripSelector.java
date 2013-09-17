package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger.Selector;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PricingPayoffFunction;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author swolpert
 */
public class OneStrategyStripSelector extends Sprite implements Configurable<Config>, Selector, MouseListener, KeyListener {

    private Client applet;
    private boolean enabled;
    private Slider slider;
    private Config config;

    /**
     * Creates a strip strategy selector for use with a one-strategy payoff
     * function. Creates an applet. Uses HeatmapHelper to create a heatmap
     * located at the origin with some width and height. Sets heatmap to visible.
     * Uses Marker to create a new marker.
     * If width is greater than height, the slider is aligned horizontally.
     *
     * @param parent
     * @param x
     * @param y
     * @param width
     * @param height
     * @param applet
     * @param strategyChanger
     */
    public OneStrategyStripSelector(Sprite parent, int x, int y, int width, int height,
            Client applet) {
        super(parent, x, y, width, height);
        if (width > height) {
            slider = new Slider(applet, Slider.Alignment.Horizontal, 0, width, height / 2f, Color.black, "A", 1f);
        } else {
            slider = new Slider(applet, Slider.Alignment.Vertical, 0, height, width / 2f, Color.black, "A", 1f);
        }
        this.applet = applet;
        FIRE.client.addConfigListener(this);
        applet.addMouseListener(this);
        applet.addKeyListener(this);
    }

    /**
     * If enabled, set the strategy strip selector to enabled.
     *
     * @param enabled boolean enabling the strategy selector
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        slider.setVisible(visible);
    }

    /**
     * If not visible, return.
     * Translate the applet to the origin.Use heatmap to draw applet. If slider
     * is enabled and slider is grabbed, mouseX() and mouseY are the respective x
     * and y coordinates in the applet minus the origin coordinates.
     *
     * If strip is horizontal, slider moves along the x-axis. If the strip is
     * vertical, move the slider along the y-axis, based on mouse movement.
     *
     * If strip is enabled and heatmap is visible, and strip is hit at mouseX() and
     * mouseY, set a hover of user's strategy. Update mouse positions relative to
     * the origin of the applet. If width is greater than height, the hover is
     * the quotient of the difference of the mouse's x-position and the width.
     * Otherwise, the hover is one minus the quotient of the difference of the
     * mouses's y-coordinate and the origin and the height. In other words, it
     * is the percentage of wither the x or y component as decided by the
     * orientation of the strategy selector.
     *
     * Set hover label to payoff function using current percent, user's hover
     * value and opponent's strategy.  Otherwise, labels are not visible.
     * Update labels with user's payoff, counterpart's payoff, target and current
     * payoffs.
     *
     * If heatmap is visible, draw a box around it using a black line 1 pixel in
     * width.
     *
     * Draw slider and hover.
     * 
     * @param applet  the strategy selector strip
     */
    @Override
    public void draw(Client applet) {
        if (!visible) {
            return;
        }

        if (Client.state.getMyStrategy() != null) {
            if (config.subperiods == 0) {
                slider.setStratValue(Client.state.getMyStrategy()[0]);
            } else {
                slider.setStratValue(Client.state.target[0]);
            }
        }

        float newTarget = slider.getGhostValue();
        float newPrice =
                (newTarget * FIRE.client.getConfig().payoffFunction.getMax()) - FIRE.client.getConfig().payoffFunction.getMin();
        if (newPrice < FIRE.client.getConfig().marginalCost) {
            float marginalCostTarget = FIRE.client.getConfig().marginalCost / (FIRE.client.getConfig().payoffFunction.getMax() - FIRE.client.getConfig().payoffFunction.getMin());
            slider.setGhostValue(marginalCostTarget);
            Client.state.setTarget(new float[]{marginalCostTarget}, config);
        } else {
            Client.state.setTarget(new float[]{newTarget}, config);
        }

        if (enabled && slider.isGhostGrabbed()) {
            float mouseX = applet.mouseX - origin.x;
            float mouseY = applet.mouseY - origin.y;

            if (!Float.isNaN(config.grid)) {
                float ghostValue = slider.getGhostValue();
                float r = ghostValue % config.grid;
                if (r > config.grid / 2f) {
                    ghostValue -= r;
                    ghostValue += config.grid;
                } else {
                    ghostValue -= r;
                }
                slider.setStratValue(ghostValue);
            }

            if (slider.getAlignment() == Slider.Alignment.Horizontal) {
                slider.moveGhost(mouseX);
            } else {
                slider.moveGhost(mouseY);
            }
        }

        applet.pushMatrix();
        applet.translate(origin.x, origin.y);

        slider.draw(applet);

        applet.popMatrix();
    }

    public void configChanged(Config config) {
        this.config = config;
        if (config.selector == Config.StrategySelector.strip) {
            setVisible(true);
            if (config.payoffFunction instanceof PricingPayoffFunction) { //payoff function dependent
                slider = new Slider(
                        applet,
                        Slider.Alignment.Vertical, height * 0.05f, height * 0.94f, width / 2f, Color.black, "", 1f);
                slider.setVisible(true);
            }
            if (Float.isNaN(config.percentChangePerSecond)) {
                slider.hideGhost();
            } else {
                slider.showGhost();
            }
        } else {
            setVisible(false);
        }
    }

    public void startPrePeriod() {
    }

    public void startPeriod() {
        slider.setStratValue(Client.state.getMyStrategy()[0]);
        slider.setGhostValue(slider.getStratValue());
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (enabled) {
            slider.grabGhost();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (enabled) {
            if (slider.isGhostGrabbed()) {
                slider.releaseGhost();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (!visible || !enabled) {
            return;
        }
        if (e.isActionKey()) {
            float newTarget = slider.getGhostValue();
            float grid = config.grid;
            if (Float.isNaN(grid)) {
                grid = 0.01f;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                newTarget += grid;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                newTarget -= grid;
            }
            newTarget = Client.constrain(newTarget, 0, 1);
            float max = FIRE.client.getConfig().payoffFunction.getMax();
            float min = FIRE.client.getConfig().payoffFunction.getMin();
            float mc = FIRE.client.getConfig().marginalCost;
            float newPrice = min + newTarget * (max - min);
            if (newPrice < mc) {
                newTarget = mc / (max - min);
            }
            Client.state.target[0] = newTarget;
            slider.setGhostValue(Client.state.target[0]);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void endSubperiod(int subperiod) {
    }
}
