/*
 * ISyslogProvider.java
 *
 * Created on April 8, 2006, 5:29 PM
 * 
 */

package org.slogger;

import java.util.Properties;

/**
 * Interface for providers of Syslog messages
 * @author preetham
 */

public interface ISyslogProvider {
    
    public void initialize(Properties properties) throws Exception ;
    
    public boolean hasNext();
    
    public ISyslogMessage getNextSyslog() throws Exception ;
    
    public void shutDown() throws Exception ; 
    
    
}
