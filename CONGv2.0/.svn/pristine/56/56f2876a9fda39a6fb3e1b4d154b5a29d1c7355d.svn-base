/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.logging;

import edu.ucsc.leeps.fire.cong.config.Config;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public interface LogEvent extends Serializable {
    
    public void log(
            String period, int subject,
            int subperiod, int millisLeft,
            int group, int matchGroup,
            Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies,
            Map<Integer, float[]> realizedStrategies, Map<Integer, float[]> realizedMatchStrategies,
            Map<Integer, float[]> targets,
            Config config);

    public String getDelimiter();
}
