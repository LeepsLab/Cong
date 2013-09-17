package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffFunction;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author jpettit
 */
public class HeatmapHelper extends Sprite implements Configurable<Config> {

    private Config config;
    private PImage currentBuffer;
    private PImage backBuffer;
    private float[][][] RPSCache;
    private boolean mine;
    private PApplet applet;
    private List<Integer> colors;

    /**
     * Creates HeatmapHelper. Creates an array list of colors. Colors correspond
     * to weather maps, with purple being the coldest and red being the warmest.
     * Adds a configListener.
     *
     * @param parent
     * @param x
     * @param y
     * @param width
     * @param height
     * @param mine belonging to subject as opposed to counterpart?
     * @param applet
     */
    public HeatmapHelper(Sprite parent, int x, int y, int width, int height,
            boolean mine,
            PApplet applet) {
        super(parent, x, y, width, height);
        this.width = width;
        this.height = height;
        this.mine = mine;
        this.applet = applet;

        colors = new ArrayList<Integer>();
        colors.add(0xFFC24FED); // dark purple
        colors.add(0xFFE4B2FF); // light purple
        colors.add(0xFF214DC1); // dark blue
        colors.add(0xFF4099F5); // light blue
        colors.add(0xFF90C7FF); // sky blue
        colors.add(0xFF4DED43); // bright green
        colors.add(0xFFFAF567); // pastel yellow
        colors.add(0xFFF57023); // orange
        colors.add(0xFFEA3E05); // red

        FIRE.client.addConfigListener(this);
    }

    public void configChanged(Config config) {
        this.config = config;
    }

    public int getRGB(float percent) {
        if (FIRE.client.getConfig().sigmoidHeatmap) {
            percent = sigmoid(percent, FIRE.client.getConfig().sigmoidAlpha, FIRE.client.getConfig().sigmoidBeta);
        }
        int floorIndex = PApplet.floor((colors.size() - 1) * percent);
        int ceilIndex = floorIndex + 1;
        floorIndex = floorIndex < 0 ? 0 : floorIndex;
        ceilIndex = ceilIndex >= colors.size() ? colors.size() - 1 : ceilIndex;
        int colorFloor = colors.get(floorIndex);
        int colorCeil = colors.get(ceilIndex);
        float ppf = 1 / (float) (colors.size() - 1);
        float amt = (percent - (ppf * floorIndex)) / ppf;
        int c = applet.lerpColor(colorFloor, colorCeil, amt);
        return c;
    }

    private float sigmoid(float x, float a, float b) {
        x = PApplet.map(x, 0, 1, -10, 10);
        return 1f / (1 + (float) Math.exp(-a * (x - b)));
    }

    public void updateTwoStrategyHeatmap(float currentPercent) {
        int size = 100;
        PayoffFunction u;
        if (mine) {
            u = config.payoffFunction;
        } else {
            u = config.counterpartPayoffFunction;
        }
        float max = u.getMax();
        if (backBuffer == null || backBuffer.width != size) {
            backBuffer = applet.createImage(size, size, Client.RGB);
        }
        backBuffer.loadPixels();
        float[] you = new float[1];
        float[] other = new float[1];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float A = 1 - (y / (float) size);
                float a = 1 - (x / (float) size);
                float value;
                you[0] = A;
                other[0] = a;
                if (mine) {
                    value = u.getPayoff(currentPercent, you, other) / max;
                } else {
                    value = u.getPayoff(currentPercent, other, you) / max;
                }
                backBuffer.pixels[y * size + x] = getRGB(value);
            }
        }
        backBuffer.updatePixels();
        backBuffer.resize(width, height);
        PImage tmp = currentBuffer;
        currentBuffer = backBuffer;
        backBuffer = tmp;
    }

    public void updateThreeStrategyHeatmap(
            float currentPercent,
            float r, float p, float s,
            ThreeStrategySelector threeStrategySelector) {
        if (r < 0 || p < 0 || s < 0) {
            currentBuffer = null;
            return;
        }
        backBuffer = applet.createGraphics(width, height, Client.P2D);
        backBuffer.loadPixels();
        PayoffFunction payoffFunction;
        if (mine) {
            payoffFunction = config.payoffFunction;
        } else {
            payoffFunction = config.counterpartPayoffFunction;
        }
        float max = payoffFunction.getMax();
        float[] rps = new float[]{r, p, s};
        if (RPSCache == null) {
            RPSCache = new float[width][height][3];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    float[] RPS = threeStrategySelector.translate(x, height - y);
                    RPSCache[x][y][0] = RPS[0];
                    RPSCache[x][y][1] = RPS[1];
                    RPSCache[x][y][2] = RPS[2];
                }
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float[] RPS = RPSCache[x][y];
                if (RPS[0] >= 0 && RPS[1] >= 0 && RPS[2] >= 0) {
                    float u = payoffFunction.getPayoff(
                            currentPercent,
                            RPS, rps);
                    backBuffer.pixels[y * width + x] = getRGB(u / max);
                } else {
                    backBuffer.pixels[y * width + x] = applet.color(255, 0, 0, 0);
                }
            }
        }
        backBuffer.updatePixels();
        currentBuffer = backBuffer;
    }

    public void updateStripHeatmap(
            float currentPercent,
            float opponentStrat) {
        backBuffer = applet.createGraphics(width, height, Client.P2D);
        backBuffer.loadPixels();

        PayoffFunction payoffFunction = config.payoffFunction;
        float u;
        float max = payoffFunction.getMax();

        if (height > width) {
            for (int i = 0; i < backBuffer.pixels.length; i += width) {
                int y = i / width;

                float myStrat = 1f - ((float) y / (float) height);
                u = payoffFunction.getPayoff(currentPercent,
                        new float[]{myStrat},
                        new float[]{opponentStrat});

                backBuffer.pixels[i] = getRGB(u / max);
            }

            for (int y = 0; y < height; ++y) {
                for (int x = 1; x < width; ++x) {
                    backBuffer.pixels[y * width + x] = backBuffer.pixels[y * width];
                }
            }
        } else {
            for (int i = 0; i / width == 0; ++i) {
                float myStrat = (float) i / (float) width;
                u = payoffFunction.getPayoff(currentPercent,
                        new float[]{myStrat},
                        new float[]{opponentStrat});

                backBuffer.pixels[i] = getRGB(u / max);
            }

            for (int x = 0; x < width; ++x) {
                for (int y = 1; y < height; ++y) {
                    backBuffer.pixels[y * width + x] = backBuffer.pixels[x];
                }
            }
        }

        backBuffer.updatePixels();
        currentBuffer = backBuffer;
    }

    @Override
    public void draw(Client applet) {
        if (!visible) {
            return;
        }
        if (currentBuffer != null) {
            applet.imageMode(Client.CORNER);
            applet.image(currentBuffer, origin.x, origin.y);
        }
    }
}
