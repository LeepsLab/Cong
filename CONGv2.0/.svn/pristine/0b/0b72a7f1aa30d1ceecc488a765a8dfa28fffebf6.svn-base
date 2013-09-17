package edu.ucsc.leeps.fire.cong.config;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author jpettit
 */
public abstract class IndefiniteEnd implements Serializable {

    public int secondsToDisplay = 60;
    public float percentToDisplay = 0.5f;
    public int subperiodLength;

    public abstract int length(Random random);

    public static class Uniform extends IndefiniteEnd {

        public int max;

        public int length(Random random) {
            return random.nextInt(max);
        }
    }

    public static class Assigned extends IndefiniteEnd {

        public int subperiods;

        public int length(Random random) {
            return subperiods;
        }
    }
}
