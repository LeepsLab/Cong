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
 *
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
                    controller.alive();
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

    public static interface ConnectionListener {

        public void Disconnected(LocalClient c);
    }
}
