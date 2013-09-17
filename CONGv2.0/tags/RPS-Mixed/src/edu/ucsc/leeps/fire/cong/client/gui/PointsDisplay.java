package edu.ucsc.leeps.fire.cong.client.gui;

import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;

/**
 *
 * @author jpettit
 */
public class PointsDisplay extends Sprite {

    private boolean displaySwitchCosts;
    private float totalPoints, periodPoints, periodCost;
    private float[] myStrategy, counterStrategy;
    private long lastChangeTime, periodStartTime;

    public PointsDisplay(Sprite parent, int x, int y, Client client) {
        super(parent, x, y, (int) client.textWidth("Current Earnings: 000"), (int) (2 * (client.textAscent() + client.textDescent())));
        totalPoints = 0;
        periodPoints = 0;
        periodCost = 0;
        displaySwitchCosts = false;
    }

    /**
     * Draws points display. Using black font, displays period payoff, period
     * cost, and net payoff to an accuracy of two decimal places. Period payoff
     * is determined by the period points, period cost is determined by period
     * cost, and net payoff is the difference between period points and period
     * costs.
     * Period payoff string is located at the origin. Period cost string is
     * located below payoff string, and is red if cost is greater than points and
     * black if less than period points. Payoff is displayed below period cost,
     * using black text. 
     * @param applet
     */
    @Override
    public void draw(Client applet) {
        String totalEarningsString = "";
        String periodEarningsString = "";
        String periodCostString = "";
        String netEarningsString = "";
        totalEarningsString = String.format("Previous Earnings: %.2f", totalPoints);
        periodEarningsString = String.format("Current Earnings: %.2f", periodPoints);
        try {
            if (FIRE.client.getConfig().changeCost != 0) {
                periodCostString = String.format("Gross Cost: %.2f", periodCost);
                netEarningsString = String.format("Net Earnings: %.2f", periodPoints - periodCost);
            }
        } catch (Exception e) {
        }
        float textHeight = applet.textAscent() + applet.textDescent();
        applet.fill(0);
        applet.textAlign(Client.LEFT);
        applet.text(totalEarningsString, Math.round(origin.x), Math.round(origin.y));
        applet.text(periodEarningsString, Math.round(origin.x), Math.round(origin.y + textHeight));
        applet.fill(200, 0, 0);
        applet.text(periodCostString, Math.round(origin.x), Math.round(origin.y + 2 * textHeight));
        if (periodCost <= periodPoints) {
            applet.fill(0);
        }
        applet.text(netEarningsString, Math.round(origin.x), Math.round(origin.y + 3 * textHeight));
        applet.fill(0);
        if (displaySwitchCosts) {
            /*
            String costString = String.format("Switch Costs: -%.2f", switchCosts);
            String totalString = String.format("Total: %.2f", points - switchCosts);
            applet.text(costString, origin.x, origin.y + textHeight);
            applet.text(totalString, origin.x, origin.y + 2 * textHeight);
             * 
             */
        }
    }

    /**
     * Using FIRE's client, updater period points and period costs.
     */
    public void update() {
        totalPoints = FIRE.client.getTotalPoints();
        periodPoints = FIRE.client.getPeriodPoints();
        periodCost = FIRE.client.getClient().getCost();
        if (lastChangeTime > 0) {
            long elapsed = System.currentTimeMillis() - lastChangeTime;
            if (elapsed > 1000) {
                float millisInPeriod = FIRE.client.getConfig().length * 1000f;
                float percentInStrategy = elapsed / millisInPeriod;
                float percentOfPeriod = (System.currentTimeMillis() - periodStartTime) / millisInPeriod;
                float payoff = FIRE.client.getConfig().payoffFunction.getPayoff(percentOfPeriod, myStrategy, counterStrategy);
                periodPoints += percentInStrategy * payoff;
            }
        }
    }

    public void startPeriod() {
        periodStartTime = System.currentTimeMillis();
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
