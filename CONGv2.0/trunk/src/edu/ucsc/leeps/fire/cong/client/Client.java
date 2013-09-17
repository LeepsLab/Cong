/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong.client;

import edu.ucsc.leeps.fire.FIREClientInterface;
import edu.ucsc.leeps.fire.config.Configurable;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.gui.*;
import edu.ucsc.leeps.fire.cong.client.gui.charting.C_D_SF;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.cong.server.PricingPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.QWERTYPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.ScriptedPayoffFunction;
import fullscreen.FullScreen;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.media.opengl.GLException;
import javax.swing.JFrame;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * This class describes the client object and includes the main method for initializing
 * cong clients.  The client holds the current strategies in a State 
 * object and implements interfaces used by the server to access and send data.  
 * This clients strategy is constantly being sent to the server by an thread and 
 * the other strategies stored in this client are constantly being updated by another
 * thread.
 * @see edu.ucsc.leeps.fire.cong.client.State
 * @see edu.ucsc.leeps.fire.cong.client.StrategyChanger
 * @author jpettit
 */
public class Client extends PApplet implements ClientInterface, FIREClientInterface, Configurable<Config> {
    private int group = 0;
    /**whether the fire.debug system property exists or not*/
    public static boolean ALLOW_DEBUG = System.getProperty("fire.debug") != null;
    /**always true*/
    public static boolean USE_OPENGL = true;
    /**
     * The static state of this client.  The state contains the client's strategy,
     * id, match strategy, and strategy changer
     */
    public static State state;
    /**whether or not cong is in debug mode*/
    public boolean debug;
    public int framesPerUpdate;
    private PeriodInfo periodInfo;
    //only one shown
    private TwoStrategySelector heatmap2d;
    private ThreeStrategySelector simplex;
    private PureStrategySelector pureMatrix;
    private OneStrategyStripSelector strip;
    private QWERTYStrategySelector qwerty;
    private BubblesSelector bubbles;
    private Sprite selector;
    private C_D_SF chart;
    private Chart payoffChart, strategyChart;
    private Chart rChart, pChart, sChart;
    // heatmap legend off/on
    private ChartLegend legend;
    private StrategyChanger strategyChanger;
    private JFrame chatFrame;
    private Chatroom chatroom;
    private boolean haveInitialStrategy;
    private int INIT_WIDTH, INIT_HEIGHT;
    public PFont size14, size14Bold, size16, size16Bold, size18, size18Bold, size24, size24Bold;
    private Agent agent = new Agent();
    private boolean fullscreen = false;
    private boolean resize = false;
    // strategy updates
    private StrategyUpdater updater;
    //FullSreen
    private FullScreen Fullscreen;
    private WindowAdapter windowListener = new WindowAdapter() {

        @Override
        public void windowIconified(WindowEvent e) {
            resize = true;
        }
    };
    
    /**
     * Constructor.  Attaches the client to the local fire client, sets initial
     * variables, and starts the updater threads.
     */
    public Client() {
        if (System.getProperty("useOpenGL") != null) {
            USE_OPENGL = Boolean.parseBoolean(System.getProperty("useOpenGL"));
        }
        FIRE.client.addConfigListener(this);
        loadLibraries();
        noLoop();
        INIT_WIDTH = 900;
        INIT_HEIGHT = 600;
        frame = new JFrame();
        frame.setTitle("CONG - " + FIRE.client.getName());
        ((JFrame) frame).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.add(this);
        frame.setSize(INIT_WIDTH, INIT_HEIGHT);
        frame.setResizable(false);
        frame.addWindowListener(windowListener);
        init();
        noLoop();
        frame.setVisible(true);
        loop();
        debug = ALLOW_DEBUG;
        if (FIRE.client.getName().toLowerCase().contains("robo")) {
            agent.start();
            noLoop();
        }
        updater = new StrategyUpdater();
        updater.start();
    }
    
    /**
     * Returns whether this client has an initial strategy for the pre-period.
     * @return 
     */
    public boolean haveInitialStrategy() {
        return haveInitialStrategy;
    }
    
