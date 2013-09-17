package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import processing.core.PApplet;

public class Line extends Sprite implements Serializable {

    public enum Mode {

        Solid, EndPoint, Dashed, Shaded,
    };
    public float weight;
    public int r, g, b, alpha;
    public Mode mode;
    public int SAMPLE_RATE = 2;
    public boolean showShock = true;
    public boolean stepFunction;
    private transient HashMap<Integer, FPoint> definedPoints;
    private transient LinkedList<FPoint> points;
    private transient int costEnd;
    private transient Marker costMarker;

    /**
     * Creates a black line, 1 pixel in width.
     */
    public Line() {
        super(null, 0, 0, 0, 0);
        r = g = b = 0;
        alpha = 255;
        weight = 1.0f;
        mode = Mode.Solid;
    }

    /** 
     * Creates a HashMap of defined points in the line. Creates a linked list of
     * the points. Adds a  cost marker.
     *
     * @param parent parent class
     * @param x x-coordinate
     * @param y y-coordinate
     * @param width width
     * @param height height
     */
    public Line(Sprite parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        visible = false;
        definedPoints = new HashMap<Integer, FPoint>();
        points = new LinkedList<FPoint>();
        costMarker = new Marker(this, 0, 0, false, 0);
    }

    /**
     * Creates a configurator for the line. Has configurators for line color,
     * transparency, weight, mode and whether or not shocks are shown.
     * Creates a step function if there are subperiods. Sample at a rate of 1.
     * Cost markers are not visible.
     *
     * @param config configurator
     */
    public void configure(Line config) {
        this.visible = true;
        this.r = config.r;
        this.g = config.g;
        this.b = config.b;
        this.alpha = config.alpha;
        this.weight = config.weight;
        this.mode = config.mode;
        this.showShock = config.showShock;
        stepFunction = FIRE.client.getConfig().subperiods != 0;
        if (stepFunction) {
            SAMPLE_RATE = 1;
        }
        costMarker.setVisible(false);
    }

    /**
     * Sets points. If defined points do not contain Key x, then create a
     * visible FPoint with x and y coordinates. Put the point at the coordinate.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param visible point is visible if set to true
     */
    public synchronized void setPoint(int x, int y, boolean visible) {
        if (!definedPoints.containsKey(x)) {
            FPoint point = new FPoint(x, y);
            point.visible = visible;
            definedPoints.put(x, point);
            points.add(point);
        }
    }

    /**
     * If starting value is greater than or equal to zero and less than or equal
     * to ending value, and stop value is less than the size of points array, go
     * through points, and set points to visible for all values of  points from
     * start to stop values.
     *
     * @param start
     * @param stop
     * @param vis point is visible if true
     */
    public synchronized void setVisible(int start, int stop, boolean vis) {
        if (start <= stop && start >= 0 && stop < points.size() - 1) {
            for (int i = start; i < stop; i++) {
                points.get(i).visible = vis;
            }
        }
    }

    private void drawSolidLine(Client applet) {
        if (points.size() >= 2) {
            applet.stroke(r, g, b, alpha);
            applet.strokeWeight(weight);
            FPoint last = null;
            int i = 0;
            for (FPoint p : points) {
                if (costMarker.visible && p.x < costEnd) {
                    last = p;
                    i++;
                    continue;
                }
                if (!p.visible) {
                    last = p;
                    i++;
                    continue;
                }
                if (i % SAMPLE_RATE == 0 || i == points.size() - 1) {
                    if (last != null && last.visible) {
                        applet.stroke(r, g, b, alpha);
                        if (stepFunction && i >= 2) {
                            applet.line(last.x, p.y, p.x, p.y);
                            applet.line(last.x, last.y, last.x, p.y);
                        } else {
                            applet.line(last.x, last.y, p.x, p.y);
                        }
                    }
                    last = p;
                }
                i++;
            }
        }
    }

    private void drawDashedLine(Client applet) {
        if (points.size() >= 2) {
            applet.stroke(r, g, b, alpha);
            applet.strokeWeight(weight);
            for (int i = 0; i < points.size() - 1; i++) {
                if (i % 2 == 0) {
                    FPoint p0 = points.get(i);
                    FPoint p1 = points.get(i + 1);
                    if (!p0.visible || !p1.visible) {
                        continue;
                    }
                    if (p0.visible && p1.visible) {
                        applet.line(
                                p0.x, p0.y,
                                p1.x, p1.y);
                    }
                }
            }
        }
    }

