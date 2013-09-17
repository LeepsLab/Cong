package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import java.io.Serializable;
import java.util.ArrayList;

public class Line extends Sprite implements Serializable {

    public enum Mode {

        Solid, EndPoint, Dashed, Shaded,
    };
    public float weight;
    public int r, g, b, alpha;
    public Mode mode;
    public boolean stepFunction;
    private transient ArrayList<Integer> ypoints;
    private transient int xMax;
    private transient Config config;
    private transient int maxLength;

    public Line() {
        super(null, 0, 0, 0, 0);
        r = g = b = 0;
        alpha = 255;
        weight = 1.0f;
        mode = Mode.Solid;
    }

    public Line(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        visible = false;
        ypoints = new ArrayList<Integer>();
        xMax = 0;
    }

    public void configure(Config config, Line lconfig) {
        this.visible = true;
        this.r = lconfig.r;
        this.g = lconfig.g;
        this.b = lconfig.b;
        this.alpha = lconfig.alpha;
        this.weight = lconfig.weight;
        this.mode = lconfig.mode;
        stepFunction = FIRE.client.getConfig().subperiods != 0;
        this.config = config;
    }

    public void setPoint(int x, int y) {
        int maxWidth;
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            maxWidth = (int) (FIRE.client.getConfig().indefiniteEnd.percentToDisplay * width);
        } else {
            maxWidth = width;
        }

        while (xMax < x) {
            ypoints.add(y);
            if (ypoints.size() > maxWidth) {
                ypoints.remove(0);
            }
            xMax++;
        }
    }

    private void drawSolidLine(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        for (int x = 1; x < ypoints.size(); x++) {
            if (stepFunction) {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x - 1));
                applet.line(x, ypoints.get(x - 1), x, ypoints.get(x));
                applet.line(x - 1, ypoints.get(x), x, ypoints.get(x));
            } else {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x));
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x - 1));
            }
        }
    }

    private void drawDashedLine(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        for (int x = 1; x < ypoints.size(); x++) {
            if (x % 4 != 0) {
                applet.line(x - 1, ypoints.get(x - 1), x, ypoints.get(x));
            }
        }
    }

    private void drawLineEndPoint(Client applet) {
        throw new UnsupportedOperationException();
    }

    private void drawShadedArea(Client applet) {
        applet.stroke(r, g, b, alpha);
        applet.strokeWeight(weight);
        applet.fill(r, g, b, alpha);
        if (stepFunction) {
            applet.beginShape();
            for (int x = 0; x < ypoints.size(); x++) {
                if (x > 0 && Math.abs(ypoints.get(x) - ypoints.get(x - 1)) > 1) {
                    applet.vertex(x, ypoints.get(x - 1));
                }
                applet.vertex(x, ypoints.get(x));
            }
            if (ypoints.size() > 0) {
                applet.vertex(ypoints.size(), ypoints.get(ypoints.size() - 1));
            }
            applet.vertex(ypoints.size(), height);
            applet.vertex(0, height);
            applet.endShape();
        } else {
            for (int x = 0; x < ypoints.size(); x++) {
                applet.line(x, ypoints.get(x), x, height);
            }
        }
    }

    public synchronized void drawCostArea(Client applet, float cost) {
        if (cost == 0) {
            return;
        }
        throw new UnsupportedOperationException();
    }

    public synchronized void draw(Client applet) {
        if (!visible) {
            return;
        }
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        switch (mode) {
            case Solid:
                drawSolidLine(applet);
                break;
            case Dashed:
                drawDashedLine(applet);
                break;
            case EndPoint:
                drawLineEndPoint(applet);
                break;
            case Shaded:
                drawShadedArea(applet);
                break;
        }
        applet.popMatrix();
    }

    public synchronized void clear() {
        xMax = 0;
        ypoints.clear();
    }

    public synchronized void addPayoffPoint(float x, float y) {
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            x = (x * FIRE.client.getConfig().length)
                    / (FIRE.client.getConfig().indefiniteEnd.displayLength / FIRE.client.getConfig().indefiniteEnd.percentToDisplay);
        }
        setPoint(
                Math.round(width * x),
                Math.round(height * (1 - ((y - FIRE.client.getConfig().payoffFunction.getMin()) / (FIRE.client.getConfig().payoffFunction.getMax() - FIRE.client.getConfig().payoffFunction.getMin())))));
    }

    public synchronized void addStrategyPoint(float x, float y) {
        if (FIRE.client.getConfig().indefiniteEnd != null) {
            x = (x * FIRE.client.getConfig().length)
                    / (FIRE.client.getConfig().indefiniteEnd.displayLength / FIRE.client.getConfig().indefiniteEnd.percentToDisplay);
        }
        setPoint(
                Math.round(width * x),
                Math.round(height * (1 - y)));
    }
}
