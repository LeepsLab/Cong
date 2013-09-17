/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsc.leeps.fire.cong.config;

import edu.ucsc.leeps.fire.cong.client.gui.Line;
import edu.ucsc.leeps.fire.cong.server.PayoffFunction;
import edu.ucsc.leeps.fire.cong.server.Population;
import edu.ucsc.leeps.fire.cong.server.ThreeStrategyPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;
import edu.ucsc.leeps.fire.cong.server.UltimatumPayoffFunction;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.cong.server.ThresholdPayoffFunction;
import java.awt.Color;

/**
 *
 * @author jpettit
 */
public class Config extends BaseConfig {

    public boolean pointsPerSecond;
    public float percentChangePerSecond;
    public PayoffFunction payoffFunction;
    public PayoffFunction counterpartPayoffFunction;
    public int numTuples;
    public int tupleSize;
    public boolean assignedTuples;
    public boolean excludeSelf;
    public boolean mixedStrategySelection;
    public boolean stripStrategySelection;
    public int subperiods;
    public StrategySelectionDisplayType strategySelectionDisplayType;
    public Line payoffAa, payoffAb, payoffBa, payoffBb, yourPayoff, otherPayoff;
    public Line yourStrategyOverTime, counterpartStrategyOverTime;
    public Line thresholdLine;
    public String rLabel, pLabel, sLabel, shortRLabel, shortPLabel, shortSLabel;
    public Color rColor, pColor, sColor;
    public boolean showRPSSliders;
    public ShockZone shock;
    public DecisionDelay initialDelay, delay;
    public float impulse;
    public float changeCost;
    public boolean showHeatmapLegend;
    public boolean chatroom;
    public boolean negativePayoffs;
    public boolean sigmoidHeatmap;
    public float sigmoidAlpha;
    public float sigmoidBeta;
    public boolean showMatrix;
    public boolean showPayoffTimeAxisLabels;
    public static final Class homotopy = TwoStrategyPayoffFunction.class;
    public static final Class bimatrix = TwoStrategyPayoffFunction.class;
    public static final Class rps = ThreeStrategyPayoffFunction.class;
    public static final Class ultimatum = UltimatumPayoffFunction.class;
    public static final Class paired = Population.class;
    public static final Class line = Line.class;
    public static final Class threshold = ThresholdPayoffFunction.class;
    public static final Class shockZone = ShockZone.class;
    public static final Class decisionDelay = DecisionDelay.class;
    // per-client
    public float[] initialStrategy;
    public int matchID;
    public boolean isCounterpart;
    public int playersInTuple;
    public int population, match;

    public Config() {
        timeConstrained = true;
        paid = true;
        length = 120;
        pointsPerSecond = false;
        percentChangePerSecond = 0.1f;
        changeCost = 0;
        subperiods = 0;
        preLength = 0;
        strategySelectionDisplayType = StrategySelectionDisplayType.Matrix;
        mixedStrategySelection = true;
        stripStrategySelection = false;
        yourPayoff = new Line();
        yourPayoff.r = 50;
        yourPayoff.g = 50;
        yourPayoff.b = 50;
        yourPayoff.alpha = 100;
        yourPayoff.weight = 2f;
        yourPayoff.visible = true;
        yourPayoff.mode = Line.Mode.Shaded;
        otherPayoff = new Line();
        otherPayoff.r = 0;
        otherPayoff.g = 0;
        otherPayoff.b = 0;
        otherPayoff.alpha = 255;
        otherPayoff.weight = 2f;
        otherPayoff.visible = true;
        otherPayoff.mode = Line.Mode.Solid;
        yourStrategyOverTime = new Line();
        yourStrategyOverTime.visible = true;
        yourStrategyOverTime.mode = Line.Mode.Solid;
        yourStrategyOverTime.r = yourPayoff.r;
        yourStrategyOverTime.g = yourPayoff.g;
        yourStrategyOverTime.b = yourPayoff.b;
        yourStrategyOverTime.alpha = 100;
        yourStrategyOverTime.weight = 2f;
        counterpartStrategyOverTime = new Line();
        counterpartStrategyOverTime.visible = true;
        counterpartStrategyOverTime.mode = Line.Mode.Solid;
        counterpartStrategyOverTime.r = otherPayoff.r;
        counterpartStrategyOverTime.g = otherPayoff.g;
        counterpartStrategyOverTime.b = otherPayoff.b;
        counterpartStrategyOverTime.alpha = 255;
        counterpartStrategyOverTime.weight = 2f;
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
        shock = new ShockZone();
        shock.start = 0f;
        shock.end = 0f;
        shock.backfill = false;
        impulse = 0f;
        showHeatmapLegend = true;
        chatroom = false;
        negativePayoffs = false;
        sigmoidHeatmap = false;
        sigmoidAlpha = 0.5f;
        showPayoffTimeAxisLabels = false;
        excludeSelf = false;
        tupleSize = -1;
        numTuples = -1;
        population = -1;
        match = -1;
    }
}
