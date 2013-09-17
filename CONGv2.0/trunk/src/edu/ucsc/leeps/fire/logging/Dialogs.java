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
 * This is a class containing a variety of static methods for gui dialougs
 * @author jpettit
 */
public class Dialogs {
    
    /**
     * Creates a pop up dialogue with the passed error message on it
     * @param err an error message
     */
    public static void popUpErr(String err) {
        JOptionPane.showMessageDialog(null, err.toString());
    }
    
    /**
     * Creates a pop up message with the error string and the stack trace of the
     * passed exception.
     * @param err an error message
     * @param ex an exception
     */
    public static void popUpErr(String err, Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, err + "\n" + errString.toString());
    }
    
    /**
     * Creates a pop up message with the stack trace of the passed exception
     * @param ex an exception
     */
    public static void popUpErr(Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, errString.toString(), ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Creates a pop up message with the pass error message and closes the program
     * @param err an error message
     */
    public static void popUpAndExit(String err) {
        JOptionPane.showMessageDialog(null, err);
        System.exit(1);
    }
    
    /**
     * Creates an pop up message with the stack trace of the passed exception
     * and then closes the program
     * @param ex an exception
     */
    public static void popUpAndExit(Exception ex) {
        Writer errString = new StringWriter();
        PrintWriter printWriter = new PrintWriter(errString);
        ex.printStackTrace(printWriter);
        JOptionPane.showMessageDialog(null, errString.toString());
        System.exit(1);
    }
}
