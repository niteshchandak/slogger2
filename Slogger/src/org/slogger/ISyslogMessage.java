/*
 * ISyslogMessage.java
 *
 * Created on April 8, 2006, 5:24 PM
 *
 * Interface to represent a Syslog Message
 */

package org.slogger;

/**
 *
 * @author preetham
 */
public interface ISyslogMessage {
    
    public String getMessage() ;

    public void setMessage(String message) ;
    
    public String getFacility() ;

    public void setFacility(String facility) ;

    public String getLevel() ;

    public void setLevel(String level) ;
    
    public String getSourceHost() ;

    public void setSourceHost(String sourceHost);

    public String getSourcePort() ;

    public void setSourcePort(String sourcePort);

    public String getTargetHost();
    
    public void setTargetHost(String targetHost) ;

    public String getTargetPort() ;

    public void setTargetPort(String targetPort);

    public String getTimeStamp() ;

    public void setTimeStamp(String timeStamp) ;
    
    
}
