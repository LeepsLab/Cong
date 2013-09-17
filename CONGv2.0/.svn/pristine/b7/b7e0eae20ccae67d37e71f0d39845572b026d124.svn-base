/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.client;

import edu.ucsc.leeps.fire.config.BaseConfig;
import java.util.Map;

public interface ClientControllerInterface<ClientInterfaceType, ConfigType extends BaseConfig> {

    /**
     * Assigns each client a unique ID number.
     * @param id ID number of the client
     */
    public void setID(int id);

    /**
     * Sets the configs for the period.
     * @param config
     */
    public void setConfigs(Map<Integer, ConfigType> configs);

    /**
     * Once the config is loaded, and the appropriate number of clients are
     * registered, the start button is clicked. This starts the pre-period,
     * which is a set delay between the clicking of the start button, and the
     * beginning of the period. This value can be changed in the config file.
     * The number of seconds remaining in the pre period is not displayed in the
     * StateDialog. The text for state changes to "Running Pre-Period". If there
     * is no pre-period, goes straight to startPeriod.
     */
    public void startPrePeriod();

    /**
     * After the pre-period, the actual period begins. The period length is
     * determined by the value specified in the config. Once per second, the
     * client runs a tick event. This ensures that all the subjects' time
     * remaining is synchronized. Time remaining is displayed in the Dialog.
     * State text is set to "Running Period".
     */
    public void startPeriod();
    
    public void tick(int millisLeft);

    /**
     * After the number of seconds specified by the config have elapsed, the
     * period ends. It then checks to see if the client is ready to start. Goes
     * back to the waiting to start state.
     */
    public void endPeriod();

    public ClientInterfaceType getClient();

    /**
     * Disconnects client from server, sending any errors.
     */
    public void disconnect();

    /**
     * Sets points earned in a period.
     * @param periodPoints Points earned in  a period
     */
    public void setPeriodPoints(float periodPoints);

    /**
     * Sets total points earned in a session
     * @param totalPoints Total points earned in a session.
     */
    public void setTotalPoints(float totalPoints);

    public void alive();
}
