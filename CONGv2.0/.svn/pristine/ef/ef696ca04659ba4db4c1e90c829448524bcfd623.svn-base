/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire;

import edu.ucsc.leeps.fire.config.Configurable;
import java.util.Random;

/**
 *
 * @author jpettit
 */
public interface FIREClient<ServerInterfaceType, ClientInterfaceType, ConfigType> {

    public void initialize();

    public int getID();

    public String getName();

    public float getTotalPoints();

    public float getPeriodPoints();

    public ServerInterfaceType getServer();

    public ClientInterfaceType getClient();

    public ConfigType getConfig();
    
    public ConfigType getConfig(int id);

    public void addConfigListener(Configurable configurable);

    public Random getRandom();

    public long getMillisLeft();

    public boolean isRunningPeriod();
}
