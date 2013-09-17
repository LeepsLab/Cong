package edu.ucsc.leeps.fire.cong.server;

/**
 *
 * @author jpettit
 */
public class UltimatumPayoffFunction extends TwoStrategyPayoffFunction {

    public float MAX;
    public boolean sender;

    @Override
    public float getMax() {
        return MAX;
    }

    @Override
    public float getMin() {
        return 0;
    }

    @Override
    public float getPayoff(
            float percent, float[] myStrategy, float[] opponentStrategy) {
        float offer, min;
        if (sender) {
            offer = myStrategy[0];
            min = opponentStrategy[0];
        } else {
            offer = opponentStrategy[0];
            min = myStrategy[0];
        }
        if (offer >= min) {
            if (sender) {
                return MAX * (1 - offer);
            } else {
                return MAX * offer;
            }
        } else {
            return 0;
        }
    }
}
