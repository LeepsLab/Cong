package edu.ucsc.leeps.fire.cong.server;

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
import java.util.List;
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
    private transient PayoffScriptInterface function;
    private transient DiagnosticCollector<JavaFileObject> errs;

    public float getMin() {
        return min;
    }

    public float getMax() {
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
            configure(config);
        }
        return function.getPayoff(id, percent, popStrategies, matchPopStrategies, config);
    }

    public float[] getPopStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }

    public float[] getMatchStrategySummary(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies) {
        return null;
    }

    public void configure(Config config) {
        if (scriptText == null) {
            File baseDir = new File(FIRE.server.getConfigSource()).getParentFile();
            File scriptFile = new File(baseDir, source);
            if (!scriptFile.exists()) {
                Dialogs.popUpErr("Error: Payoff script referenced in config does not exist.\n" + scriptFile.getAbsolutePath());
                return;
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
                Dialogs.popUpErr("Error reading payoff script.", ex);
            }
        }
        CharSequenceCompiler<PayoffScriptInterface> compiler = new CharSequenceCompiler<PayoffScriptInterface>(
                getClass().getClassLoader(), Arrays.asList(new String[]{"-target", "1.5"}));
        errs = new DiagnosticCollector<JavaFileObject>();
        Class<PayoffScriptInterface> clazz = null;
        Pattern classNamePattern = Pattern.compile("public class (.*?) implements PayoffScriptInterface");
        Matcher m = classNamePattern.matcher(scriptText);
        if (!m.find() || m.groupCount() != 1) {
            Dialogs.popUpErr("Failed to find class name");
            return;
        }
        try {
            clazz = compiler.compile(m.group(1), scriptText, errs, new Class<?>[]{PayoffScriptInterface.class});
        } catch (ClassCastException ex1) {
            Dialogs.popUpErr(ex1);
        } catch (CharSequenceCompilerException ex2) {
            Dialogs.popUpErr(ex2);
        }
        if (clazz != null) {
            try {
                function = clazz.newInstance();
            } catch (InstantiationException ex1) {
                Dialogs.popUpErr(ex1);
            } catch (IllegalAccessException ex2) {
                Dialogs.popUpErr(ex2);
            }
        }
    }

    public List<Diagnostic<? extends JavaFileObject>> setScript(String scriptText) {
        this.scriptText = scriptText;
        configure(null);
        return errs.getDiagnostics();
    }

    public static interface PayoffScriptInterface {

        public float getPayoff(int id, float percent, Map<Integer, float[]> popStrategies, Map<Integer, float[]> matchPopStrategies, Config config);
    }
}
