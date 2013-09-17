/**
 * Copyright (c) 2012, University of California All rights reserved.
 *
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 *
 */
package edu.ucsc.leeps.fire.cong.server;

import compiler.CharSequenceCompiler;
import compiler.CharSequenceCompilerException;
import edu.ucsc.leeps.fire.config.BaseConfig;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.client.Client;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.logging.LogEvent;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * This class is used to hold scripted payoff functions that allow cong's client
 * side functionality to be replaced by a exterior .java script file.  Payoff scripts
 * implement the payoff script interface which is defined in this class.
 * @author jpettit
 * @see edu.ucsc.leeps.fire.cong.server.PayoffFunction;
 */
public class ScriptedPayoffFunction implements PayoffFunction, Serializable {

    public String name;
    public String source;
    public float min, max;
    public int strategies;
    private String scriptText;
    private transient static final Map<String, Class<PayoffScriptInterface>> codeCache = new HashMap<String, Class<PayoffScriptInterface>>();
    private transient PayoffScriptInterface function;
    private transient DiagnosticCollector<JavaFileObject> errs;
    
    /**
     * Constructor.
     */
    public ScriptedPayoffFunction() {
        min = Float.NaN;
        max = Float.NaN;
    }
    
    public float getMin() {
        if (Float.isNaN(min) && function != null) {
            return function.getMin();
        }
        return min;
    }

    public float getMax() {
        if (Float.isNaN(max) && function != null) {
            return function.getMax();
        }
        return max;
    }

    public int getNumStrategies() {
        return strategies;
    }
    
    /**
     * Returns a bonus of 0
     * @param subperiod
     * @param config
     * @return 0
     */
    public float getSubperiodBonus(int subperiod, Config config) {
        return 0;
    }

    public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config) {
        if (function == null) {
            try {
                configure(config);
            } catch (BaseConfig.ConfigException ex) {
                return Float.NaN;
            }
        }
        return function.getPayoff(id, percent, popStrategies, matchPopStrategies, config);
    }
    
    /**
     * Draw the client gui using the code in the script file
     * @param a the client instance to draw to
     */
    public void draw(Client a) {
        if (function != null) {
            try {
                function.draw(a);
            } catch (Exception ex) {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                ex.printStackTrace(printWriter);
                String exString = writer.toString();
                a.text(exString, 20, 30);
            }
        }
    }
    
    /**
     * Returns the log event class from the client
     * @param config
     * @return 
     */
    public Class<? extends LogEvent> getLogEventClass(Config config) {
        if (function == null) {
            try {
                configure(config);
            } catch (BaseConfig.ConfigException ex) {
                return null;
            }
        }
        return function.getLogEventClass();
    }
    
    /**
     * Returns null
     * @param id
     * @param percent
     * @param popStrategies
     * @param matchPopStrategies
     * @return null
     */
    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }
    
    /**
     * Returns null
     * @param id
     * @param percent
     * @param popStrategies
     * @param matchPopStrategies
     * @return null 
     */
    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }
    
    /**
     * Configures this scripted payoff function.  Attempts to read and compile the code
     * in the file specified in the config.
     * @param config
     * @throws edu.ucsc.leeps.fire.config.BaseConfig.ConfigException when compilation 
     * fails, the script file can't be found, or an IOException is thrown while
     * reading the file.
     */
    public void configure(Config config) throws BaseConfig.ConfigException {
        if (codeCache.containsKey(scriptText)) {
            try {
                function = codeCache.get(scriptText).newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (scriptText == null) {
            File baseDir = new File(FIRE.server.getConfigSource()).getParentFile();
            File scriptFile = new File(baseDir, source);
            if (!scriptFile.exists()) {
                throw new BaseConfig.ConfigException("Cannot find payoff script file %s", scriptFile.getAbsolutePath());
            }
            scriptText = "";
            try {
                FileReader reader = new FileReader(scriptFile);
                int c = reader.read();
                while (c != -1) {
                    scriptText += (char) c;
                    c = reader.read();
                }
            } catch (IOException ex) {
                throw new BaseConfig.ConfigException("Error reading payoff script");
            }
        }
        CharSequenceCompiler<PayoffScriptInterface> compiler = new CharSequenceCompiler<PayoffScriptInterface>(
                getClass().getClassLoader(), Arrays.asList(new String[]{"-target", "1.5"}));
        errs = new DiagnosticCollector<JavaFileObject>();
        Class<PayoffScriptInterface> clazz = null;
        Pattern classNamePattern = Pattern.compile("public class (.*?) implements PayoffScriptInterface");
        Matcher m = classNamePattern.matcher(scriptText);
        if (!m.find() || m.groupCount() != 1) {
            throw new BaseConfig.ConfigException("Error compiling payoff script: Failed to find class name");
        }
        try {
            clazz = compiler.compile(m.group(1), scriptText, errs, new Class<?>[]{PayoffScriptInterface.class});
        } catch (CharSequenceCompilerException ex) {
            final Writer result = new StringWriter();
            for (Diagnostic d : errs.getDiagnostics()) {
                try {
                    result.append(String.format("Line %d\nCol %d\n%s", d.getLineNumber(), d.getColumnNumber(), d.getMessage(null)));
                } catch (IOException ignore) {
                }
            }
            throw new BaseConfig.ConfigException("Error compiling payoff script:\n%s", result.toString());
        } catch (ClassCastException ex) {
            throw new BaseConfig.ConfigException("Error compiling payoff script:\n%s", ex.toString());
        }
        if (clazz != null) {
            try {
                function = clazz.newInstance();
                codeCache.put(scriptText, clazz);
            } catch (InstantiationException ex) {
                throw new BaseConfig.ConfigException("Error compiling payoff script:\n%s", ex.toString());
            } catch (IllegalAccessException ex) {
                throw new BaseConfig.ConfigException("Error compiling payoff script:\n%s", ex.toString());
            }
        }
    }
    
    /**
     * This interface is used by exterior .java script files to extend cong, providing
     * a payoff function and replacing the draw function.
     */
    public static interface PayoffScriptInterface {
        /**
         * Returns the payoff the the client whose id is passed
         * @param id id of client whose payoff is to be returned
         * @param percent -1 if homotopy is specified, otherwise the amount of time to calculate the payoff for
         * @param popStrategies the strategies for this client's group
         * @param matchPopStrategies the match strategies for this client's group
         * @param config the current config
         * @return 
         */
        public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config);
        
        /**
         * Returns the minimum payoff
         * @return the minimum payoff
         */
        public float getMin();
        
        /**
         * Returns the maximum payoff. 
         * @return the maximum payoff
         */
        public float getMax();
        
        /**
         * Draw function for the client gui.  
         * @param a the current client instance
         */
        public void draw(Client a);
        
        /**
         * Returns the class object for a custom log event type.
         * @return the class object for a custom log event type
         */
        public Class<? extends LogEvent> getLogEventClass();
    }
}