    private void drawLineEndPoint(Client applet) {
        if (points.size() >= 1) {
            applet.fill(r, g, b, alpha);
            applet.stroke(r, g, b, alpha);
            applet.strokeWeight(weight);
            applet.ellipseMode(PApplet.CENTER);
            FPoint last = null;
            for (FPoint p : points) {
                if (p != null) {
                    last = p;
                }
            }
            if (last.visible) {
                applet.ellipse(last.x, last.y, 12, 12);
            }
        }
    }

    private void drawShadedArea(Client applet) {
        if (points.size() >= 1) {
            applet.fill(r, g, b, alpha);
            applet.stroke(r, g, b, alpha);
            applet.strokeWeight(weight);

            applet.beginShape();
            FPoint last = null;
            int i = 0;
            for (FPoint p : points) {
                if (costMarker.visible && p.x < costEnd) {
                    i++;
                    continue;
                }
                if (last != null && last.visible && !p.visible) {
                    // begin shock zone
                    applet.vertex(last.x, height);
                    applet.endShape(PApplet.CLOSE);
                } else if (last != null && !last.visible && p.visible) {
                    // end shock zone
                    applet.beginShape();
                    applet.vertex(p.x, height);
                }
                if (!p.visible) {
                    last = p;
                    i++;
                    continue;
                }
                if (i % SAMPLE_RATE == 0 || i == points.size() - 1) {
                    if (last == null) {
                        applet.vertex(p.x, height);
                        // for some reason, the polygon render fails if the
                        // next point has y value equal to the last point,
                        // we eliminate that vertex in this special case
                        if (Math.abs(p.y - height) > Float.MIN_NORMAL) {
                            applet.vertex(p.x, p.y);
                        }
                    } else {
                        if (stepFunction && i >= 2 && Math.abs(last.y - p.y) > Float.MIN_NORMAL) {
                            applet.vertex(last.x, p.y);
                        }
                        applet.vertex(p.x, p.y);
                    }
                    last = p;
                }
                i++;
            }
            // FIXME why is last null here!?
            if (last != null) {
                applet.vertex(last.x, height);
            }
            applet.endShape(PApplet.CLOSE);
        }
    }

    public synchronized void drawCostArea(Client applet, float cost) {
        if (cost == 0) {
            return;
        }
        float pixelCost = cost * width * height;
        float totalPixels = 0;
        costEnd = 0;
        for (FPoint p : points) {
            if (pixelCost > 0) {
                pixelCost -= p.y;
                costEnd++;
            }
            totalPixels += p.y;
        }
        if (costEnd <= 1) {
            return;
        }
        float costPercent = (cost * width * height) / totalPixels;
        costMarker.setVisible(true);
        costMarker.setLabelPercent(costPercent);
        costMarker.update(costEnd / 2f, 0.95f * height);
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        applet.stroke(0xFFB40406);
        applet.strokeWeight(2f);
        FPoint first = null;
        FPoint last = null;
        int i = 0;
        if (FIRE.client.getClient().getCost() >= FIRE.client.getPeriodPoints()) {
            applet.fill(255, 40, 40, 150);
            applet.beginShape();
            for (FPoint p : points) {
                if (i % SAMPLE_RATE == 0) {
                    if (i >= costEnd - 1) {
                        applet.vertex(last.x, height);
                        break;
                    }
                    if (last == null) {
                        first = p;
                        applet.vertex(p.x, height);
                        applet.vertex(p.x, p.y);
                    } else {
                        applet.vertex(p.x, p.y);
                    }
                    last = p;
                }
                i++;
            }
            applet.endShape();
        } else {
            for (FPoint p : points) {
                if (i % SAMPLE_RATE == 0) {
                    if (i >= costEnd - 1) {
                        applet.line(last.x, last.y, last.x, height);
                        applet.line(last.x, height, first.x, height);
                        break;
                    }
                    if (last == null) {
                        first = p;
                        applet.line(p.x, height, p.x, p.y);
                    } else {
                        applet.line(p.x, p.y, last.x, last.y);
                    }
                    last = p;
                }
                i++;
            }
        }
        costMarker.draw(applet);
        applet.popMatrix();
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

    /**
     * Clears points and defined points.
     */
    public synchronized void clear() {
        definedPoints.clear();
        points.clear();
    }

    /**
     * Removes points from first round, as it is the practice period.
     */
    public synchronized void removeFirst() {
        FPoint first = points.removeFirst();
        definedPoints.remove(Math.round(first.x));
    }

    /**
     * Clear shocks. For a point in points, set point to visible.
     */
    public synchronized void clearShocks() {
        for (FPoint point : points) {
            point.visible = true;
        }
    }
}
