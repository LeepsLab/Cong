package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.client.StrategyChanger;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffFunction;
import edu.ucsc.leeps.fire.cong.server.ThreeStrategyPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.ThresholdPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;

/**
 *
 * @author jpettit
 */
public class Chart extends Sprite implements Configurable<Config> {

    // Variables to modify that manipulate the chart
    public float currentPercent;
    private Config config;
    private PayoffFunction payoffFunction, counterpartPayoffFunction;
    private float currentPayoffYou, currentPayoffCounterpart;
    private float minPayoff, maxPayoff;
    private int scaledMargin;
    private int scaledHeight;
    // Two strategy
    private float percent_A;
    private float percent_a;
    private float currentAPayoff;
    private float currentBPayoff;
    private float currentAaPayoff;
    private float currentAbPayoff;
    private float currentBaPayoff;
    private float currentBbPayoff;
    // Three strategy
    private ThreeStrategySelector simplex;
    private float percent_R;
    private float percent_r;
    private float percent_P;
    private float percent_p;
    private float percent_S;
    private float percent_s;
    private float currentRPayoff;
    private float currentPPayoff;
    private float currentSPayoff;
    // Either two or three strategies
    private Line actualPayoffYou;
    private Line actualPayoffCounterpart;
    // Two strategy
    private Line actualAPayoff;
    private Line actualBPayoff;
    private Line actualAaPayoff;
    private Line actualAbPayoff;
    private Line actualBaPayoff;
    private Line actualBbPayoff;
    private Line futureAaPayoff;
    private Line futureAbPayoff;
    private Line futureBaPayoff;
    private Line futureBbPayoff;
    private Line futureAPayoff;
    private Line futureBPayoff;
    private Line yourStrategyOverTime;
    private Line counterpartStrategyOverTime;
    // Three strategy
    private Line actualRPayoff;
    private Line actualPPayoff;
    private Line actualSPayoff;
    private Line futureRPayoff;
    private Line futurePPayoff;
    private Line futureSPayoff;
    private Line futureRrPayoff;
    private Line futureRpPayoff;
    private Line futureRsPayoff;
    private Line futurePrPayoff;
    private Line futurePpPayoff;
    private Line futurePsPayoff;
    private Line futureSrPayoff;
    private Line futureSpPayoff;
    private Line futureSsPayoff;
    private Line yourROverTime;
    private Line counterpartROverTime;
    private Line yourPOverTime;
    private Line counterpartPOverTime;
    private Line yourSOverTime;
    private Line counterpartSOverTime;
    // threshold
    private Line threshold;
    private StrategyChanger strategyChanger;
    private HeatmapLegend heatmapLegend;

    /**
     *A list of modes. Payoff, two-strategy and three-strategy. 
     */
    public enum Mode {

        Payoff, TwoStrategy, RStrategy, PStrategy, SStrategy
    };
    private Mode mode;

