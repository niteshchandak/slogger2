/*
 * SyslogMessage.java
 *
 * Created on April 8, 2006, 5:25 PM
 * 
 */

package org.slogger;

/**
 *
 * @author preetham
 */
public class SyslogMessage implements ISyslogMessage {
    
    
    
    /** Creates a new instance of SyslogMessage */
    public SyslogMessage(String message, String facility, String level ) {
        this.message = message;
        this.level = level;
        this.facility = facility;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public String toString() {
        return message + "/" +
                facility + "/" +
                level + "/" +                
                sourceHost + "/" +
                sourcePort + "/" +
                targetHost + "/" +
                targetPort + "/" +
                timeStamp;
    }
    
    
        //Data members
    private String message;
    private String facility;
    private String level;
    
    private String sourceHost;
    private String sourcePort;
    private String targetHost;
    private String targetPort;
    private String timeStamp;
    

    
}
