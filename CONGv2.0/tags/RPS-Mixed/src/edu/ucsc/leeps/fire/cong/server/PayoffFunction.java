package edu.ucsc.leeps.fire.cong.server;

import java.io.Serializable;

/**
 *
 * @author dev
 */
public interface PayoffFunction extends Serializable {

    public float getMin();

    public float getMax();

    public float getPayoff(float percent, float[] myStrategy, float[] opponentStrategy);
}
