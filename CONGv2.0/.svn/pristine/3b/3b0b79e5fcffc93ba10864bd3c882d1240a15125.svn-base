/**
 * Copyright (c) 2012, University of California All rights reserved.
 *
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 *
 */
package edu.ucsc.leeps.fire.cong.client;

import compiler.CharSequenceCompiler;
import compiler.CharSequenceCompilerException;
import edu.ucsc.leeps.fire.cong.FIRE;
import edu.ucsc.leeps.fire.cong.config.Config;
import edu.ucsc.leeps.fire.logging.Dialogs;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jpettit
 */
public class Agent extends Thread implements Serializable {

    public transient volatile boolean running;
    public transient volatile boolean paused;
    public String agentText;
    private transient static final Map<String, Class<AgentScriptInterface>> codeCache = new HashMap<String, Class<AgentScriptInterface>>();
    private transient AgentScriptInterface function;

    public Agent() {
        paused = false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            Config config = FIRE.client.getConfig();
            if (!paused && FIRE.client.isRunningPeriod() && Client.state != null && Client.state.target != null && config != null) {
                if (function != null) {
                    Client.state.setTarget(function.act(config), config);
                } else if (config.agentSource != null) {
                    configure(config);
                }
            }
            try {
                if (config != null) {
                    Thread.sleep(config.agentRefreshMillis);
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public DiagnosticCollector<JavaFileObject> configure(Config config) {
        DiagnosticCollector<JavaFileObject> errs = null;
        if (config.agentSource == null) {
            return errs;
        }
        if (agentText != null) {
            if (codeCache.containsKey(agentText)) {
                try {
                    function = codeCache.get(agentText).newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return errs;
            }
        }
        if (agentText == null) {
            File baseDir = new File(FIRE.server.getConfigSource()).getParentFile();
            File scriptFile = new File(baseDir, config.agentSource);
            if (!scriptFile.exists()) {
                Dialogs.popUpErr("Error: Payoff script referenced in config does not exist.\n" + scriptFile.getAbsolutePath());
                return errs;
            }
            agentText = "";
            try {
                FileReader reader = new FileReader(scriptFile);
                int c = reader.read();
                while (c != -1) {
                    agentText += (char) c;
                    c = reader.read();
                }
            } catch (IOException ex) {
                Dialogs.popUpErr("Error reading payoff script.", ex);
            }
        }

        CharSequenceCompiler<AgentScriptInterface> compiler = new CharSequenceCompiler<AgentScriptInterface>(
                System.class.getClassLoader(), Arrays.asList(new String[]{"-target", "1.5"}));
        errs = new DiagnosticCollector<JavaFileObject>();
        Class<AgentScriptInterface> clazz = null;
        Pattern classNamePattern = Pattern.compile("public class (.*?) implements AgentScriptInterface");
        Matcher m = classNamePattern.matcher(agentText);
        if (!m.find() || m.groupCount() != 1) {
            Dialogs.popUpErr("Failed to find class name");
            return errs;
        }
        try {
            clazz = compiler.compile(m.group(1), agentText, errs, new Class<?>[]{AgentScriptInterface.class});
        } catch (ClassCastException ex1) {
            Dialogs.popUpErr(ex1);
        } catch (CharSequenceCompilerException ex2) {
            Dialogs.popUpErr(ex2);
        }
        if (clazz != null) {
            try {
                function = clazz.newInstance();
                codeCache.put(agentText, clazz);
            } catch (InstantiationException ex1) {
                Dialogs.popUpErr(ex1);
            } catch (IllegalAccessException ex2) {
                Dialogs.popUpErr(ex2);
            }
        }
        return errs;
    }

    public static interface AgentScriptInterface {

        public float[] act(Config config);
    }
}
