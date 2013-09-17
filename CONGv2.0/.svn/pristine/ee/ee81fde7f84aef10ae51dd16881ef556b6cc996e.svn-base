package edu.ucsc.leeps.fire.cong.config;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author jpettit
 */
public abstract class IndefiniteEnd implements Serializable {

    public int displayLength;
    public float percentToDisplay;
    public int subperiodLength;

    public abstract int length(Random random);

    public static class Uniform extends IndefiniteEnd {

        public int max;

        public int length(Random random) {
            return random.nextInt(max);
        }
    }

    public static class Assigned extends IndefiniteEnd {

        public int length;

        public int length(Random random) {
            return length;
        }
    }
}
