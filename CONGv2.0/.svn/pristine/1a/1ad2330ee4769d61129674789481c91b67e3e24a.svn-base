/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/

package edu.ucsc.leeps.fire.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jpettit
 */
public class Startup {

    public static void main(String[] args, int clients) {
        String libPath = "lib";
        String classPath = System.getProperty("java.class.path");
        String className = "";
        try {

            Class callingClass = Class.forName(new Throwable().getStackTrace()[1].getClassName());
            className += callingClass.getPackage().getName();

            List<Process> children = new LinkedList<Process>();
            Process server = start("server", libPath, classPath, className + ".server.Server");
            children.add(server);
            Thread.sleep(500);
            for (int i = 0; i < clients; i++) {
                Process client = start("client_" + (i + 1), libPath, classPath, className + ".client.Client");
                children.add(client);
                Thread.sleep(200);
            }
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(children));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Process start(String id, String libPath, String classPath, String className) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(new String[]{"java", "-Dfire.debug", "-Dfire.id=" + id, "-Djava.library.path=" + libPath, "-cp", classPath, "-ea", className});
        new OutputRedirectThread(p.getErrorStream()).start();
        return p;
    }

    public static class OutputRedirectThread extends Thread {

        private InputStream is;

        public OutputRedirectThread(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while (line != null) {
                    System.err.println(line);
                    line = reader.readLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class ShutdownThread extends Thread {

        private List<Process> processes;

        public ShutdownThread(List<Process> processes) {
            this.processes = processes;
        }

        @Override
        public void run() {
            for (Process p : processes) {
                p.destroy();
            }
        }
    }
}
