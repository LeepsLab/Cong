/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.config;

import java.io.Serializable;

/**
 *
 * @author jpettit
 */
public abstract class BaseConfig implements Serializable {

    public String period;
    public int subject;
    public boolean paid = true;
    public int length = 60;
    public boolean autostart = true;
    public int preLength = 0;

    public abstract void test() throws ConfigException;

    public static class ConfigException extends Exception {

        public ConfigException(String string) {
            super(string);
        }

        public ConfigException(String string, Object... os) {
            super(String.format(string, os));
        }
    }
}
