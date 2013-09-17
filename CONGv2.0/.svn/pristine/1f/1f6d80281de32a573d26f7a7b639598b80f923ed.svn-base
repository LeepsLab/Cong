/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.JOptionPane;

/**
 *
 * @author jpettit
 */
public class Dialogs {

    public static void popUpErr(String err) {
        JOptionPane.showMessageDialog(null, err.toString());
    }

    public static void popUpErr(String err, Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, err + "\n" + errString.toString());
    }

    public static void popUpErr(Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, errString.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
    }
    
    public static void popUpAndExit(String err) {
        JOptionPane.showMessageDialog(null, err);
        System.exit(1);
    }

    public static void popUpAndExit(Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, errString.toString());
        System.exit(1);
    }
}
