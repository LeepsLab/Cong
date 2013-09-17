package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.config.StrategySelectionDisplayType;
import edu.ucsc.leeps.fire.cong.server.ThreeStrategyPayoffFunction;

/**
 *
 * @author alexlou
 */
public class HeatmapLegend extends Sprite implements Configurable<Config> {

    private HeatmapHelper heatmap;

    /**
     * Creates a legend for the Heatmap. Starts at x and the sum of y and half
     * the height, and goes to width and 90 percent of the height.
     *
     * Adds a configListener.
     *
     * @param parent
     * @param x x-coordinate
     * @param y y-coordinate
     * @param width width of applet
     * @param height height of applet
     */
    public HeatmapLegend(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, Math.round(y + .05f * height), width, Math.round(.9f * height));
        FIRE.client.addConfigListener(this);
    }

    /**
     * If not visible, return. Translate origin to x and y. Set stroke weight to
     * 1. Create a new heatmap, where it is a parent, located at the new origin,
     * has a width and height of 0, is for the user and is an applet. For each
     * y-coordinate of heatmap helper less than height of HeatmapLegend,
     * calculate percent by subtracting y from .999 and dividing the results by
     * height of HeatmapLegend. Use this percentage to get the RGB value from
     * heatmap.Draw a  horizontal line from 0 and y to width and y.
     * @param applet HeatmapLegend
     */
    @Override
    public void draw(Client applet) {
        if (!visible) {
            return;
        }
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        applet.strokeWeight(1f);
        heatmap = new HeatmapHelper(this, 0, 0, 0, 0, true, applet);
        for (float y = 0; y < this.height; y++) {
            float percent = .999f - y / this.height;
            applet.stroke(heatmap.getRGB(percent));
            applet.line(0, y, (float) this.width, y);
        }
        applet.popMatrix();
    }

    public void configChanged(Config config) {
        if ((config.strategySelectionDisplayType == StrategySelectionDisplayType.HeatmapBoth ||
                config.strategySelectionDisplayType == StrategySelectionDisplayType.HeatmapSingle ||
                config.payoffFunction instanceof ThreeStrategyPayoffFunction) &&
                config.showHeatmapLegend) {

             setVisible(true);
        } else {
            setVisible(false);
        }
    }
}
