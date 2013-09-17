/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/
package edu.ucsc.leeps.fire.cong.config;

import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Agent;
import edu.ucsc.leeps.fire.cong.client.gui.Line;
import edu.ucsc.leeps.fire.cong.server.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class holds variables used to describe cong's behavior.  Fields are read
 *  from the configuration .csv file and matched to the fields of the same name
 * in the class.  In general, variable in the config should be considered read 
 * only.
 * @author jpettit
 */
public class Config extends BaseConfig {
    
    /**
     * The types of strategy selectors
     */
    public enum StrategySelector {

        heatmap2d, simplex, bubbles, strip, pure, qwerty,}
    
    /**
     * The types of displays
     */
    public enum MatrixDisplayType {

        HeatmapSingle, HeatmapBoth, Corners
    }
    
    /**
     * The different match groups.  Par indicates the the groups are to be 
     * matched to other groups while self indicates that each group will be
     * matched to itself.
     */
    public enum MatchGroup {

        self, pair;
    }
    
    /**Config field.  Used to indicate randomized turns in a special turn based treatment*/
    public boolean diceRoll;
    /**Config field. Used to indicate that clients are to take turns setting their strategies.
       This treatment is intended to work with a payoff script and probably will not
       work with a selector*/
    public boolean turnBased;
    /**Determines the delta value used when calculating continuous time strategies*/ 
    public float percentChangePerSecond;
    /**The payoff function for the period*/
    public PayoffFunction payoffFunction;
    /**The payoff function used to calculate match payoffs*/
    public PayoffFunction counterpartPayoffFunction;
    /**The number of groups in the period*/
    public int numGroups;
    /**the number of client's in each group*/
    public int groupSize;
    
