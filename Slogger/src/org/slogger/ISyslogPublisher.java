/*
 * ISyslogPublisher.java
 *
 * Created on April 8, 2006, 5:32 PM
 *
 *
 */

package org.slogger;

import java.util.Properties;

/**
 * Interface for all publishers of syslog message
 * @author preetham
 */
public interface ISyslogPublisher {
    
    public void initialize(Properties properties) throws Exception ;
    
    public void publishSyslog(ISyslogMessage message) throws Exception;
    
    public void shutDown() throws Exception;
    
}
