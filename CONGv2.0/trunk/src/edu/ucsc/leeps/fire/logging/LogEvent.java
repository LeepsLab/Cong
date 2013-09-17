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
 * The interface describes a class for a log event which is used to store data
 * to be written into a log file.  The fields of a log event are converted into 
 * the fields of the log file by use of the log event's class class, thus to 
 * create fields to be written in a log event add simple add fields to a log event.
 * Log events are csv file with collumns delimited by the character provided by
 * the log event.
 * @author jpettit
 */
public interface LogEvent extends Serializable {
    
    /**
     * Sets the passed information in the log event.
     * @param period 
     * @param subject
     * @param subperiod
     * @param millisLeft
     * @param group
     * @param matchGroup
     * @param strategies
     * @param matchStrategies
     * @param realizedStrategies
     * @param realizedMatchStrategies
     * @param targets
     * @param config 
     */
    public void log(
            String period, int subject,
            int subperiod, int millisLeft,
            int group, int matchGroup,
            Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies,
            Map<Integer, float[]> realizedStrategies, Map<Integer, float[]> realizedMatchStrategies,
            Map<Integer, float[]> targets,
            Config config);
    /**
     * Return the string used to delimit the columns in the log file
     * @return a string to be used as a delimiter
     */
    public String getDelimiter();
}