    /**Whether or not groups are assigned explicitely in the configuration file*/
    public boolean assignedGroups;
    /**whether or not a client is to include itself when calculating the average strategy*/
    public boolean excludeSelf;
    /**The number of sub-periods in the current period*/
    public int subperiods;
    /**Whether the initial strategy is to be a random number between 1 and 0
     rather than just 1 or 0*/
    public boolean mixed;
    /**The current strategy selector.  Selectors are used by the client to describe 
     and draw the gui and provide an interface that allows the user to select
     their strategy*/
    public StrategySelector selector = StrategySelector.bubbles;
    /**used to set the modes of the TwoStrategySelctor or BimatrixSelector if those
     are used*/
    public MatrixDisplayType matrixDisplay;
    /**The payoff lines of the current client. Used for charting*/
    public Line yourPayoff, matchPayoff;
    /**The strategy lines of the current client. Used for charting*/
    public Line yourStrategy, matchStrategy;
    /**The threshold line of the current client. Used for charting*/
    public Line thresholdLine;
    /**Labels for chart*/
    public String rLabel, pLabel, sLabel, shortRLabel, shortPLabel, shortSLabel;
    /**Colors used by the ThreeStrategySelector*/
    public Color rColor, pColor, sColor;
    /**does not seem to show up anywhere else*/
    public boolean showRPSSliders;
    /**The DesisionDelay objects describing how strategy changes will be delayed
     if specified.  In the csv file delay and initialDelay can be indicated with
     either "uniform" or "poisson"*/
    public DecisionDelay initialDelay, delay;
    public IndefiniteEnd indefiniteEnd;
    /**whether the legend is to be drawn when drawing a heatmap*/
    public boolean showHeatmapLegend;
    /**Whether or not a chatroom is used with the current treatment*/
    public boolean chatroom;
    /**If a chatroom is used, whether free or limited chat is to be used*/
    public boolean freeChat;
    public boolean negativePayoffs;
    /**whether a sigmoid heat map is to be used*/
    public boolean sigmoidHeatmap;
    /**alpha value for a sigmoid heat map*/
    public float sigmoidAlpha;
    /**beta value for a sigmoid heat map*/
    public float sigmoidBeta;
    /**used by the three strategy selector and pure strategy selector to indicate
     whether their matrices should be drawn.  Should probably always be true.*/
    public boolean showMatrix;
    /**whether or not the label for the time axis on a Chart object should be drawn*/
    public boolean showPayoffTimeAxisLabels;
    /**whether or not the groups are shuffled after every subperiod*/
    public boolean subperiodRematch;
    /**whether or not probability payoffs are to be used*/
    public boolean probPayoffs;
    /**If a heatmap is used, whether or not the heatmap is to be drawn*/
    public boolean showHeatmap;
    /**Causes the clients target to be set to either 1 or 0 until it reaches the
     set target*/
    public boolean trajectory;
    /**the menue used by the limited chat*/
    public ChatMenu menu;
    /**true if colors are to be assigned to each member or a group and false if one color
     is to be used for the current client's strategy and another for all of the other
     members in the group*/
    public boolean objectiveColors;
    /**whether or not the minimum and maximum strategy values are to be drawn*/
    public boolean showSMinMax;
    /**The type of matching used for groups.  Either pair or self*/
    public MatchGroup matchType;
    /**whether members of a group are to take turns being enabled to change their
     strategies*/
    public boolean turnTaking;
    /**whether or not potential payoffs are to be drawn by the bubbles selector*/
    public boolean potential;
    /**the size of the grid that target's are to be locked to.  Default value is 
     * NaN which results in a grid with 0.01 sized increments*/
    public float grid;
    public int xAxisTicks;
    public int infoDelay;
    /**string used to indicate period points when drawing the period's info. 
     * Default value is "Current Points:"*/
    public String periodPointsString = "Current Points:";
     /**string used to indicate total points when drawing the period's info. 
     * Default value is "Previous Points:"*/
    public String totalPointsString = "Previous Points:";
    public String params;
    /**Source file for an agent script*/
    public String agentSource;
    /**"In"*/
    public String inString = "In";
    /**"Out"*/
    public String outString = "Out";
    public static final Class matrix2x2 = TwoStrategyPayoffFunction.class;
    public static final Class matrix3x3 = ThreeStrategyPayoffFunction.class;
    public static final Class qwerty = QWERTYPayoffFunction.class;
    public static final Class pricing = PricingPayoffFunction.class;
    public static final Class sum = SumPayoffFunction.class;
    public static final Class script = ScriptedPayoffFunction.class;
    public static final Class line = Line.class;
    public static final Class threshold = ThresholdPayoffFunction.class;
    public static final Class decisionDelay = DecisionDelay.class;
    public static final Class chatMenu = ChatMenu.class;
    public static final Class uniform = IndefiniteEnd.Uniform.class;
    public static final Class assigned = IndefiniteEnd.Assigned.class;
    // per-client
    public float[] initialStrategy;
    public float initial = Float.NaN;
    public float initial0 = Float.NaN;
    public float initial1 = Float.NaN;
    public int matchID;
    public float revealLambda = Float.NaN;
    public String revealTimes = "";
    public boolean revealAll;
    public boolean isCounterpart;
    public int playersInGroup;
    public int population, match;
    public int marginalCost;
    public int[] initiatives;
    public boolean showPGMultiplier;
    public String contributionsString;
    public int agentRefreshMillis = 100;
    public Map<String, Float> paramMap;
    public Map<String, Float[]> paramArrayMap;
    public Agent agent;

