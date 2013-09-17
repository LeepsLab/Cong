/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.cong;

import edu.ucsc.leeps.fire.testing.SessionTestDialog;
import edu.ucsc.leeps.fire.testing.Startup;

/**
 *
 * @author jpettit
 */
public class Main {

    public static void main(String[] args) {
        SessionTestDialog dialog = new SessionTestDialog(null, true);
        dialog.setVisible(true);
        if (dialog.accepted) {
            try {
                int numSubjects = Integer.parseInt(dialog.numSubjectsInput.getValue().toString());
                System.setProperty("useOpenGL", String.valueOf(dialog.useOpenGLCheckbox.isSelected()));
                if (numSubjects > 0) {
                    Startup.main(args, numSubjects);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                System.err.println("Illegal number of subjects");
                System.exit(1);
            }
        }
    }
}
