/*
 * Hotelling.java
 * If you want to create an experiment that acts similarly to Hotelling, use
 * this code as an example.
 */

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.gui.PeriodInfo;
import edu.ucsc.leeps.fire.cong.client.gui.Slider;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import edu.ucsc.leeps.fire.cong.server.ScriptedPayoffFunction.PayoffScriptInterface;
import edu.ucsc.leeps.fire.cong.server.SumPayoffFunction;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class HotellingScripted implements PayoffScriptInterface, MouseListener, KeyListener {

    // Variables for setting up and drawing the graph, obtain info
    private boolean enabled = true;
    private boolean setup = false;
    private Slider slider;
    private Config config;
    private float[] subperiodStrategy;
    private PeriodInfo periodInfo;
    // Variables for drawing the dimensions of the graphs
    private float width, height;
    private float scale = 0.7f;     // Scales the size of the graph
    private boolean firstDraw = false;

    // Constructor
    public HotellingScripted() {
        if (FIRE.client != null) {
            config = FIRE.client.getConfig();
        } else if (FIRE.server != null) {
            config = FIRE.server.getConfig();
        }
    }

    // Sets up the periodInfo object to get periodInfo information
    public void setup(Client a, int x, int y) {
        periodInfo = new PeriodInfo(null, x, y, a);
        periodInfo.startPeriod();
    }

    public float getPayoff(
            int id,
            float percent,
            Map<Integer, float[]> popStrategies,
            Map<Integer, float[]> matchPopStrategies,
            Config config) {
        
        if (popStrategies.size() < 2) { // if only 1 person is playing, they get zero
            return 0;
        }

        SortedSet<Float> sorted = new TreeSet<Float>();
        for (float[] s : popStrategies.values()) {
            sorted.add(s[0]);
        }

        // Assigns the strategy at the client ID to s
        float s = popStrategies.get(id)[0];

        SortedSet<Float> leftSide = sorted.headSet(s);
        SortedSet<Float> rightSide = sorted.tailSet(s);
        rightSide.remove(s); // remove s from right side because tailSet is inclusive

        // This code affects slider location, payoff function
        float left, right;
        if (leftSide.isEmpty()) {
            left = 0;
        } else {
            left = leftSide.last();     // left = highest element of this set
        }
        if (rightSide.isEmpty()) {
            right = 1f;
        } else {
            right = rightSide.first();  // right = lowest element of this set
        }

        float u;
        if (left == 0) {
            u = s + 0.5f * (right - s);
        } else if (right == 1f) {
            u = 0.5f * (s - left) + (1 - s);
        } else {
            u = 0.5f * (s - left) + 0.5f * (right - s);
        }

        int shared = 0; // shared must be at least 1 after the loop, as you have to share your own strategy
        for (int otherId : popStrategies.keySet()) {
            if (popStrategies.get(otherId)[0] == s) {
                shared++;
            }
        }
        assert shared >= 1;     // shared must >= 1 for experiment to make sense
        
        // Payoff function for Hotelling
        return config.get("Alpha") * 100 * (u / shared);
    }

    // Draws info related to the periods such as score and time left
    private void drawPeriodInfo(Client a, int x, int y) {
        if (config == null) {
            return;
        }
        // The period info text drawn above the graph
        String s;

        if (config.subperiods != 0) {
            s = String.format("Subperiods Left: %d", config.subperiods - Client.state.subperiod);
        }

        if (config.indefiniteEnd == null) {
            if (config.subperiods != 0) {
                s = String.format("Subperiods Left: %d", config.subperiods - Client.state.subperiod);
            } else {
                s = String.format("Seconds Left: %d", FIRE.client.getMillisLeft() / 1000f);
            }

        } else {
            s = String.format("Seconds Left: %d", FIRE.client.getMillisLeft() / 1000f);

            if (config.subperiods != 0) {
                if (Client.state.subperiod < config.subperiods) {
                    s = String.format("Subperiod: %d", Client.state.subperiod + 1);
                } else {
                    s = String.format("Subperiod: %d", Client.state.subperiod);
                }
            } else {
                s = String.format("Seconds Elapsed: %.0f", ((config.length * 1000) - FIRE.client.getMillisLeft()) / 1000f);
            }

        }

        a.fill(0);
        a.textAlign(Client.LEFT);
        int lineNumber = 0;
        float textHeight = a.textAscent() + a.textDescent();
        a.text(s, x, (y + lineNumber++ * textHeight));
        String totalPointsString = "";
        String periodPointsString = "";
        String multiplierString = "";
        String contributionsString = "";
        totalPointsString = String.format(config.totalPointsString + " %.2f", Client.state.totalPoints);
        periodPointsString = String.format(config.periodPointsString + " %.2f", Client.state.periodPoints);
        a.text(totalPointsString, x, (y + lineNumber++ * textHeight));
        a.text(periodPointsString, x, (y + lineNumber++ * textHeight));
        
        if (FIRE.client.getConfig().showPGMultiplier) {
            multiplierString = String.format("Multipler: %.2f", config.get("Alpha"));
            a.fill(0);
            a.text(multiplierString, x, (y + lineNumber++ * textHeight));
            float contributions = ((SumPayoffFunction) FIRE.client.getConfig().payoffFunction).getContributions(Client.state.strategies);
            contributionsString = String.format("%s: %.2f", FIRE.client.getConfig().contributionsString, contributions);
            a.fill(0);
            a.text(contributionsString, x, (y + lineNumber++ * textHeight));
        }
        if (config.subperiods != 0 && FIRE.client.isRunningPeriod()) {
            //drawSubperiodTicker(applet);
        }
    }

    public void draw(Client a) {
        //xPInfo and yPInfo are x,y coords for the period info drawing
        int xPInfo = 140, yPInfo = 15;

        // On the first iteration of draw, create periodInfo object and start
        // the period
        if (firstDraw == false) {
            setup(a, xPInfo, yPInfo);
            firstDraw = true;
        }

        // Gets the width and height from the Client and multiplies by scale
        width = a.width * scale;
        height = a.height * scale;

        if (!setup && Client.state != null && Client.state.getMyStrategy() != null) {
            slider = new Slider(a, Slider.Alignment.Horizontal,
                    0, a.width * scale, a.height * scale, Color.black, "", 1f);
            slider.setShowStrategyLabel(false);
            slider.hideGhost();
            slider.setOutline(true);
            a.addMouseListener(this);
            a.addKeyListener(this);
            subperiodStrategy = new float[1];
            slider.setStratValue(Client.state.getMyStrategy()[0]);
            slider.setGhostValue(slider.getStratValue());
            setup = true;
        }

        slider.sliderStart = 0;
        slider.sliderEnd = a.width * scale;
        slider.length = a.width * scale;

        if (Client.state.getMyStrategy() != null) {
            slider.setStratValue(Client.state.getMyStrategy()[0]);
        }
        if (Client.state.target != null) {
            slider.setGhostValue(Client.state.target[0]);
        }
        if (config.subperiods != 0 && Client.state.target != null) {
            slider.setStratValue(Client.state.target[0]);
        }

        if (enabled && !config.trajectory && slider.isGhostGrabbed()) {
            float mouseX = a.mouseX * scale;
            slider.moveGhost(mouseX);
            setTarget(slider.getGhostValue());
        }

        drawPeriodInfo(a, xPInfo, yPInfo);

        a.pushMatrix();
        try {
            /**
             * *Debug Stuff
             *
             * a.text("Width: " + a.width + "\nHeight: " + a.height +
             * "\nScreenWidth: " + a.screenWidth + "\nScreenHeight " +
             * a.screenHeight, 200, 60);
             **/
            
            // OpenGL translate
            a.translate(125, 100);

            if (config.potential) {
                drawPotentialPayoffs(a);
            }

            if (config.payoffFunction.getNumStrategies() == 2) {
                drawInOutButtons(a);
            }

            drawAxis(a);
            slider.draw(a);

            int i = 1;
            for (int id : Client.state.strategies.keySet()) {
                Color color;
                if (config.objectiveColors) {
                    color = config.currColors.get(id);
                } else {
                    if (id == FIRE.client.getID()) {
                        color = Config.colors[0];
                    } else {
                        color = Config.colors[i];
                        i++;
                    }
                }
                drawStrategy(a, color, id);
            }
            if (config.subperiods != 0 && FIRE.client.isRunningPeriod()) {
                drawPlannedStrategy(a);
            }
            if (config.objectiveColors) {
                a.fill(config.currColors.get(FIRE.client.getID()).getRGB());
                a.text(config.currAliases.get(FIRE.client.getID()), 0, -10);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        a.popMatrix();

    }

    // getMax and getMin are the min, max values of the graph
    // Use values appropriate to your experiment
    public float getMax() {
        return 100;
    }

    public float getMin() {
        return 0;
    }

    private void drawPotentialPayoffs(Client a) {
        float[] s = {0};
        float max = config.payoffFunction.getMax();
        a.stroke(50);
        for (float x = 0; x < a.width; x++) {
            s[0] = x / a.width;
            float u = PayoffUtils.getPayoff(s);
            float y = u / max;
            a.point(x * scale, ((a.height * (1 - y))) * scale);
        }
    }

    private void drawInOutButtons(Client a) {
        a.stroke(0, 0, 0, 255);     // RGBA
        a.rectMode(Client.CORNERS);
        a.strokeWeight(3);
        a.fill(255, 255, 255, 255);
        a.rect(a.width * scale - 100, -5, a.width * scale, - 30);
        a.rect(a.width * scale - 205, -5, a.width * scale - 105, -30);
        a.fill(0, 0, 0, 100);
        
        if (Client.state.getMyStrategy() != null) {
            if (Client.state.getMyStrategy()[1] == 0) {
                a.rect(a.width * scale - 100, -5, a.width * scale, -30);
            } else {
                a.rect(a.width * scale - 205, -5, a.width * scale - 105, -30);
            }
        }
        a.fill(0, 0, 0, 255);
        a.textAlign(Client.CENTER, Client.CENTER);
        float in_w = a.textWidth(config.inString);
        float out_w = a.textWidth(config.outString);
        float h = a.textAscent() + a.textDescent();
        a.text(config.outString, a.width * scale - 50 - out_w / 2, -25 + h / 2);
        a.text(config.inString, a.width * scale - 150 - in_w / 2, -25 + h / 2);
    }

    private void drawPlannedStrategy(Client a) {
        a.stroke(0, 0, 0, 20);
        a.line(a.width * scale * Client.state.target[0], 0, a.width * scale * Client.state.target[0], a.height * scale);
    }

    private void drawStrategy(Client applet, Color color, int id) {
        if (Client.state.strategiesTime.size() < 1) {
            return;
        }
        float x, y, min, max;
        min = config.payoffFunction.getMin();
        max = config.payoffFunction.getMax();
        float payoff;
        float[] strategy;
        if (config.subperiods != 0) {
            payoff = config.payoffFunction.getPayoff(
                    id, 0, Client.state.getFictitiousStrategies(FIRE.client.getID(), subperiodStrategy), null, config);
            if (!Client.state.strategiesTime.isEmpty()) {
                strategy = Client.state.strategiesTime.get(Client.state.strategiesTime.size() - 1).strategies.get(id);
            } else {
                strategy = config.initialStrategy;
            }
        } else {
            strategy = Client.state.strategies.get(id);
            payoff = PayoffUtils.getPayoff(id, strategy);
        }
        x = applet.width * scale * strategy[0];
        y = applet.height * scale * (1 - (payoff - min) / (max - min));
        if (y > applet.height * scale) {
            y = applet.height * scale;
        } else if (y < 0) {
            y = 0;
        }
        applet.stroke(color.getRed(), color.getGreen(), color.getBlue());
        applet.fill(color.getRed(), color.getGreen(), color.getBlue());
        if (id != FIRE.client.getID() && config.subperiods == 0 || Client.state.subperiod != 0) {
            applet.strokeWeight(3);
            applet.line(x, applet.height * scale - 5, x, applet.height * scale + 5);
        }
        if (config.subperiods == 0 || Client.state.subperiod != 0) {
            applet.strokeWeight(1);
            if (id == FIRE.client.getID()) {
                applet.ellipse(x, y, 11, 11);
            } else {
                applet.ellipse(x, y, 8, 8);
            }
        }
        if (config.subperiods == 0 || Client.state.subperiod != 0) {
            applet.textAlign(Client.RIGHT, Client.CENTER);
            String label = String.format("%.1f", payoff);
            applet.text(label, Math.round(x - 5), Math.round(y - 6));
        }
        if (payoff > max && (config.subperiods == 0 || Client.state.subperiod != 0)) {
            drawUpArrow(applet, color, x);
        } else if (payoff < min && (config.subperiods == 0 || Client.state.subperiod != 0)) {
            drawDownArrow(applet, color, x);
        }
    }

    private void drawUpArrow(Client applet, Color color, float x) {
        applet.strokeWeight(3f);
        applet.line(x, -22, x, -10);
        applet.noStroke();
        applet.triangle(x - 5, -20, x, -30, x + 5, -20);
    }

    private void drawDownArrow(Client applet, Color color, float x) {
        applet.strokeWeight(3f);
        applet.line(x, applet.height * scale + 10, x, applet.height * scale + 22);
        applet.noStroke();
        applet.triangle(x - 5, applet.height * scale + 20, x, applet.height * scale + 30, x + 5, applet.height * scale + 20);
    }

    private void drawAxis(Client applet) {
        float min, max;
        min = config.payoffFunction.getMin();
        max = config.payoffFunction.getMax();
        applet.rectMode(Client.CORNER);
        applet.noFill();
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.rect(0, 0, applet.width * scale, applet.height * scale);

        applet.textAlign(Client.CENTER, Client.CENTER);
        applet.fill(255);
        applet.noStroke();
        applet.rect(-40, 0, 38, applet.height * scale);
        applet.rect(0, applet.height * scale + 2, applet.width * scale, 40);
        String maxPayoffLabel = String.format("%.1f", max);
        float labelX = 10 + applet.width * scale + 1.1f * applet.textWidth(maxPayoffLabel) / 2f;
        
        for (float y = 0.0f; y <= 1.01f; y += 0.1f) {
            applet.noFill();
            applet.stroke(100, 100, 100);
            applet.strokeWeight(2);
            float x0, y0, x1, y1;
            x0 = 0;
            y0 = y * applet.height * scale;
            x1 = applet.width * scale + 10;
            y1 = y * applet.height * scale;
            applet.stroke(100, 100, 100, 50);
            applet.line(x0, y0, x1, y1);
            float payoff = (1 - y) * (max - min) + min;
            if (payoff < 0) {
                payoff = 0f;
            }
            applet.fill(0);
            String label = String.format("%.1f", payoff);
            applet.text(label, Math.round(labelX), Math.round(y0));
        }
        if (config.payoffFunction instanceof SumPayoffFunction && config.showSMinMax) { //payoff function dependent
            SumPayoffFunction pf = (SumPayoffFunction) config.payoffFunction;
            String label = String.format("%.1f", pf.smin);
            applet.text(label, Math.round(0), Math.round(applet.height * scale + 20));
            label = String.format("%.1f", pf.smax);
            applet.text(label, Math.round(applet.width * scale), Math.round(applet.height * scale + 20));
        }
    }

    private void setTarget(float newTarget) {
        if (config.trajectory) {
            float current = Client.state.getMyStrategy()[0];
            if (newTarget == current) {
                Client.state.target[0] = newTarget;
            } else if (newTarget > current) {
                Client.state.target[0] = 1f;
            } else if (newTarget < current) {
                Client.state.target[0] = 0f;
            }
        } else {
            Client.state.target[0] = newTarget;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (enabled) {
            boolean button = false;
            if (config.payoffFunction.getNumStrategies() == 2) {
                float x = e.getX() * scale;
                float y = e.getY() * scale;
                if (x >= width - 100 && x <= width && y >= -30 && y <= -5) {
                    Client.state.target[1] = 0;
                    button = true;
                }
                if (x >= width - 205 && x <= width - 105 && y >= -30 && y <= -5) {
                    Client.state.target[1] = 1;
                    button = true;
                }
            }
            if (!button) {
                slider.grabGhost();
            }
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
        if (enabled && e.isActionKey()) {
            float newTarget = slider.getGhostValue();
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (config.trajectory) {
                    newTarget = 1f;
                } else {
                    float grid = config.grid;
                    if (Float.isNaN(grid)) {
                        grid = 0.01f;
                    }
                    newTarget += grid;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (config.trajectory) {
                    newTarget = 0f;
                } else {
                    float grid = config.grid;
                    if (Float.isNaN(grid)) {
                        grid = 0.01f;
                    }
                    newTarget -= grid;
                }
            }
            newTarget = Client.constrain(newTarget, 0, 1);
            setTarget(newTarget);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (enabled && e.isActionKey() && (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT)) {
            setTarget(Client.state.getMyStrategy()[0]);
        }
    }
}