package edu.ucsc.leeps.fire.cong;

import edu.ucsc.leeps.fire.FIREClient;
import edu.ucsc.leeps.fire.FIREServer;
import edu.ucsc.leeps.fire.client.ClientController;
import edu.ucsc.leeps.fire.cong.client.ClientInterface;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.ServerInterface;
import edu.ucsc.leeps.fire.server.ServerController;

/**
 *
 * @author jpettit
 */
public class FIRE {

    public static FIREServer<ClientInterface, Config> server;
    public static FIREClient<ServerInterface, ClientInterface, Config> client;

    public static void startServer() {
        server = new ServerController<ServerInterface, ClientInterface, Config>();
    }

    public static void startClient() {
        client = new ClientController<ServerInterface, ClientInterface, Config>();
        client.initialize();
    }
}
