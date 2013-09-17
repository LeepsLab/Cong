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
 * This is the interface used to generate the object of the server when connecting
 * clients to the server.
 * @author jpettit
 */
public interface SessionControllerInterface<ServerInterfaceType, ClientInterfaceType, ConfigType extends BaseConfig> {

    /**
     * Assigns a client an id an registers it with the server.
     * @param controller
     * @param firstName
     * @param lastName
     * @return true if the given controller was registered
     */
    public void register(
            ClientControllerInterface<ClientInterfaceType, ConfigType> controller,
            String name);
    
    /**
     * Removes a client with the given id from the server
     * @param id 
     */
    public void unregister(int id);
    
    /**
     * Returns the ServerInterfaceType implementation of this instance.
     * @return 
     */
    public ServerInterfaceType getServerInterface();

    /**
     * Try to start period periodNum
     * @param periodNum
     * @return true if periodNum exists and was started
     */
    public boolean startPeriod(String period);
    
    /**
     * Ends the current period
     */
    public void endPeriod();
    
    /**
     * Configures the server with the passed configurator.
     * @param configurator
     * @throws edu.ucsc.leeps.fire.config.BaseConfig.ConfigException 
     */
    public void loadConfig(Configurator<ConfigType> configurator) throws BaseConfig.ConfigException;
    
    /**
     * Disconnects the clients and closes the program.
     */
    public void endSession();
    
    /**
     * Changes the period to the period at the passed index.
     * @param i 
     */
    public void setPeriodListIndex(int i);
}
