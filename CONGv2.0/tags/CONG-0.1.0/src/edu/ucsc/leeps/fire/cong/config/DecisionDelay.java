package edu.ucsc.leeps.fire.cong.config;

import edu.ucsc.leeps.fire.cong.FIRE;
import java.io.Serializable;

/**
 *
 * @author jpettit
 */
public class DecisionDelay implements Serializable {

    public static enum Distribution {

        uniform, poisson, gaussian;
    };
    public Distribution distribution;
    public float lambda;

    public int getDelay() {
        float delay = 0;
        switch (distribution) {
            case uniform:
                delay = FIRE.client.getRandom().nextFloat() * lambda;
                break;
            case poisson:
                delay = generatePoisson(lambda);
                break;
            case gaussian:
                throw new UnsupportedOperationException();
        }
        return Math.round(delay);
    }

    private int generatePoisson(float lambda) {
        float L = (float) Math.pow(Math.E, -1 * lambda);
        int k = 0;
        float p = 1;

        do {
            k = k + 1;
            p = p * FIRE.client.getRandom().nextFloat();
        } while (p > L);

        return k - 1;
    }
}
