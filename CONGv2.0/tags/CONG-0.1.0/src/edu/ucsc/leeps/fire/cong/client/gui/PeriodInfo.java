package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PayoffUtils;
import edu.ucsc.leeps.fire.cong.server.SumPayoffFunction;

/**
 *
 * @author jpettit
 */
public class PeriodInfo extends Sprite implements Configurable<Config> {

    private Config config;
    private int secondsLeft;
    private boolean displaySwitchCosts;
    private float totalPoints, periodPoints, periodCost, multiplier;
    private float[] myStrategy, counterStrategy;
    private long lastChangeTime, periodStartTime;
    private int lineNumber;

    public PeriodInfo(Sprite parent, int x, int y, Client embed) {
        super(parent, x, y, (int) embed.textWidth("Current Earnings: 000"), (int) (embed.textAscent() + embed.textDescent()));
        secondsLeft = 0;
        FIRE.client.addConfigListener(this);
        totalPoints = 0;
        periodPoints = 0;
        periodCost = 0;
        multiplier = 1;
        displaySwitchCosts = false;
    }

    public void configChanged(Config config) {
        this.config = config;
    }

    private void drawSubperiodTicker(Client applet) {
        applet.pushMatrix();
        applet.translate(origin.x, origin.y + ++lineNumber * (applet.textAscent() + applet.textDescent()));
        applet.rectMode(Client.CORNERS);
        applet.noStroke();
        applet.fill(0, 50, 255, 50);
        float x = 0;
        if (Client.state.currentPercent >= 0 && Client.state.currentPercent <= 1) {
            float percentPerSub = 1f / config.subperiods;
            float percentElapsed = Client.state.subperiod * percentPerSub;
            float remainder = Client.state.currentPercent - percentElapsed;
            x = remainder / percentPerSub;
        }
        applet.rect(0, 0, x * 150, -20);
        applet.stroke(0);
        applet.strokeWeight(2f);
        applet.noFill();
        applet.rect(0, 0, 150, -20);
        applet.popMatrix();
    }

    @Override
    public void draw(Client applet) {
        if (config == null) {
            return;
        }
        String s;
        if (config.indefiniteEnd == null) {
            if (config.subperiods != 0) {
                s = String.format("Subperiods Left: %d", config.subperiods - Client.state.subperiod);
            } else {
                s = String.format("Seconds Left: %d", secondsLeft);
            }
        } else {
            if (config.subperiods != 0) {
                if (Client.state.subperiod < config.subperiods) {
                    s = String.format("Subperiod: %d", Client.state.subperiod + 1);
                } else {
                    s = String.format("Subperiod: %d", Client.state.subperiod);
                }
            } else {
                s = String.format("Seconds Elapsed: %.0f", FIRE.client.getElapsedMillis() / 1000f);
            }
        }
        applet.fill(0);
        applet.textAlign(Client.LEFT);
        lineNumber = 0;
        float textHeight = applet.textAscent() + applet.textDescent();
        applet.text(s, (int) origin.x, (int) (origin.y + lineNumber++ * textHeight));
        String totalEarningsString = "";
        String periodEarningsString = "";
        String multiplierString = "";
        totalEarningsString = String.format("Previous Earnings: %.2f", totalPoints);
        periodEarningsString = String.format("Current Earnings: %.2f", periodPoints);
        applet.text(totalEarningsString, (int) origin.x, (int) (origin.y + lineNumber++ * textHeight));
        applet.text(periodEarningsString, (int) origin.x, (int) (origin.y + lineNumber++ * textHeight));
        if (FIRE.client.getConfig().showPGMultiplier) {
            multiplierString = String.format("Multipler: %.2f", multiplier);
            applet.fill(0);
            applet.text(multiplierString, (int) origin.x, (int) (origin.y + lineNumber++ * textHeight));
        }
        if (config.subperiods != 0 && FIRE.client.isRunningPeriod() && !FIRE.client.isPaused()) {
            drawSubperiodTicker(applet);
        }
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public void update() {
        totalPoints = FIRE.client.getTotalPoints();
        periodPoints = FIRE.client.getPeriodPoints();
        periodCost = FIRE.client.getClient().getCost();
        if (lastChangeTime > 0) {
            long elapsed = System.currentTimeMillis() - lastChangeTime;
            if (elapsed > 1000) {
                float millisInPeriod = FIRE.client.getConfig().length * 1000f;
                float percentInStrategy = elapsed / millisInPeriod;
                float payoff = PayoffUtils.getPayoff();
                periodPoints += percentInStrategy * payoff;
            }
        }
    }

    public void startPeriod() {
        periodStartTime = System.currentTimeMillis();
        if (FIRE.client.getConfig().payoffFunction instanceof SumPayoffFunction) { //payoff function dependent
            multiplier = ((SumPayoffFunction) FIRE.client.getConfig().payoffFunction).A;
            multiplier /= Client.state.strategies.size();
        }
        update();
    }

    public void endPeriod() {
        lastChangeTime = -1;
        update();
    }

    public void setMyStrategy(float[] s) {
        lastChangeTime = System.currentTimeMillis();
        myStrategy = s;
    }

    public void setCounterpartStrategy(float[] s) {
        lastChangeTime = System.currentTimeMillis();
        counterStrategy = s;
    }
}
