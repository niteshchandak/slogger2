/*
 * FilePublisher.java
 *
 * Created on April 8, 2006, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.slogger.publisher;


import java.util.Date;
import java.util.Properties;
import org.slogger.ISyslogMessage;
import org.slogger.ISyslogPublisher;
import java.net.*;
import java.io.*;


/**
 *
 * @author preetham
 */
public class FilePublisher implements ISyslogPublisher{
    
    public static String FILE_NAME_PROPERTY = "FilePublisher.file";
    
    /** Creates a new instance of FilePublisher */
    public FilePublisher() {
    }

    public void initialize(Properties properties) throws Exception {
        initialized = true;
        
        String fileName = properties.getProperty(FILE_NAME_PROPERTY);
        fos = new FileOutputStream(fileName);
        
    }

    public void publishSyslog(ISyslogMessage syslog) throws Exception {
        if (!initialized)
            throw new RuntimeException("FilePublisher not initialized");

        Date date = new Date();        
        String message = date + " " + syslog.getFacility() + "." 
                + syslog.getLevel() + "   " + syslog.getSourceHost() 
                + " :" + syslog.getMessage() + "\n";
        fos.write(message.getBytes());
    }

    public void shutDown() throws Exception  {
        fos.close();
    }
    
    
    private boolean initialized = false;
    private FileOutputStream fos = null;
    
}
 