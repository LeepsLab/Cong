/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

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
    public float expectedLength = 1;

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