    /**
     * Tells the client to start the pre-period which is used to set initial 
     * strategies before the period starts.
     */
    public void startPrePeriod() {
        state.id = FIRE.client.getID();
        if (FIRE.client.getConfig().initialStrategy == null) {
            haveInitialStrategy = false;
        } else {
            haveInitialStrategy = true;
        }
        state.currentPercent = 0;
        if (simplex.visible) {
            selector = simplex;
            strategyChanger.selector = simplex;
        } else if (heatmap2d.visible) {
            selector = heatmap2d;
            strategyChanger.selector = heatmap2d;
        } else if (pureMatrix.visible) {
            selector = pureMatrix;
            strategyChanger.selector = pureMatrix;
        } else if (strip.visible) {
            selector = strip;
            strategyChanger.selector = strip;
        } else if (qwerty.visible) {
            selector = qwerty;
            strategyChanger.selector = qwerty;
        } else {
            selector = bubbles;
            strategyChanger.selector = bubbles;
        }
        if (strategyChanger.selector != null) {
            strategyChanger.selector.startPrePeriod();
        }
        payoffChart.clearAll();
        strategyChart.clearAll();
        rChart.clearAll();
        pChart.clearAll();
        sChart.clearAll();
    }
    
    /**
     * Tells the client that the period has started
     */
    public void startPeriod() {
        state.startPeriod();
        if (FIRE.client.getConfig().preLength == 0) {
            heatmap2d.setEnabled(true);
            pureMatrix.setEnabled(true);
            strip.setEnabled(true);
            payoffChart.clearAll();
            strategyChart.clearAll();
            rChart.clearAll();
            pChart.clearAll();
            sChart.clearAll();
            chart.clearAll();
        }
        simplex.setEnabled(true);
        if (chatroom != null) {
            chatroom.startPeriod();
        }

        strategyChanger.startPeriod();
        strategyChanger.selector.startPeriod();

        if (FIRE.client.getConfig().subperiods != 0) {
            payoffChart.endSubperiod();
            strategyChart.endSubperiod();
            rChart.endSubperiod();
            pChart.endSubperiod();
            sChart.endSubperiod();
        }
        periodInfo.startPeriod();
    }
    
    /**
     * Tells the client that the period has ended
     */
    public void endPeriod() {
        strategyChanger.endPeriod();
        state.endPeriod();

        if (chatroom != null) {
            chatroom.endPeriod();
        }

        if (FIRE.client.getConfig().subperiods == 0) {
            payoffChart.updateLines();
            strategyChart.updateLines();
            rChart.updateLines();
            pChart.updateLines();
            sChart.updateLines();
        }
        System.err.println("Flushing updates: " + updater.queue.size());
        updater.queue.clear();
    }
    
    /**
     * Sets whether or not the strategy changer for the client is paused.  Pausing
     * the strategy changer stops the server from being updated with this clients
     * strategy.
     * @param isPaused 
     */
    public void setIsPaused(boolean isPaused) {
        strategyChanger.setPause(isPaused);
    }
    
    /**
     * Updates seconds left in the period for this client.
     * @param millisLeft 
     */
    public void tick(int millisLeft) {
        periodInfo.setSecondsLeft(millisLeft / 1000);
    }
    
    /**
     * Adds a strategy change to the updater queue
     * @param whoChanged the id of client who's strategy changed
     * @param strategies the new strategies
     * @param timestamp the time this change occurred
     */
    public void setStrategies(int whoChanged, Map<Integer, float[]> strategies, long timestamp) {
        if (FIRE.client.isRunningPeriod()) {
            updater.queue.add(new StrategyUpdateEvent(whoChanged, strategies, null, System.nanoTime() - Client.state.periodStartTime));
        }
    }
    
    /**
     * Adds a match strategy change to the updater queue
     * @param whoChanged the id of client who's strategy changed
     * @param strategies the new match strategies
     * @param timestamp the time this change occurred
     */
    public void setMatchStrategies(int whoChanged, Map<Integer, float[]> matchStrategies, long timestamp) {
        if (FIRE.client.isRunningPeriod()) {
            updater.queue.add(new StrategyUpdateEvent(whoChanged, null, matchStrategies, System.nanoTime() - Client.state.periodStartTime));
        }
    }
    