    /**
     * Creates the chart. Scales the height and margin. Shows line depicting
     * actual payoff for subject and subject's counterpart.
     *
     * For 2 strategy payoff, shows actual and future payoffs for your and
     * counterpart's strategy, Using different combinations: A, B, Aa ,Ab, Ba, Bb.
     *
     * For a 3 strategy, shows actual RPS payoffs. Shows future payoffs for
     * various combinations of R, P, and S. Shows your R, P and S over time, and
     * shows counterpart's R, P and S over time.
     *
     * Shows threshold and draws a simplex. Adds a configListener.
     * 
     * @param parent
     * @param x x-coordinate
     * @param y y-coordinate
     * @param width width of display
     * @param height height of display
     * @param simplex simplex is selected for the strategy changer-- 3 strategies used
     * @param mode mode used
     * @param strategyChanger
     */
    public Chart(Sprite parent, int x, int y, int width, int height, ThreeStrategySelector simplex, Mode mode, StrategyChanger strategyChanger) {
        super(parent, x, y, width, height);

        this.strategyChanger = strategyChanger;

        scaledHeight = Math.round(0.9f * height);
        scaledMargin = Math.round((height - scaledHeight) / 2f);

        actualPayoffYou = new Line(this, 0, scaledMargin, width, scaledHeight);
        actualPayoffCounterpart = new Line(this, 0, scaledMargin, width, scaledHeight);
        // Two strategy
        actualAPayoff = new Line(this, 0, 0, width, height);
        actualBPayoff = new Line(this, 0, 0, width, height);
        futureAPayoff = new Line(this, 0, 0, width, height);
        futureBPayoff = new Line(this, 0, 0, width, height);
        actualAaPayoff = new Line(this, 0, 0, width, height);
        actualAbPayoff = new Line(this, 0, 0, width, height);
        actualBaPayoff = new Line(this, 0, 0, width, height);
        actualBbPayoff = new Line(this, 0, 0, width, height);
        futureAaPayoff = new Line(this, 0, 0, width, height);
        futureAbPayoff = new Line(this, 0, 0, width, height);
        futureBaPayoff = new Line(this, 0, 0, width, height);
        futureBbPayoff = new Line(this, 0, 0, width, height);
        yourStrategyOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        counterpartStrategyOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        // RPSD
        actualRPayoff = new Line(this, 0, 0, width, height);
        actualPPayoff = new Line(this, 0, 0, width, height);
        actualSPayoff = new Line(this, 0, 0, width, height);

        futureRPayoff = new Line(this, 0, 0, width, height);
        futurePPayoff = new Line(this, 0, 0, width, height);
        futureSPayoff = new Line(this, 0, 0, width, height);
        futureRrPayoff = new Line(this, 0, 0, width, height);
        futureRpPayoff = new Line(this, 0, 0, width, height);
        futureRsPayoff = new Line(this, 0, 0, width, height);
        futurePrPayoff = new Line(this, 0, 0, width, height);
        futurePpPayoff = new Line(this, 0, 0, width, height);
        futurePsPayoff = new Line(this, 0, 0, width, height);
        futureSrPayoff = new Line(this, 0, 0, width, height);
        futureSpPayoff = new Line(this, 0, 0, width, height);
        futureSsPayoff = new Line(this, 0, 0, width, height);

        yourROverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        counterpartROverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        yourPOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        counterpartPOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        yourSOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);
        counterpartSOverTime = new Line(this, 0, scaledMargin, width, scaledHeight);

        heatmapLegend = new HeatmapLegend(this, -9, 1, 8, height);

        // Threshold
        threshold = new Line(this, 0, 0, width, height);

        this.simplex = simplex;

        this.mode = mode;

