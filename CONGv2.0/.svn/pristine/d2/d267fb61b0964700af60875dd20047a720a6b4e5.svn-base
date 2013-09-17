/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.server;

import edu.ucsc.leeps.fire.client.ClientControllerInterface;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.config.Configurator;
import edu.ucsc.leeps.fire.reflection.ObjectMapper;
import java.util.List;

/**
 *
 * @author jpettit
 */
public interface SessionControllerInterface<ServerInterfaceType, ClientInterfaceType, ConfigType extends BaseConfig> {

    /**
     *
     * @param controller
     * @param firstName
     * @param lastName
     * @return true if the given controller was registered
     */
    public void register(
            ClientControllerInterface<ClientInterfaceType, ConfigType> controller,
            String name);

    public void unregister(int id);

    public ServerInterfaceType getServerInterface();

    /**
     * Try to start period periodNum
     * @param periodNum
     * @return true iff periodNum exists and was started
     */
    public boolean startPeriod(String period);

    public void endPeriod();

    public void loadConfig(Configurator<ConfigType> configurator) throws BaseConfig.ConfigException;

    public void endSession();

    public void setPeriodListIndex(int i);
}
