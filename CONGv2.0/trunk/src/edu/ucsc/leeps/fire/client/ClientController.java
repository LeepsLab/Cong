/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.client;

import edu.ucsc.leeps.fire.FIREClient;
import edu.ucsc.leeps.fire.FIREClientInterface;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.logging.Logger;
import edu.ucsc.leeps.fire.networking.NetworkUtil;
import edu.ucsc.leeps.fire.reflection.ClassFinder;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * This class is a remote wrapper that passes calls from the server to the client
 * @author jpettit
 */
public final class ClientController<ServerInterfaceType, ClientInterfaceType, ConfigType extends BaseConfig>
        implements
        FIREClient<ServerInterfaceType, ClientInterfaceType, ConfigType>,
        ClientControllerInterface<ClientInterfaceType, ConfigType> {

    public int id;
    private String name;
    private float totalPoints, periodPoints;
    private ServerInterfaceType server;
    private Map<Integer, ConfigType> configs;
    private FIREClientInterface client;
    private Set<Configurable> configListeners;
    private long millisLeft;
    private volatile boolean runningPeriod;
    private Random random = new Random();
    
    /**
     * Initializer.  Initializes the class finder.
     */
    public ClientController() {
        ClassFinder.initialize();
        configListeners = new HashSet<Configurable>();
        configs = new HashMap<Integer, ConfigType>();
    }
    
    /**
     * Sets important fields to default values and connects this controller to 
     * the server.
     */
    public void initialize() {
        id = -1;
        totalPoints = 0;
        periodPoints = 0;
        NetworkUtil.connectToServer(this);
    }

    /**
     * Set user ID.
     * @param id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Return user ID
     * @return ID
     */
    public int getID() {
        return id;
    }
    
    /**
     * Returns the name of this client, which the user input in the startup dialogue.
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the number of period points.
     * @param periodPoints
     */
    public void setPeriodPoints(float periodPoints) {
        this.periodPoints = periodPoints;
    }

    /**
     * Sets total points.
     * @param totalPoints
     */
    public void setTotalPoints(float totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * Gets total points, returns them.
     * @return total points
     */
    public float getTotalPoints() {
        return totalPoints;
    }

    /**
     * Returns period points
     * @return period Points
     */
    public float getPeriodPoints() {
        return periodPoints;
    }

    /**
     * Returns server
     * @return
     */
    public ServerInterfaceType getServer() {
        return server;
    }

    /**
     * Returns your config.
     * @return
     */
    public ConfigType getConfig() {
        return configs.get(id);
    }

    /**
     * Returns config with given id.
     * @return
     */
    public ConfigType getConfig(int id) {
        return configs.get(id);
    }

    /**
     * Creates a thread to exit. Requires user to wait a short time.
     */
    public void disconnect() {
        /* Cannot exit immediately, this is a RMI call and must return otherwise
         * the call will fail. Instead, spawn a thread to exit in a short time.
         */
        new Thread() {

            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                }
                System.exit(0);
            }
        }.start();
    }

    /**
     * Receive messages. If operation is not supported, use string "not supported
     * yet".
     * @param message
     */
    public void receiveMessage(String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Return Client interface type.
     * @return
     */
    public ClientInterfaceType getClient() {
        return (ClientInterfaceType) client;
    }

    /**
     * Set config type. Uses configurable configListeners to notify other classes
     * when configuration changes.
     * @param config
     */
    public synchronized void setConfigs(Map<Integer, ConfigType> configs) {
        this.configs = configs;
        for (Configurable configurable : configListeners) {
            configurable.configChanged(getConfig());
        }
    }
    
    /**
     * Notifies the client that the pre-period has started.
     */
    public void startPrePeriod() {
        client.startPrePeriod();
    }

    /**
     * Starts a new period.
     * Tasks are attached to the event time to tick and quicktick the client at
     * a fixed rate.
     */
    public void startPeriod() {
        runningPeriod = true;
        millisLeft = getConfig().length * 1000;
        client.startPeriod();
    }
    
    /**
     * Updates the time in th this object and the clients
     * @param millisLeft 
     */
    public void tick(int millisLeft) {
        this.millisLeft = millisLeft;
        client.tick(millisLeft);
    }

    /**
     * At the end of the period, stop event Timer, and notify the client.
     *
     */
    public void endPeriod() {
        runningPeriod = false;
        client.endPeriod();
    }

    /**
     * Adds a configListener. This enables other classes that use config to attach
     * themselves and listen for changes in the config.
     * @param configurable
     */
    public synchronized void addConfigListener(Configurable configurable) {
        configListeners.add(configurable);
    }
    
    /**
     * Returns a random number generator stored in the client
     * @return 
     */
    public Random getRandom() {
        return random;
    }
    
    /**
     * Returns the time left in the period in milliseconds.
     * @return the time left in the period in milliseconds.
     */
    public long getMillisLeft() {
        return millisLeft;
    }
    
    /**
     * Returns whether or not this client is currently running a period.
     * @return whether or not this client is currently running a period.
     */
    public boolean isRunningPeriod() {
        return runningPeriod;
    }
    
    /**
     * Creates a loop that can be used to check if this client is connected to
     * a local client instance
     */
    public void alive() {
        while (true) {
        }
    }
    
    /**
     * Sets the client this controller acts on.
     * @param client 
     */
    public void setClient(FIREClientInterface client) {
        this.client = client;
    }
    
    /**
     * Sets the server this controller refers to.
     * @param server 
     */
    public void setServer(ServerInterfaceType server) {
        this.server = server;
    }
    
    /**
     * Sets the name of this client.
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Redirects all error messages the client_errors.txt file
     */
    static {
        try {
            System.setErr(new PrintStream(Logger.getCurrentWorkingDir() + "client_errors.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
