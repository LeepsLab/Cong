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
 * This class takes log events and writes the to the log file asynchronously.  Each
 * type of log event gets its own writer thread and log file.
 * @author jpettit
 */
public class Logger {

    private Map<String, BufferedWriter> logs = new HashMap<String, BufferedWriter>();
    private AsyncWriter writer = new AsyncWriter();
    
    /**
     * Constructor.  Starts a new thread to write the log event to file.
     */
    public Logger() {
        writer.start();
    }
    
    /**
     * Adds a new event to the loggers event queue
     * @param event 
     */
    public void commit(LogEvent event) {
        writer.queue.addFirst(event);
    }
    
    /**
     * Prints an error message if there are events still being written to the disk.
     */
    public void endPeriod() {
        if (!writer.queue.isEmpty()) {
            System.err.println(String.format("WARNING: %d log events being written to disk", writer.queue.size()));
        }
    }
    
    /**
     * Retrurns the size of the loggers event queue or the number of events that
     * have yet to be written to the log file.
     * @return the size of the loggers event queue
     */
    public int queueSize() {
        return writer.queue.size();
    }
    
    /**
     * Sets the queue's action listener.  A new action event with a reference to 
     * the logger's writer's LinkedBlockingDeque is sent each time a file is written 
     * @param listener the action listener to attack to the logger's queue
     */
    public void setQueueListener(ActionListener listener) {
        writer.listener = listener;
    }
    
    /**
     * Gets the working directory for cong which is the folder that cong's source
     * folder is in rather that the folder with the executable.
     * @return cong's working directory
     */
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
    
    /**
     * This threads handles writing log event asynchronously.
     */
    private class AsyncWriter extends Thread {

        private LinkedBlockingDeque<LogEvent> queue = new LinkedBlockingDeque<LogEvent>();
        private ActionListener listener;

        @Override
        /**
         * Writes all the events in the writer's blocking queue as fast as it can
         */
        public void run() {
            while (true) {
                try {
                    write(queue.takeFirst());
                } catch (InterruptedException ex) {
                }
            }
        }
        
        /**
         * Writes a log event to file.
         * @param event the event to write.
         */
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
                if (!logs.containsKey(className)) { //creates a new log file if one does not already exist
                    File logFile = new File(fileName);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
                    logs.put(className, writer);
                    ObjectMapper.writeHeader(event, writer, event.getDelimiter());
                }
                BufferedWriter writer = logs.get(getCurrentWorkingDir() + className);
                ObjectMapper.write(event, writer, event.getDelimiter());//write the log event
                if (listener != null) {
                    listener.actionPerformed(new ActionEvent(this.queue, 0, null));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
