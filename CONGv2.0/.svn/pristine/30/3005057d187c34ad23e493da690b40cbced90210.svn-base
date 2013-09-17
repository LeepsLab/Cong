/**
 * Copyright (c) 2012, University of California
 * All rights reserved.
 * 
 * Redistribution and use is governed by the LICENSE.txt file included with this
 * source code and available at http://leeps.ucsc.edu/cong/wiki/license
 **/
package edu.ucsc.leeps.fire.logging;

import edu.ucsc.leeps.fire.reflection.ObjectMapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author jpettit
 */
public class Logger {

    private Map<String, BufferedWriter> logs = new HashMap<String, BufferedWriter>();
    private AsyncWriter writer = new AsyncWriter();

    public Logger() {
        writer.start();
    }

    public void commit(LogEvent event) {
        writer.queue.addFirst(event);
    }

    public void endPeriod() {
        if (!writer.queue.isEmpty()) {
            System.err.println(String.format("WARNING: %d log events being written to disk", writer.queue.size()));
        }
    }

    public int queueSize() {
        return writer.queue.size();
    }

    public void setQueueListener(ActionListener listener) {
        writer.listener = listener;
    }

    public static String getCurrentWorkingDir() {
        URL location = Logger.class.getProtectionDomain().getCodeSource().getLocation();
        String[] components = location.getFile().split(File.pathSeparator);
        String path = "";
        for (int i = 0; i < components.length - 1; i++) {
            path += components[i];
            path += File.separator;
        }
        return path;
    }

    private class AsyncWriter extends Thread {

        private LinkedBlockingDeque<LogEvent> queue = new LinkedBlockingDeque<LogEvent>();
        private ActionListener listener;

        @Override
        public void run() {
            while (true) {
                try {
                    write(queue.takeFirst());
                } catch (InterruptedException ex) {
                }
            }
        }

        private void write(LogEvent event) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy.hh:mm.a");
                String className = event.getClass().getSimpleName();
                String fileName;
                if (className.contains("Event")) {
                    fileName = className.replace("Event", "s." + formatter.format(Calendar.getInstance().getTime()) + ".csv").replace(":", ".");
                } else {
                    fileName = (className + "s." + formatter.format(Calendar.getInstance().getTime()) + ".csv").replace(":", ".");
                }
                if (!logs.containsKey(className)) {
                    File logFile = new File(fileName);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
                    logs.put(className, writer);
                    ObjectMapper.writeHeader(event, writer, event.getDelimiter());
                }
                BufferedWriter writer = logs.get(getCurrentWorkingDir() + className);
                ObjectMapper.write(event, writer, event.getDelimiter());
                if (listener != null) {
                    listener.actionPerformed(new ActionEvent(this.queue, 0, null));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
