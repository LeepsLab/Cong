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
 *
 * @author jpettit
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

    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }

    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }

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

    public static interface PayoffScriptInterface {

        public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config);

        public float getMin();

        public float getMax();

        public void draw(Client a);

        public Class<? extends LogEvent> getLogEventClass();
    }
}