        FIRE.client.addConfigListener(this);
    }

    private void drawShockZone(Client applet) {
        if (config.shock.showZone) {
            applet.fill(100, 100, 100, 50);
            applet.noStroke();
            float x0, y0, x1, y1;
            x0 = width * FIRE.client.getConfig().shock.start;
            y0 = 0;
            x1 = width * FIRE.client.getConfig().shock.end;
            y1 = scaledHeight + scaledMargin * 2;
            applet.rect(x0, y0, x1 - x0, y1);
        }
    }

    private void drawAxis(Client applet) {
        applet.rectMode(Client.CORNER);
        applet.noFill();
        applet.stroke(0);
        applet.strokeWeight(2);
        applet.rect(0, 0, width, height);

        applet.textAlign(Client.CENTER, Client.CENTER);
        applet.fill(255);
        applet.noStroke();
        applet.rect(-40, 0, 38, height);
        applet.rect(0, height + 2, width, 40);
        if (mode == Mode.Payoff) {
            for (float x = 0.0f; x <= 1.01f; x += 0.1f) {
                applet.noFill();
                applet.stroke(100, 100, 100);
                applet.strokeWeight(2);
                float x0, y0, x1, y1;
                x0 = x * width;
                y0 = height;
                x1 = x * width;
                y1 = height + 10;
                applet.line(x0, y0, x1, y1);
                applet.fill(0);
                if (config.showPayoffTimeAxisLabels) {
                    int percent = Math.round(x * 100);
                    String label = String.format("%d%%", percent);
                    applet.text(label,
                            Math.round(x0),
                            Math.round(y0 + 1.2f * applet.textAscent() + applet.textDescent()));
                }
            }
            String maxPayoffLabel = String.format("%.1f", maxPayoff);
            float labelX = -10 - 1.1f * applet.textWidth(maxPayoffLabel) / 2f;
            heatmapLegend.origin.x = -10 - 1.1f * applet.textWidth(maxPayoffLabel)
                    - heatmapLegend.width;
            heatmapLegend.draw(applet);
            for (float y = 0.0f; y <= 1.01f; y += 0.1f) {
                applet.noFill();
                applet.stroke(100, 100, 100);
                applet.strokeWeight(2);
                float x0, y0, x1, y1;
                x0 = -10;
                y0 = y * scaledHeight + scaledMargin;
                x1 = 0;
                y1 = y * scaledHeight + scaledMargin;
                applet.line(x0, y0, x1, y1);
                applet.stroke(100, 100, 100, 50);
                applet.line(x0, y0, width, y1);
                applet.fill(0);
                float payoff = (1 - y) * (maxPayoff - minPayoff) + minPayoff;
                if (payoff < 0) {
                    payoff = 0f;
                }
                String label = String.format("%.1f", payoff);
                applet.text(label, Math.round(labelX), Math.round(y0));
            }
        } else {
            applet.textAlign(Client.RIGHT);
            applet.fill(0);
            if (mode == Mode.RStrategy) {
                applet.text(config.rLabel, -10,
                        Math.round(height / 2f + (applet.textAscent() + applet.textDescent()) / 2f));
            } else if (mode == Mode.PStrategy) {
                applet.text(config.pLabel, -10,
                        Math.round(height / 2f + (applet.textAscent() + applet.textDescent()) / 2f));
            } else if (mode == Mode.SStrategy) {
                applet.text(config.sLabel, -10,
                        Math.round(height / 2f + (applet.textAscent() + applet.textDescent()) / 2f));
            }
        }
    }

    private void drawPercentLine(Client applet) {
        applet.strokeWeight(2f);
        applet.stroke(150, 150, 150);
        applet.line(currentPercent * width, 0, currentPercent * width, height);
    }

    private void drawTwoStrategyPayoffLines(Client applet) {
        actualAPayoff.draw(applet);
        actualBPayoff.draw(applet);
        futureAPayoff.draw(applet);
        futureBPayoff.draw(applet);
        actualAaPayoff.draw(applet);
        actualAbPayoff.draw(applet);
        actualBaPayoff.draw(applet);
        actualBbPayoff.draw(applet);
        futureAaPayoff.draw(applet);
        futureBbPayoff.draw(applet);
    }

    private void drawTwoStrategyLines(Client applet) {
        counterpartStrategyOverTime.draw(applet);
        yourStrategyOverTime.draw(applet);
    }

    private void drawThreeStrategyPayoffLines(Client applet) {
        actualRPayoff.draw(applet);
        actualPPayoff.draw(applet);
        actualSPayoff.draw(applet);
        futureRPayoff.draw(applet);
        futurePPayoff.draw(applet);
        futureSPayoff.draw(applet);
        futureRrPayoff.draw(applet);
        futureRpPayoff.draw(applet);
        futureRsPayoff.draw(applet);
        futurePrPayoff.draw(applet);
        futurePpPayoff.draw(applet);
        futurePsPayoff.draw(applet);
        futureSrPayoff.draw(applet);
        futureSpPayoff.draw(applet);
        futureSsPayoff.draw(applet);
    }

    private void drawThreeStrategyLines(Client applet) {
        if (mode == Mode.RStrategy) {
            yourROverTime.draw(applet);
            counterpartROverTime.draw(applet);
        } else if (mode == Mode.PStrategy) {
            yourPOverTime.draw(applet);
            counterpartPOverTime.draw(applet);
        } else if (mode == Mode.SStrategy) {
            yourSOverTime.draw(applet);
            counterpartSOverTime.draw(applet);
        }
    }

    private void drawSubperiodMarkers(Client applet) {
        if (config == null || config.subperiods == 0) {
            return;
        }
        applet.strokeWeight(1f);
        applet.stroke(100, 100, 100);
        float interval = 1f / (float) config.subperiods;
        float offset = interval;
        for (int i = 0; i < config.subperiods; ++i) {
            applet.line(width * offset, 0, width * offset, height);
            offset += interval;
        }
    }

    /**
     * Draw chart. Embed the applet in the corner. Translate the origin of the
     * applet to x and y.
     *
     * If the config is not null, draw shockZone applet. When using the two
     * strategy payoff function, if the mode is set to payoff, Draw two strategy
     * payoff lines. Otherwise, if the mode is TwoStrategy, use  drawTwoStrategyLines.
     * Use threshold. If the config of the payoff function is an instance of
     * ThresholdPayoffFunction, have no borders and fill with a transparent
     * yellow. Embed applet in corner, 
     *
     * @param applet a sub-program to be run in Cong.
     */
    @Override
    public void draw(Client applet) {
        if (config == null) {
            return;
        }
        applet.rectMode(Client.CORNER);
        applet.pushMatrix();
        applet.translate(origin.x, origin.y);
        if (config != null) {
            drawShockZone(applet);
            if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
                if (mode == Mode.Payoff) {
                    drawTwoStrategyPayoffLines(applet);
                    actualPayoffYou.draw(applet);
                    actualPayoffCounterpart.draw(applet);
                } else if (mode == Mode.TwoStrategy) {
                    drawTwoStrategyLines(applet);
                    threshold.draw(applet);
                    if (config.payoffFunction instanceof ThresholdPayoffFunction) {
                        applet.noStroke();
                        applet.fill(255, 255, 0, 75);
                        applet.rectMode(Client.CORNER);
                        applet.rect(0, 0, width,
                                height * (1 - ((ThresholdPayoffFunction) config.payoffFunction).threshold));
                    }
                }
            } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
                if (mode == Mode.Payoff) {
                    drawThreeStrategyPayoffLines(applet);
                    actualPayoffYou.draw(applet);
                    actualPayoffCounterpart.draw(applet);
                } else if (mode == Mode.RStrategy
                        || mode == Mode.PStrategy
                        || mode == Mode.SStrategy) {
                    drawThreeStrategyLines(applet);
                }
            }
        }
        if (mode == Mode.Payoff) {
            actualPayoffYou.drawCostArea(applet, strategyChanger.getCost());
        }
        drawPercentLine(applet);
        drawSubperiodMarkers(applet);
        drawAxis(applet);
        applet.popMatrix();
    }

    /**
     * Clear actual payoff for you, your counterpart,and strategies over time for
     * combinations of strategies for two or three strategy payoff functions.
     */
    public void clearAll() {
        actualPayoffYou.clear();
        actualPayoffCounterpart.clear();
        actualAPayoff.clear();
        actualBPayoff.clear();
        actualAaPayoff.clear();
        actualBbPayoff.clear();
        yourStrategyOverTime.clear();
        counterpartStrategyOverTime.clear();
        yourPOverTime.clear();
        yourROverTime.clear();
        yourSOverTime.clear();
        counterpartROverTime.clear();
        counterpartPOverTime.clear();
        counterpartSOverTime.clear();

        clearFuture();
    }

    /**
     * Clear future payoffs.
     */
    public void clearFuture() {
        // clear two strategy
        futureAPayoff.clear();
        futureBPayoff.clear();
        futureAaPayoff.clear();
        futureAbPayoff.clear();
        futureBaPayoff.clear();
        futureBbPayoff.clear();

        // clear three strategy
        futureRPayoff.clear();
        futurePPayoff.clear();
        futureSPayoff.clear();
        futureRrPayoff.clear();
        futureRpPayoff.clear();
        futureRsPayoff.clear();
        futurePrPayoff.clear();
        futurePpPayoff.clear();
        futurePsPayoff.clear();
        futureSrPayoff.clear();
        futureSpPayoff.clear();
        futureSsPayoff.clear();
    }

    private void addTwoStrategyFuturePayoffPoints() {
        clearFuture();
        for (float futurePercent = currentPercent; futurePercent <= 1.0; futurePercent += 0.001f) {
            float future_A = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1},
                    new float[]{percent_a});
            float future_B = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0},
                    new float[]{percent_a});
            float future_Aa = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1},
                    new float[]{1});
            float future_Ab = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1},
                    new float[]{0});
            float future_Ba = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0},
                    new float[]{1});
            float future_Bb = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0},
                    new float[]{0});
            addPayoffPoint(futureAPayoff, futurePercent, future_A);
            addPayoffPoint(futureBPayoff, futurePercent, future_B);
            addPayoffPoint(futureAaPayoff, futurePercent, future_Aa);
            addPayoffPoint(futureAbPayoff, futurePercent, future_Ab);
            addPayoffPoint(futureBaPayoff, futurePercent, future_Ba);
            addPayoffPoint(futureBbPayoff, futurePercent, future_Bb);
        }
    }

    private void addThreeStrategyFuturePayoffPoints() {
        clearFuture();
        for (float futurePercent = currentPercent; futurePercent <= 1.0; futurePercent += 0.01f) {
            float futureR = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1, 0, 0},
                    simplex.getOpponentRPS());
            float futureP = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 1, 0},
                    simplex.getOpponentRPS());
            float futureS = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 0, 1},
                    simplex.getOpponentRPS());
            float futureRr = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1, 0, 0},
                    new float[]{1, 0, 0});
            float futureRp = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1, 0, 0},
                    new float[]{0, 1, 0});
            float futureRs = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{1, 0, 0},
                    new float[]{0, 0, 1});
            float futurePr = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 1, 0},
                    new float[]{1, 0, 0});
            float futurePp = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 1, 0},
                    new float[]{0, 1, 0});
            float futurePs = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 1, 0},
                    new float[]{0, 0, 1});
            float futureSr = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 0, 1},
                    new float[]{1, 0, 0});
            float futureSp = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 0, 1},
                    new float[]{0, 1, 0});
            float futureSs = payoffFunction.getPayoff(
                    futurePercent,
                    new float[]{0, 0, 1},
                    new float[]{0, 0, 1});

            addPayoffPoint(futureRPayoff, futurePercent, futureR);
            addPayoffPoint(futurePPayoff, futurePercent, futureP);
            addPayoffPoint(futureSPayoff, futurePercent, futureS);
            addPayoffPoint(futureRrPayoff, futurePercent, futureRr);
            addPayoffPoint(futureRpPayoff, futurePercent, futureRp);
            addPayoffPoint(futureRsPayoff, futurePercent, futureRs);
            addPayoffPoint(futurePrPayoff, futurePercent, futurePr);
            addPayoffPoint(futurePpPayoff, futurePercent, futurePp);
            addPayoffPoint(futurePsPayoff, futurePercent, futurePs);
            addPayoffPoint(futureSrPayoff, futurePercent, futureSr);
            addPayoffPoint(futureSpPayoff, futurePercent, futureSp);
            addPayoffPoint(futureSsPayoff, futurePercent, futureSs);
        }
    }

    /**
     * If period is not completed, add payoff points for both your and
     * counterpart's actual payoff, current percent, and current payoff.
     * If using a 2 strategy payoff event, add two strategy actual and future
     * payoff points. If a three strategy payoff function  is being used, add
     * strategy payoff points, and actual and future payoff points.
     *
     * Strategy points are added using strategy over time, current percent, and
     * percent based on strategy.
     */
    public void updateLines() {
        if (currentPercent <= 1f) {
            addPayoffPoint(actualPayoffYou, currentPercent, currentPayoffYou);
            addPayoffPoint(actualPayoffCounterpart, currentPercent, currentPayoffCounterpart);
            if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
                //addTwoStrategyActualPayoffPoints();
                //addTwoStrategyFuturePayoffPoints();
            } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
                //addThreeStrategyActualPayoffPoints();
                //addThreeStrategyFuturePayoffPoints();
                addThreeStrategyPoints();
            }
            addStrategyPoint(yourStrategyOverTime, currentPercent, percent_A);
            addStrategyPoint(counterpartStrategyOverTime, currentPercent, percent_a);
        }
    }

    private void addTwoStrategyActualPayoffPoints() {
        addPayoffPoint(actualAPayoff, currentPercent, currentAPayoff);
        addPayoffPoint(actualBPayoff, currentPercent, currentBPayoff);

        addPayoffPoint(actualAaPayoff, currentPercent, currentAaPayoff);
        addPayoffPoint(actualAbPayoff, currentPercent, currentAbPayoff);
        addPayoffPoint(actualBaPayoff, currentPercent, currentBaPayoff);
        addPayoffPoint(actualBbPayoff, currentPercent, currentBbPayoff);
    }

    private void addThreeStrategyActualPayoffPoints() {
        addPayoffPoint(actualRPayoff, currentPercent, currentRPayoff);
        addPayoffPoint(actualPPayoff, currentPercent, currentPPayoff);
        addPayoffPoint(actualSPayoff, currentPercent, currentSPayoff);
    }

    private void addThreeStrategyPoints() {
        addStrategyPoint(yourROverTime, currentPercent, percent_R);
        addStrategyPoint(counterpartROverTime, currentPercent, percent_r);
        addStrategyPoint(yourPOverTime, currentPercent, percent_P);
        addStrategyPoint(counterpartPOverTime, currentPercent, percent_p);
        addStrategyPoint(yourSOverTime, currentPercent, percent_S);
        addStrategyPoint(counterpartSOverTime, currentPercent, percent_s);
    }

    private void twoStrategyChanged() {
        currentPayoffYou = payoffFunction.getPayoff(
                currentPercent,
                new float[]{percent_A},
                new float[]{percent_a});
        currentPayoffCounterpart = counterpartPayoffFunction.getPayoff(
                currentPercent,
                new float[]{percent_a},
                new float[]{percent_A});
        // FIXME - use counterpart info to fix these
        currentAPayoff = payoffFunction.getPayoff(
                currentPercent,
                new float[]{1},
                new float[]{percent_a});
        currentBPayoff = payoffFunction.getPayoff(
                currentPercent,
                new float[]{0},
                new float[]{percent_a});
        currentAaPayoff = payoffFunction.getPayoff(currentPercent,
                new float[]{1},
                new float[]{1});
        currentAbPayoff = payoffFunction.getPayoff(currentPercent,
                new float[]{1},
                new float[]{0});
        currentBaPayoff = payoffFunction.getPayoff(currentPercent,
                new float[]{0},
                new float[]{1});
        currentBbPayoff = payoffFunction.getPayoff(currentPercent,
                new float[]{0},
                new float[]{0});
    }

    private void threeStrategyChanged() {
        currentPayoffYou = payoffFunction.getPayoff(
                currentPercent,
                simplex.getPlayerRPS(),
                simplex.getOpponentRPS());
        currentPayoffCounterpart = counterpartPayoffFunction.getPayoff(
                currentPercent,
                simplex.getOpponentRPS(),
                simplex.getPlayerRPS());
    }

    private void strategyChanged() {
        if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            twoStrategyChanged();
        } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
            threeStrategyChanged();
        } else {
            assert false;
        }
    }

    /**
     * Sets strategy. If a two strategy payoff function is being used, subject's
     * strategy is set as initial conditions. If a three strategy payoff function
     * is being used, Percent R is first element in array, percent P is second
     * element and percent S is third element.
     *
     * Implements the strategyChanged function.
     *
     * @param s an array with the strategy.
     */
    public void setMyStrategy(float[] s) {
        if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            percent_A = s[0];
        } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
            percent_R = s[0];
            percent_P = s[1];
            percent_S = s[2];
        }
        strategyChanged();
    }

    /**
     * Sets the counterpart's strategy. If a two strategy payoff function is
     * being used, counterpart's initial percent is set as initial element in
     * array. If three strategy payoff function is being used, Percent r is first
     * element in array, percent p is second element, and percent s is third
     * element.
     *
     * Implements strategyChange function.
     *
     * @param s an array with the counterpart's strategy.
     */
    public void setCounterpartStrategy(float[] s) {
        if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            percent_a = s[0];
        } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
            percent_r = s[0];
            percent_p = s[1];
            percent_s = s[2];
        }
        strategyChanged();
    }

    /**
     * Sets strategy at end of period. In the event that a two strategy payoff
     * function is being used, sets subject's strategy as subperiodStrategy, and
     * sets counterpart's strategy as counterpartSubperiodStrategy. If a three
     * strategy payoff function is used, sets RPS as the first, second and third
     * elements of subperiodStrategy, respectively. Similarly, the rps elements
     * are set as the first, second and third elements of the
     * counterpartSubperiodStrategy.
     *
     * Calculates the percent start using the quotient if one less than the
     * subperiod and the total number of subperiods. Calculates the percent end
     * using the quotient of the subperiod and the total number of subperiods.
     * The current percent is called tmpCurrentPercent.
     *
     * Your current payoff is based on the payoffFunction using tmpCurrentPercent,
     * subperiodStrategy, and counterpartSubperiodStrategy. Counterpart's payoff
     * is determined by counterpartPayoffFunction, using tmpCurrentPercent,
     * counterpartSubperiodStrategy, and subperiodStrategy.
     *
     * Update lines using percent start and percent end. Sets current percent to
     * tmpCurrentPercent.
     *
     * @param subperiod number of subperiod.
     * @param subperiodStrategy strategy selected by subject for subperiod
     * @param counterpartSubperiodStrategy counterpart's strategy for subperiod.
     */
    public void endSubperiod(int subperiod, float[] subperiodStrategy, float[] counterpartSubperiodStrategy) {
        if (config.payoffFunction instanceof TwoStrategyPayoffFunction) {
            percent_A = subperiodStrategy[0];
            percent_a = counterpartSubperiodStrategy[0];
        } else if (config.payoffFunction instanceof ThreeStrategyPayoffFunction) {
            percent_R = subperiodStrategy[0];
            percent_P = subperiodStrategy[1];
            percent_S = subperiodStrategy[2];
            percent_r = counterpartSubperiodStrategy[0];
            percent_p = counterpartSubperiodStrategy[1];
            percent_s = counterpartSubperiodStrategy[2];
        }
        float percentStart = (float) (subperiod - 1) / FIRE.client.getConfig().subperiods;
        float percentEnd = (float) subperiod / FIRE.client.getConfig().subperiods;
        float tmpCurrentPercent = currentPercent;
        currentPayoffYou = payoffFunction.getPayoff(tmpCurrentPercent, subperiodStrategy, counterpartSubperiodStrategy);
        currentPayoffCounterpart = counterpartPayoffFunction.getPayoff(tmpCurrentPercent, counterpartSubperiodStrategy, subperiodStrategy);
        currentPercent = percentStart;
        updateLines();
        currentPercent = percentEnd;
        updateLines();
        currentPercent = tmpCurrentPercent;
    }

    public void configChanged(Config config) {
        this.config = config;
        payoffFunction = config.payoffFunction;
        counterpartPayoffFunction = config.counterpartPayoffFunction;
        minPayoff = config.payoffFunction.getMin();
        maxPayoff = config.payoffFunction.getMax();
        actualPayoffYou.configure(config.yourPayoff);
        actualPayoffCounterpart.configure(config.otherPayoff);
        yourStrategyOverTime.configure(config.yourStrategyOverTime);
        counterpartStrategyOverTime.configure(config.counterpartStrategyOverTime);
        yourROverTime.configure(config.yourPayoff);
        yourROverTime.mode = Line.Mode.Solid;
        yourROverTime.weight = 2f;
        counterpartROverTime.configure(config.otherPayoff);
        counterpartROverTime.mode = Line.Mode.Solid;
        yourPOverTime.configure(config.yourPayoff);
        yourPOverTime.mode = Line.Mode.Solid;
        yourPOverTime.weight = 2f;
        counterpartPOverTime.configure(config.otherPayoff);
        counterpartPOverTime.mode = Line.Mode.Solid;
        yourSOverTime.configure(config.yourPayoff);
        yourSOverTime.mode = Line.Mode.Solid;
        yourSOverTime.weight = 2f;
        counterpartSOverTime.configure(config.otherPayoff);
        counterpartSOverTime.mode = Line.Mode.Solid;
        threshold.configure(config.thresholdLine);
        if (config.payoffFunction instanceof ThresholdPayoffFunction) {
            threshold.clear();
            for (float percent = 0f; percent < 1.0f; percent += .01f) {
                threshold.setPoint(Math.round(threshold.width * percent),
                        Math.round(threshold.height * (1 - ((ThresholdPayoffFunction) config.payoffFunction).threshold)),
                        true);
            }
            threshold.visible = true;
        } else {
            threshold.visible = false;
        }
    }

    /**
     * If current percent is greater than beginning shock as defined in config,
     * and is less than the ending shock, and line is set to show shocks, set
     * shocked to true.
     *
     * If not shocked, set width of line to the product of width and x. Set the
     * height of line proportional to 1 minus the quotient of y and the max payoff.
     * Height is inversely proportional to the max payoff.
     *
     * If shocks are backfilled, and current percent is greater than end shock,
     * clear shocks.
     * 
     * @param line
     * @param x
     * @param y
     */
    public void addPayoffPoint(Line line, float x, float y) {
        boolean shocked =
                config != null
                && config.shock != null
                && currentPercent > config.shock.start
                && currentPercent < config.shock.end
                && line.showShock;
        line.setPoint(
                Math.round(line.width * x),
                Math.round(line.height * (1 - ((y - minPayoff) / (maxPayoff - minPayoff)))),
                !shocked);
        if (FIRE.client.getConfig().shock.backfill && currentPercent > config.shock.end) {
            line.clearShocks();
        }
    }

    /**
     * If current percent is greater than starting shock and less than ending
     * shock and line shows shock, set shocked to true.
     *
     * If not shocked, multiply line width by x and height by 1 minus y.
     *
     * If shock is backfilled, and current percent is greater than ending shock,
     * clear shocks.
     * 
     * @param line
     * @param x
     * @param y
     */
    public void addStrategyPoint(Line line, float x, float y) {
        boolean shocked = currentPercent > config.shock.start && currentPercent < config.shock.end && line.showShock;
        line.setPoint(
                Math.round(line.width * x),
                Math.round(line.height * (1 - y)),
                !shocked);
        if (FIRE.client.getConfig().shock.backfill && currentPercent > config.shock.end) {
            line.clearShocks();
        }
    }
}
