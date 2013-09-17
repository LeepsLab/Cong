/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire;

import edu.ucsc.leeps.fire.logging.LogEvent;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

/**
 *
 * @author jpettit
 */
public interface FIREServer<ClientInterfaceType, ConfigType> {

    public float getTotalPoints(int id);

    public float getPeriodPoints(int id);

    public void addToPeriodPoints(int id, float points);

    public void setPeriodPoints(int id, float points);

    public String getName(int id);

    public ConfigType getConfig();

    public ConfigType getConfig(int id);
    
    public List<ConfigType> getDefinedConfigs();
    
    public String getConfigSource();

    public List<String> getPeriods();

    public Map<String, ConfigType> getDefaultConfigs();

    public void commit(LogEvent event);

    public Timer getTimer();

    public Random getRandom();
    
    public void endPeriod();
}
