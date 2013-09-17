/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire;

import edu.ucsc.leeps.fire.config.BaseConfig;
import java.util.Map;

/**
 *
 * @author jpettit
 */
public interface FIREServerInterface<ClientInterfaceType, ConfigType extends BaseConfig> {

    public void setClients(Map<Integer, ClientInterfaceType> clients);

    /**
     * * Uses data from the config file to configure period. This is done after
     * clients are enrolled in the period, and before the start of pre-period
     */
    public void configurePeriod();

    /**
     * After the period is configured, and the appropriate number of clients
     * added, the start period button is clicked. This initializes the pre-period,
     * which is a number of seconds long, as specified in the config file (if
     * applicable). After the given number of seconds have elapsed, the server
     * ends the pre-period.
     * @return True when the number of seconds specified for the pre-period have
     * elapsed.
     *
     */
    public boolean readyToEndPrePeriod();

    /**
     * Begins the period for all of the registered clients. Records the starting
     * time of the period in HH:MM:SS form.
     * @param periodStartTime Time of day when period is started.
     *
     */
    public void startPeriod(long periodStartTime);

    /**
     * Ends period for all registered clients.
     */
    public void endPeriod();

    /**
     * Creates a tick event once every 1/10 of a second (100 milliseconds).
     * This ensures all clients are synchronized.
     *
     * @param millisLeft Number of milliseconds left in the period.
     */
     
    public void tick(int millisLeft);
}