    /**
     * Constructor
     */
    public Config() {
        diceRoll=false;
        turnBased=false;
        paid = true;
        length = 120;
        percentChangePerSecond = Float.NaN;
        subperiods = 0;
        preLength = 0;
        yourPayoff = new Line();
        yourPayoff.r = 50;
        yourPayoff.g = 50;
        yourPayoff.b = 50;
        yourPayoff.alpha = 100;
        yourPayoff.weight = 2f;
        yourPayoff.visible = true;
        yourPayoff.mode = Line.Mode.Shaded;
        matchPayoff = new Line();
        matchPayoff.r = 0;
        matchPayoff.g = 0;
        matchPayoff.b = 0;
        matchPayoff.alpha = 255;
        matchPayoff.weight = 2f;
        matchPayoff.visible = true;
        matchPayoff.mode = Line.Mode.Solid;
        yourStrategy = new Line();
        yourStrategy.visible = true;
        yourStrategy.mode = Line.Mode.Solid;
        yourStrategy.r = yourPayoff.r;
        yourStrategy.g = yourPayoff.g;
        yourStrategy.b = yourPayoff.b;
        yourStrategy.alpha = 100;
        yourStrategy.weight = 2f;
        matchStrategy = new Line();
        matchStrategy.visible = true;
        matchStrategy.mode = Line.Mode.Solid;
        matchStrategy.r = matchPayoff.r;
        matchStrategy.g = matchPayoff.g;
        matchStrategy.b = matchPayoff.b;
        matchStrategy.alpha = 255;
        matchStrategy.weight = 2f;
        thresholdLine = new Line();
        thresholdLine.mode = Line.Mode.Dashed;
        thresholdLine.r = 255;
        thresholdLine.g = 170;
        thresholdLine.b = 0;
        thresholdLine.alpha = 255;
        thresholdLine.weight = 2f;
        rLabel = "A";
        pLabel = "B";
        sLabel = "C";
        shortRLabel = "A";
        shortPLabel = "B";
        shortSLabel = "C";
        rColor = new Color(255, 255, 255);
        pColor = new Color(0, 0, 0);
        sColor = new Color(150, 150, 150);
        showRPSSliders = false;
        showHeatmapLegend = false;
        chatroom = false;
        negativePayoffs = false;
        sigmoidHeatmap = false;
        showHeatmap = true;
        sigmoidAlpha = 0.5f;
        showPayoffTimeAxisLabels = false;
        showMatrix = true;
        excludeSelf = false;
        groupSize = -1;
        numGroups = -1;
        population = -1;
        match = -1;
        marginalCost = 0;
        probPayoffs = false;
        trajectory = false;
        matchType = MatchGroup.self;
        menu = new ChatMenu();
        menu.m1 = "go left";
        menu.m2 = "go right";
        menu.m3 = "stay still";
        menu.m4 = "ok";
        menu.m5 = aliases[0];
        menu.m6 = aliases[1];
        menu.m7 = aliases[2];
        menu.m8 = aliases[3];
        //menu.m9 = aliases[4];
        //menu.m10 = aliases[5];
        //menu.m11 = aliases[6];
        //menu.m12 = aliases[7];
        grid = Float.NaN;
        showPGMultiplier = false;
        infoDelay = 0;
        agent = new Agent();
        paramMap = new HashMap<String, Float>();
        paramArrayMap = new HashMap<String, Float[]>();
    }
    
    /**
     * Returns a float value for an arbitrary field
     * @param key the name of the csv field to get the value of
     * @return the value of the csv field with the name of the passed key for
     * this period.
     */
    public float get(String key) {
        
        if (paramMap.containsKey(key)) {
            return paramMap.get(key);
        }
        return Float.NaN;
    }
    
    /**
     * Returns an array for an arbitrary csv field
     * @param key the name of the csv field
     * @return an array for the csv field passed
     */
    public Float[] getArray(String key) {
        if (paramArrayMap.containsKey(key)) {
            return paramArrayMap.get(key);
        }
        return null;
    }
    
    public void generateRevealedPoints(Random random) {
        if (!Float.isNaN(revealLambda)) {
            revealTimes = "\"";
            int time = 0;
            do {
                revealTimes += time + ",";
                double L = Math.exp(-revealLambda);
                int k = 0;
                double p = 1;
                do {
                    k++;
                    p = p * random.nextFloat();
                } while (p > L);
                time += k - 1;
            } while (time < length);
            revealTimes += length + "\"";
        }
    }
    
    /**
     * Returns the color for the client with the passed id.  Used to color chatroom
     * messages.
     * @param id the id of a client.
     * @return 
     */
    public Color getColor(int id) {
        if (chatroom) {
            return currColors.get(id);
        }
        if (id == FIRE.client.getID()) {
            return colors[0];
        }
        return colors[1];
    }
    
    /**
     * An array containing the names of the preset colors
     */
    public static String[] aliases = new String[]{
        "Green", "Red", "Blue", "Orange", "Purple", "Gray", "Aqua", "Yellow"
    };
    
    /**
     * An array of  preset colors
     */
    public static Color[] colors = new Color[]{
        new Color(0x1FCB1A), // green
        new Color(0xF74018), // red
        new Color(0x587CFF), // blue
        new Color(0xFF8B00), // orange
        new Color(0xA646E0), // purple
        new Color(0xA8A8A8), // gray
        new Color(0x5DE6D7), // aqua
        new Color(0xF6FF00), // yellow
    };
    // assigned by the server in configurePeriod
    public Map<Integer, String> currAliases;
    public Map<Integer, Color> currColors;

    @Override
    /**
     * Attempts to configure the payoff function.  Exception is thrown is the case
     * of an error occurring when a scripted payoff function fails to configure and 
     * compile.
     */
    public void test() throws ConfigException {
        payoffFunction.configure(this);
    }
}