    /**
     * Tells the client to end the current sub-period
     * @param subperiod the number of this sup-period
     * @param strategies the strategies at the end of this sub-period
     * @param matchStrategies the match strategies at the end of this sub-period
     * @param payoff the payoff for this client
     * @param matchPayoff the match payoff for this client
     * @param turn whose turn it is
     * @param group the group number of the client
     */
    public void endSubperiod(
            final int subperiod,
            final Map<Integer, float[]> strategies,
            final Map<Integer, float[]> matchStrategies,
            final float payoff, final float matchPayoff,int turn,int group) {
        this.group = group;
        state.turn = turn;
        state.endSubperiod(subperiod, strategies, matchStrategies);
        strategyChanger.endSubperiod(subperiod);
        if (!(FIRE.client.getConfig().payoffFunction instanceof PricingPayoffFunction)) {
            payoffChart.updateLines();
            strategyChart.updateLines();
            rChart.updateLines();
            pChart.updateLines();
            sChart.updateLines();
        }
    }

    /**
     * Adds a message to this clients chatroom 
     * @param message 
     */
    public void newMessage(String message) {
        if (chatroom != null) {
            chatroom.newMessage(message);
        }
    }
    
    /**
     * Returns true
     * @return true
     */
    public boolean readyForNextPeriod() {
        return true;
    }

    /**
     * Terminates the virtual machine upon disconnect.
     */
    public void disconnect() {
        System.exit(0);
    }

    @Override
    /**
     * Sets up this client GUI
     */
    public void setup() {
        textMode(MODEL);
        //textMode(SCREEN);
        hint(ENABLE_NATIVE_FONTS);
        if (USE_OPENGL) {
            try {
                size(INIT_WIDTH, INIT_HEIGHT - 40, OPENGL);
            } catch (GLException ex1) {
                try {
                    size(INIT_WIDTH, INIT_HEIGHT - 40, OPENGL);
                } catch (GLException ex2) {
                }
            }
            hint(DISABLE_OPENGL_2X_SMOOTH);
            hint(DISABLE_OPENGL_ERROR_REPORT);
            hint(DISABLE_DEPTH_TEST);
        } else {
            size(INIT_WIDTH, INIT_HEIGHT - 40, P2D);
            frameRate(35);
        }
        setupFonts();
        textFont(size24);
        width = INIT_WIDTH;
        height = INIT_HEIGHT - 40;

        int leftMargin = 20;
        int topMargin = 20;
        float textHeight = textAscent() + textDescent();
        int matrixSize = (int) (height - (4 * textHeight) - 120);
        int counterpartMatrixSize = 100;
        strategyChanger = new StrategyChanger();
        Client.state = new State(strategyChanger);
        heatmap2d = new TwoStrategySelector(
                null, leftMargin, topMargin + counterpartMatrixSize + 30,
                matrixSize, counterpartMatrixSize,
                this);
        simplex = new ThreeStrategySelector(
                null, 60, 250, 300, 600,
                this);
        pureMatrix = new PureStrategySelector(
                null, leftMargin, topMargin + counterpartMatrixSize + 30,
                matrixSize, this);
        strip = new OneStrategyStripSelector(null,
                leftMargin + 7 * matrixSize / 8, topMargin + counterpartMatrixSize + 30,
                matrixSize / 8, matrixSize,
                this);
        qwerty = new QWERTYStrategySelector(
                null, leftMargin, topMargin + counterpartMatrixSize + 30,
                matrixSize,
                this);
        bubbles = new BubblesSelector(null, leftMargin, topMargin + counterpartMatrixSize,
                matrixSize, matrixSize, this);
        periodInfo = new PeriodInfo(
                null, heatmap2d.width - 150, 20 + topMargin, this);
        int chartLeftOffset = heatmap2d.width;
        int chartWidth = (int) (width - chartLeftOffset - 2 * leftMargin - 80);
        int chartMargin = 30;
        int strategyChartHeight = 100;
        int threeStrategyChartHeight = 30;
        int payoffChartHeight = (int) (height - strategyChartHeight - 2 * topMargin - chartMargin - 10);
        chart = new C_D_SF(null, chartLeftOffset + 80 + leftMargin, strategyChartHeight + topMargin + chartMargin,
                chartWidth, matrixSize, (int) (chartWidth + (7f * matrixSize) / 8f));
        strategyChart = new Chart(
                null, chartLeftOffset + 80 + leftMargin, topMargin,
                chartWidth, strategyChartHeight,
                simplex, Chart.Mode.TwoStrategy);
        payoffChart = new Chart(
                null, chartLeftOffset + 80 + leftMargin, strategyChart.height + topMargin + chartMargin,
                chartWidth, payoffChartHeight,
                simplex, Chart.Mode.Payoff);
        rChart = new Chart(
                null, chartLeftOffset + 80 + leftMargin, topMargin,
                chartWidth, threeStrategyChartHeight,
                simplex, Chart.Mode.RStrategy);
        pChart = new Chart(
                null, chartLeftOffset + 80 + leftMargin, topMargin + threeStrategyChartHeight + 5,
                chartWidth, threeStrategyChartHeight,
                simplex, Chart.Mode.PStrategy);
        sChart = new Chart(
                null, chartLeftOffset + 80 + leftMargin, topMargin + 2 * (threeStrategyChartHeight + 5),
                chartWidth, threeStrategyChartHeight,
                simplex, Chart.Mode.SStrategy);
        legend = new ChartLegend(
                null, (int) (strategyChart.origin.x + strategyChart.width), (int) strategyChart.origin.y + strategyChartHeight + 3,
                0, 0);
        smooth();
        Fullscreen = new FullScreen(this);
        Fullscreen.setShortcutsEnabled(false);
    }

