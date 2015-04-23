/*
 * RmpMessage.java
 *
 * Created on April 8, 2006, 6:38 PM
 * 
 */

package org.slogger.provider.randomMessageProvider;

import org.slogger.*;

/**
 *
 * @author preetham
 */
public class RmpMessage {
    
    /** Creates a new instance of RmpMessage */
    public RmpMessage( IValueProvider message, 
            IValueProvider facility, IValueProvider level,
            IValueProvider sourceHost,IValueProvider sourcePort,
            IValueProvider targetHost, IValueProvider targetPort,
            IValueProvider timeStamp) {
        this.message = message;
        this.facility = facility;
        this.level     = level;
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.timeStamp = timeStamp;
    }
    
    public String toString() {
        String str = message.getValue() + "/" + facility.getValue() + "/" +
                level.getValue() + "/" + sourceHost.getValue() + "/" +
                sourcePort.getValue() + "/" + targetHost.getValue() + "/" +
                targetPort.getValue() + "/" + timeStamp.getValue();
        return str;
         
    }
    
    public ISyslogMessage getSyslogMessage(boolean useEmblem) {
        String mssgText = message.getValue().toString();
        if (useEmblem) {
            mssgText = getEmblemMessage(facility.getValue().toString(), 
                    level.getValue().toString(), mssgText);
        }
        SyslogMessage ret = new SyslogMessage(mssgText, 
                facility.getValue().toString(), level.getValue().toString());
        
        ret.setSourceHost(sourceHost.getValue().toString());
        ret.setSourcePort(sourcePort.getValue().toString());
        ret.setTargetHost(targetHost.getValue().toString());
        ret.setTargetPort(targetPort.getValue().toString());
        ret.setTimeStamp(timeStamp.getValue().toString());
        
        return ret;
    }
    
    public ISyslogMessage getSyslogMessage() {        
        return getSyslogMessage(false);
    }
    
    
    /** returns an emblem format of the message. Since Mnemonic 
     * is not part of the standard, this is not added to the message 
     * and is considered as part of the message text
     */
    public static String getEmblemMessage(String fac, String lev, String mssg) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('%');
        buffer.append(fac);
        buffer.append('-');
        buffer.append(lev);
        buffer.append('-');
        buffer.append(mssg);
        return buffer.toString();
    }
    
    //Data members
    private IValueProvider message;
    private IValueProvider facility;
    
    private IValueProvider level;
    
    private IValueProvider sourceHost;
    private IValueProvider sourcePort;
    private IValueProvider targetHost;
    private IValueProvider targetPort;
    private IValueProvider timeStamp;
    
    
    
    
}
