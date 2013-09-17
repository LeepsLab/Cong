/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong;

import edu.ucsc.leeps.fire.FIREClient;
import edu.ucsc.leeps.fire.FIREServer;
import edu.ucsc.leeps.fire.client.ClientController;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.ServerInterface;
import edu.ucsc.leeps.fire.server.ServerController;

/**
 * This class holds static methods that initialize the server and clients as well as
 * references to the server controller and client controller once they are initialized
 * @see edu.ucsc.leeps.fire.FIREClient;
 * @see edu.ucsc.leeps.fire.FIREServer;
 * @see edu.ucsc.leeps.fire.server.ServerController;
 * @see edu.ucsc.leeps.fire.client.ClientController;
 * @author jpettit
 */
public class FIRE {
    
    /**The static server controller instance for this session*/
    public static FIREServer<ClientInterface, Config> server;
    /**The static client controller instance for this session*/
    public static FIREClient<ServerInterface, ClientInterface, Config> client;
    
    /**
     * Initializes the server controller
     */
    public static void startServer() {
        server = new ServerController<ServerInterface, ClientInterface, Config>();
    }
    
    /**
     * Initializes the client controller
     */
    public static void startClient() {
        client = new ClientController<ServerInterface, ClientInterface, Config>();
        client.initialize();
    }
}
