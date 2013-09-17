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
import java.io.Serializable;

/**
 * This class is used to store information relating to a client on the server.
 * @author jpettit
 */
public class LocalClient<ServerInterfaceType, ClientInterfaceType, ConfigType extends BaseConfig> implements Serializable {

    public int id;
    public transient ClientControllerInterface<ClientInterfaceType, ConfigType> controller;
    public transient ClientInterfaceType client;
    public transient ConnectionListener l;
    public String name;
    public float totalPoints;
    public float periodPoints;
    public boolean connected;
    
    /**
     * Initializer. Starts a thread that continously checks if this client is 
     * still connected and notifies the server when it is disconnected.
     * @param id the id of the client
     * @param controller the client controller that this local client listenst to
     * @param client the client attached to the client controller
     * @param name the name of the client
     * @param l a connection listener
     */
    public LocalClient(
            int id,
            final ClientControllerInterface<ClientInterfaceType, ConfigType> controller,
            ClientInterfaceType client,
            String name,
            ConnectionListener l) {
        this.id = id;
        this.controller = controller;
        this.client = client;
        this.name = name;
        this.totalPoints = 0;
        this.periodPoints = 0;
        this.l = l;
        connected = true;
        new Thread() {

            @Override
            public void run() {
                try {
                    controller.alive();//this methods starts a infinite loop that thows an exception when the connection is lost
                } catch (Exception ex) {
                }
                connected = false;
                LocalClient.this.l.Disconnected(LocalClient.this);
            }
        }.start();
    }

    @Override
    public String toString() {
        return String.format("%s(%d) %s %.2f/%.2f", connected ? "" : "[x] ", id, name, periodPoints, totalPoints);
    }
    
    /**
     * Interface for a class that responds to the local client being disconnected
     */
    public static interface ConnectionListener {
        
        /**
         * Called when the the client controller that this local client listens
         * to is disconnected from the server
         * @param c the local client instance that this listener was attached to
         */
        public void Disconnected(LocalClient c);
    }
}
