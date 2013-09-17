package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public class IndefiniteEndPricesChart extends Sprite implements Configurable<Config> {

    private Config config;
    private float umax, umin;
    private float scaledHeight;
    private int scaledMargin;
    private List<Map<Integer, Float>> subperiodPrices;
    private List<Float> subperiodProfits;
    private Map<Integer, Integer> colors;

    public IndefiniteEndPricesChart(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        FIRE.client.addConfigListener(this);
        scaledHeight = Math.round(0.9f * height);
        scaledMargin = Math.round((height - scaledHeight) / 2f);
    }

    @Override
    public void draw(Client a) {
        if (config == null || !visible) {
            return;
        }
        a.pushMatrix();
        try {
            a.rectMode(Client.CORNERS);
            a.translate(origin.x, origin.y);
            int maxSubperiodToDisplay = Math.round((config.indefiniteEnd.displayLength * config.indefiniteEnd.percentToDisplay) / config.indefiniteEnd.subperiodLength);
            int offset = Client.state.subperiod - maxSubperiodToDisplay;
            if (offset < 0) {
                offset = 0;
            }
            float percentThroughSub = 0;
            if (Client.state.currentPercent >= 0 && Client.state.currentPercent <= 1) {
                float percentPerSub = 1f / config.subperiods;
                float percentElapsed = Client.state.subperiod * percentPerSub;
                float remainder = Client.state.currentPercent - percentElapsed;
                percentThroughSub = remainder / percentPerSub;
            }
            for (int i = 1; i <= maxSubperiodToDisplay; i++) {
                int subperiod = i + offset - 1;
                int x1 = Math.round(width * (i - 1) * ((float) config.indefiniteEnd.subperiodLength / config.indefiniteEnd.displayLength));
                int x2 = Math.round(width * i * ((float) config.indefiniteEnd.subperiodLength / config.indefiniteEnd.displayLength));
                int xoff1 = 0;
                int xoff2 = 0;
                if (Client.state.subperiod >= maxSubperiodToDisplay) {
                    if (i != 1) {
                        xoff1 = Math.round(-(x2 - x1) * percentThroughSub);
                    }
                    xoff2 = Math.round(-(x2 - x1) * percentThroughSub);
                }
                // draw any sub-period locks
                if (config.turnTaking && Client.state.strategyChanger.isTurnTakingLocked(subperiod)) {
                    a.noStroke();
                    a.fill(100, 50);
                    a.rect(x1 + xoff1, 0, x2 + xoff2, height);
                    a.fill(0);
                } else if (config.turnTaking
                        && i == maxSubperiodToDisplay
                        && subperiod != config.subperiods
                        && Client.state.strategyChanger.isTurnTakingLocked(subperiod + 1)
                        && x2 + xoff1 != x2) {
                    a.noStroke();
                    a.fill(100, 50);
                    a.rect(x2 + xoff1, 0, x2, height);
                }

                // draw your profit area
                if (subperiod < subperiodProfits.size()) {
                    float profit = subperiodProfits.get(subperiod);
                    if (profit > 0) {
                        a.fill(profitColor);
                        float y1 = scaledHeight * ((profit + config.marginalCost - umin) / (umax - umin));
                        float y2 = scaledHeight * ((config.marginalCost - umin) / (umax - umin));
                        y1 = scaledHeight - y1;
                        y2 = scaledHeight - y2;
                        a.noStroke();
                        a.rect(x1 + xoff1, y2 + scaledMargin, x2 + xoff2, y1 + scaledMargin);
                    }
                }
                // draw the marginal cost line
                if (subperiod <= Client.state.subperiod) {
                    float y = scaledHeight * ((config.marginalCost - umin) / (umax - umin));
                    y = scaledHeight - y;
                    a.strokeWeight(1);
                    a.stroke(0);
                    a.line(x1 + xoff1, y + scaledMargin, x2 + xoff2, y + scaledMargin);
                }
                // draw the price lines
                if (subperiod < subperiodPrices.size()) {
                    Map<Integer, Float> prices = subperiodPrices.get(subperiod);
                    for (int id : prices.keySet()) {
                        float price = prices.get(id);
                        if (id == Client.state.id) {
                            a.stroke(0);
                        } else {
                            a.stroke(colors.get(id));
                        }
                        float y = scaledHeight * (1 - price);
                        a.strokeWeight(2);
                        a.line(x1 + xoff1, y + scaledMargin, x2 + xoff2, y + scaledMargin);
                    }
                } else if (subperiod - 1 >= 0 && subperiod - 1 < subperiodPrices.size()) {
                    Map<Integer, Float> prices = subperiodPrices.get(subperiod - 1);
                    for (int id : prices.keySet()) {
                        if (StrategyChanger.isTurnTakingLocked(id, subperiod, config)) {
                            float price = prices.get(id);
                            if (id == Client.state.id) {
                                a.stroke(0);
                            } else {
                                a.stroke(colors.get(id));
                            }
                            float y = scaledHeight * (1 - price);
                            a.strokeWeight(2);
                            a.line(x1 + xoff1, y + scaledMargin, x2 + xoff2, y + scaledMargin);
                        }
                    }
                }
                // draw the subperiod divider
                a.strokeWeight(1);
                a.stroke(0);
                a.line(x2 + xoff2, 0, x2 + xoff2, height);
                if (Client.state.subperiod >= maxSubperiodToDisplay && i == maxSubperiodToDisplay) {
                    a.line(x1, 0, x1, height);
                    a.stroke(currentTimeColor);
                    a.line(x2, 0, x2, height);
                }
                if (subperiod == Client.state.subperiod) {
                    a.stroke(currentTimeColor);
                    a.line(x1 + (x2 - x1) * percentThroughSub, 0, x1 + (x2 - x1) * percentThroughSub, height);
                }
                // draw the target preview line
                if (Client.state.target != null) {
                    float target = Client.state.target[0];
                    if (!Float.isNaN(config.grid)) {
                        float r = target % config.grid;
                        if (r > config.grid / 2f) {
                            target -= r;
                            target += config.grid;
                        } else {
                            target -= r;
                        }
                    }
                    int y = height - Math.round(target * scaledHeight) - scaledMargin;
                    a.stroke(100);
                    a.strokeWeight(2);
                    if (subperiod == Client.state.subperiod) {
                        if (!Client.state.strategyChanger.isTurnTakingLocked(subperiod)) {
                            // draw a dashed line at y from x1 to x2
                            for (int x = x1; x <= x2; x++) {
                                if (x % 6 == 0 || x % 6 == 1) {
                                    a.point(x, y);
                                }
                            }
                        } else {
                            a.line(x1, y, x2, y);
                        }
                    } else if (Client.state.subperiod >= maxSubperiodToDisplay && i == maxSubperiodToDisplay) {
                        if (!Client.state.strategyChanger.isTurnTakingLocked(subperiod + 1)) {
                            // draw a dashed line at y from x2+xoff1 to x2
                            for (int x = x2 + xoff1; x <= x2; x++) {
                                if (x % 6 == 0 || x % 6 == 1) {
                                    a.point(x, y);
                                }
                            }
                        } else {
                            a.line(x2 + xoff1, y, x2, y);
                        }
                    }
                }
            }

            // draw the bounding rect
            a.stroke(0);
            a.strokeWeight(2);
            a.noFill();
            a.rect(0, 0, width, height);

            // draw the axis
            a.textAlign(Client.RIGHT, Client.CENTER);
            for (int i = 0; i <= config.xAxisTicks; i++) {
                float p = i / (float)config.xAxisTicks;
                float u = p * (umax - umin);
                a.fill(0);
                a.text(String.format("%.1f", u), -5, Math.round(height - p * scaledHeight - scaledMargin));
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        a.popMatrix();
    }

    public void startPeriod() {
    }

    public void endSubperiod(int subperiod) {
        if (subperiod == 1) {
            colors = new HashMap<Integer, Integer>();
            int i = 0;
            for (int id : Client.state.strategies.keySet()) {
                if (id != Client.state.id) {
                    colors.put(id, colorArray[i++]);
                }
            }
        }
        Map<Integer, Float> currentPrices = new HashMap<Integer, Float>();
        for (int id : Client.state.strategies.keySet()) {
            currentPrices.put(id, Client.state.strategies.get(id)[0]);
        }
        subperiodPrices.add(currentPrices);
        subperiodProfits.add(Client.state.subperiodPayoff);
    }

    public void update() {
    }

    public void clearAll() {
    }

    public void configChanged(Config config) {
        this.config = config;
        umax = config.payoffFunction.getMax();
        umin = config.payoffFunction.getMin();
        subperiodPrices = new ArrayList<Map<Integer, Float>>();
        subperiodProfits = new ArrayList<Float>();
    }
    private final static int[] colorArray = new int[]{
        0xFFE49A3E, // orange
        0xFF3E64E4, // blue
        0xFFBE2F1E, // red
        0xFF56CCD0, // teal
        0xFFA16C1F, // brown
    };
    private final static int profitColor = 0x994EDD43;
    private final static int currentTimeColor = 0xFF00A7D6;
}