    @Override
    /**
     * Called to update the GUI
     */
    public void draw() {
        try {
            doDraw();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * The clients main draw function
     */
    private void doDraw() {
        if (resize) {
            setupFonts();
            textFont(size14Bold);
            resize = false;
        }

        background(255);
        if (FIRE.client.getConfig() == null) {//period has not started
            fill(0);
            String s = "Please wait for the experiment to begin";
            textSize(14);
            text(s, Math.round(width / 2 - textWidth(s) / 2), Math.round(height / 2));
            return;
        }

        if (FIRE.client.isRunningPeriod()) {
            float length = FIRE.client.getConfig().length * 1e9f;
            float elapsed = System.nanoTime() - Client.state.periodStartTime;
            state.currentPercent = elapsed / length;
            if (Float.isNaN(FIRE.client.getConfig().revealLambda)) {
                Client.state.updatePoints();
            }
        }

        //ScriptedPayoffFunctions provide thier own draw functions
        if (FIRE.client.getConfig().payoffFunction instanceof ScriptedPayoffFunction) {
            ScriptedPayoffFunction payoffFunction = (ScriptedPayoffFunction) FIRE.client.getConfig().payoffFunction;
            payoffFunction.draw(this);
            return;
        }

        if (FIRE.client.isRunningPeriod()) {
            if (FIRE.client.getConfig().subperiods == 0) {
                if (!(FIRE.client.getConfig().payoffFunction instanceof PricingPayoffFunction)) {
                    payoffChart.updateLines();
                    strategyChart.updateLines();
                    rChart.updateLines();
                    pChart.updateLines();
                    sChart.updateLines();
                }
            }
        }

        if (selector != null) {
            selector.draw(this);
        }
        if (FIRE.client.getConfig() != null) {
            if (!(FIRE.client.getConfig().payoffFunction instanceof PricingPayoffFunction)) {
                payoffChart.draw(this);
                strategyChart.draw(this);
                rChart.draw(this);
                pChart.draw(this);
                sChart.draw(this);
            } else {
                chart.draw(this);
            }
        }
        legend.draw(this);
        if (FIRE.client.getConfig().preLength > 0 && !haveInitialStrategy) {
            float textHeight = textDescent() + textAscent() + 8;
            fill(255, 50, 50);
            text("Please choose an initial strategy.", Math.round(periodInfo.origin.x), Math.round(periodInfo.origin.y - textHeight));
        }
        periodInfo.draw(this);
        if (debug) {
            String frameRateString = String.format("FPS: %.2f, Agent: %s", frameRate, agent.paused ? "off" : "on");
            if (frameRate < 8) {
                fill(255, 0, 0);
            } else {
                fill(0);
            }
            text(frameRateString, 10, height - 10);    
        }
    }
    
    /**
     * Reconfigures this client
     * @param config the new Config object
     */
    public void configChanged(final Config config) {
        agent.agentText = config.agent.agentText;
        agent.configure(config);
        if (config.chatroom && chatFrame == null) {
            new Thread() {

                @Override
                public void run() {
                    chatFrame = new JFrame("Chat");
                    chatroom = new Chatroom(chatFrame);
                    chatFrame.add(chatroom);
                    chatFrame.pack();
                    chatFrame.setVisible(false);
                    chatroom.configure(config);
                }
            }.start();
        } else if (chatFrame != null && chatroom != null) {
            chatroom.configure(config);
        }

        framesPerUpdate = Math.round(frameRateTarget * 0.5f);
        payoffChart.setVisible(true);
        if (config.payoffFunction instanceof QWERTYPayoffFunction == false) {
            legend.setVisible(true);
        }
        strategyChart.setVisible(false);
        rChart.setVisible(false);
        pChart.setVisible(false);
        sChart.setVisible(false);
        if (config.selector == Config.StrategySelector.strip) {
            if (config.indefiniteEnd != null && config.subperiods != 0) {
                payoffChart.setVisible(false);
                legend.setVisible(false);
                strategyChart.setVisible(false);
                strip.origin.x = 10;
            } else {
                payoffChart.setVisible(true);
                legend.setVisible(false);
                strategyChart.setVisible(false);
                payoffChart.width = width - 110;
                payoffChart.origin.x = 100;
                strip.origin.x = 10;
                payoffChart.configChanged(config);
            }
        }
        if (config.selector == Config.StrategySelector.bubbles) {
            strategyChart.setVisible(false);
            payoffChart.setVisible(false);
            legend.setVisible(false);
            bubbles.width = Math.round(0.7f * width);
            bubbles.origin.x = 0.15f * width;
        }
        if (config.selector == Config.StrategySelector.simplex) {
            rChart.setVisible(true);
            pChart.setVisible(true);
            sChart.setVisible(true);
            legend.setVisible(true);
            strategyChart.setVisible(true);
        }
        if (config.selector == Config.StrategySelector.heatmap2d) {
            payoffChart.setVisible(true);
            strategyChart.setVisible(true);
            heatmap2d.setVisible(true);
        }
    }

    @Override
    /**
     * Manages key input. d turns on debug mode, a pauses the agent, alt-f11 toggles 
     * fullscreen using swing, f11 toggles fullscreen using the processing api,
     * and r resizes the screen.
     */
    public void keyPressed(KeyEvent ke) {
        if (ALLOW_DEBUG && ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_D) {
            debug = !debug;
        } else if (ALLOW_DEBUG && ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_A) {
            agent.paused = !agent.paused;
        } else if (ke.isAltDown() && ke.getKeyCode() == KeyEvent.VK_F11) {
            fullscreen = !fullscreen;
            if (fullscreen) {
                frame.removeAll();
                frame.dispose();
                frame = new JFrame();
                frame.setTitle("CONG - " + FIRE.client.getName());
                ((JFrame) frame).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.add(this);
                frame.setResizable(false);
                frame.setUndecorated(true);
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
                frame.setVisible(true);
                frame.addWindowListener(windowListener);
            } else {
                frame.removeAll();
                frame.dispose();
                frame = new JFrame();
                frame.setTitle("CONG - " + FIRE.client.getName());
                ((JFrame) frame).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.add(this);
                frame.setSize(INIT_WIDTH, INIT_HEIGHT);
                frame.setResizable(false);
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
                frame.setVisible(true);
                frame.addWindowListener(windowListener);
            }
            resize = true;
        } else if (ke.isAltDown() && ke.getKeyCode() == KeyEvent.VK_R) {
            resize = true;
        } else if (keyCode == KeyEvent.VK_F11) {
            if (Fullscreen.isFullScreen()) {
                Fullscreen.leave();
            } else {
                Fullscreen.enter();
            }
        }
    }

    @Override
    /**
     * Adds the typed key to the chatroom if the chatroom exists
     */
    public void keyTyped(KeyEvent ke) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        if (chatroom != null) {
            chatroom.addCharacter(ke.getKeyChar());
        }
    }
    
    /**
     * Convenience class used to store strategy updates by the StrategyUpdater
     */
    private class StrategyUpdateEvent {

        public int changedId;
        public Map<Integer, float[]> strategies;
        public Map<Integer, float[]> matchStrategies;
        public long timestamp;
        
        /**
         * Initializer
         * @param changedId
         * @param strategies
         * @param matchStrategies
         * @param timestamp 
         */
        public StrategyUpdateEvent(int changedId, Map<Integer, float[]> strategies, Map<Integer, float[]> matchStrategies, long timestamp) {
            this.changedId = changedId;
            this.strategies = strategies;
            this.matchStrategies = matchStrategies;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Class defining a thread that constantly updates the clients strategies by
     * constantly pulling updates off of a blocking queue
     */
    private class StrategyUpdater extends Thread {

        private BlockingQueue<StrategyUpdateEvent> queue;

        public StrategyUpdater() {
            this.queue = new LinkedBlockingQueue<StrategyUpdateEvent>();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    StrategyUpdateEvent event = queue.take();
                    if (queue.size() >= 100) {
                        System.err.println("WARNING: Input queue depth = " + queue.size());
                        queue.clear();
                    }
                    if (event.strategies != null) {
                        state.setStrategies(event.changedId, event.strategies, event.timestamp);
                    } else if (event.matchStrategies != null) {
                        state.setMatchStrategies(event.changedId, event.matchStrategies, event.timestamp);
                    } else {
                        throw new IllegalArgumentException("null strategy in update event");
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Sets up the fonts used by the GUI
     */
    private void setupFonts() {
        try {
            InputStream fontInputStream;
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-14.vlw");
            size14 = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-Bold-14.vlw");
            size14Bold = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-16.vlw");
            size16 = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-Bold-16.vlw");
            size16Bold = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-18.vlw");
            size18 = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-Bold-18.vlw");
            size18Bold = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-24.vlw");
            size24 = new PFont(fontInputStream);
            fontInputStream = Client.class.getResourceAsStream("resources/DejaVuSans-Bold-24.vlw");
            size24Bold = new PFont(fontInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Loads the localized graphics libraries to be uses by the GUI
     */
    private void loadLibraries() {
        System.err.println("os.arch: " + System.getProperty("os.arch"));
        File path = null;
        try {
            path = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            path = path.getParentFile();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        String libDir = null;
        if (System.getProperty("os.name").contains("Win")) {
            if (System.getProperty("os.arch").contains("64")) {
                libDir = "win-64-bit";
            } else {
                libDir = "win-32-bit";
            }
        } else if (System.getProperty("os.name").contains("Mac")) {
            if (System.getProperty("os.arch").contains("64")) {
                libDir = "mac-64-bit";
            } else {
                libDir = "mac-ppc-bit";
            }
        } else if (System.getProperty("os.name").contains("Linux")) {
            if (System.getProperty("os.arch").contains("64")) {
                libDir = "linux-64-bit";
            } else {
                libDir = "linux-32-bit";
            }
        }
        if (libDir != null) {
            addDir(new File(new File(path, "lib"), libDir).getAbsolutePath());
            addDir(new File("lib", libDir).getAbsolutePath());
        } else {
            // error
        }
    }
    /**
     * This enables modifies java.library.path at runtime
     * From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
     */ 
    public static void addDir(String s) {
        System.err.println("adding " + s + " to class path");
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Main method for the client
     * @param args 
     */
    public static void main(String[] args) {
        FIRE.startClient();
    }
}
