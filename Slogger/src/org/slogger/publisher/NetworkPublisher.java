/*
 * NetworkPublisher.java
 *
 * Created on May 22, 2006, 9:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.slogger.publisher;

import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.slogger.ISyslogMessage;
import org.slogger.ISyslogPublisher;

/**
 * A primitive syslog client which sends out the messages on the
 * specified port to the specified host. 
 * It supports encapsulation of the source address as done by Kiwi 
 * @author preetham
 */
public class NetworkPublisher implements ISyslogPublisher{
    
    public static final String REMOTE_HOST = "NetworkPublisher.host";
    public static final String PORT = "NetworkPublisher.port";    
    public static final String ENCAP_STRING = "NetworkPublisher.sourceAddressEncapString";
    public static final String KEEP_COUNTERS = "NetworkPublisher.keepMessageCounters";
    
    /** Creates a new instance of NetworkPublisher */
    public NetworkPublisher() {
        messageCounters = new HashMap();
        
    }

    public void initialize(Properties properties) throws Exception {  
        try {
            address = InetAddress.getByName(properties.getProperty(
                    REMOTE_HOST, "localhost"));
            port = Integer.parseInt(properties.getProperty(PORT, "514"));
            System.out.println("Using port " + port);
            socket = new DatagramSocket();
            encapString = properties.getProperty(ENCAP_STRING, "");            
            keepMessageCounters = Boolean.valueOf(
                    properties.getProperty(KEEP_COUNTERS, "TRUE")).booleanValue();
            // init the 3rd party syslog client
            System.out.println("address=" + address.getHostName());
            this.syslogClient = new com.ice.syslog.Syslog(address.getHostName(), port,
                    "", /*com.ice.syslog.SyslogDefs.LOG_PERROR*/ 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void publishSyslog(ISyslogMessage message) throws Exception {

        String messageToSend = null;
        String rawMessage = message.getMessage();
            
        
        // Check if the source should be appended to the 
        // message (Kiwi style of forwarding mssgs and presvering
        // source address
        if (encapString.trim().equals("")) {
            messageToSend = rawMessage;
        } else {
            messageToSend = encapString + message.getSourceHost() + " " + 
                    rawMessage;
        }
        
        if (keepMessageCounters) {
            Integer i = (Integer)messageCounters.get(messageToSend);
            if (i == null)
                i = new Integer(0);            
            i = new Integer(i.intValue() + 1);
            messageCounters.put(messageToSend, i);            
        }
        System.out.println(">>" + messageToSend);
        syslogClient.syslog(22, Integer.valueOf(message.getLevel()).intValue(), messageToSend );
    }
    
    public void publishSyslogOld(ISyslogMessage message) throws Exception {
        
        String messageToSend = null;
        String rawMessage = message.getMessage();
            
        
        // Check if the source should be appended to the 
        // message (Kiwi style of forwarding mssgs and presvering
        // source address
        if (encapString.equals("")) {
            messageToSend = rawMessage;
        } else {
            messageToSend = encapString + message.getSourceHost() + " " + 
                    rawMessage;
        }
        
        if (keepMessageCounters) {
            Integer i = (Integer)messageCounters.get(messageToSend);
            if (i == null)
                i = new Integer(0);            
            i = new Integer(i.intValue() + 1);
            messageCounters.put(messageToSend, i);            
        }
           
        String senderSource = message.getSourceHost();
        
        
        int msgLen = messageToSend.length();
        
        byte[] messageBytes = new byte[msgLen];
        
        messageToSend.getBytes(0,msgLen, messageBytes,0);
        //System.out.println(">" + messageToSend);
        
        DatagramPacket packet =
                new DatagramPacket(messageBytes, msgLen, address, port);
        
        socket.send(packet);
        
    }
    
    
    public void shutDown() throws Exception {        
        socket.close();
        
        if (keepMessageCounters) {
            System.out.println("Message Stats");
            Iterator it = messageCounters.keySet().iterator();
            while (it.hasNext()) {
                Object mssg = it.next();
                Object counter = messageCounters.get(mssg);
     //           System.out.println(mssg + " [" + counter + "]");
                
            }
        }
        
    }
    
    
    private InetAddress address = null; 
    private int port = 514;
    private boolean initialized = false; 
    private DatagramSocket socket = null;
    // This is used to encapsulate the senders address
    private String encapString = null; 
    
    
    private boolean keepMessageCounters = true;
    private HashMap messageCounters = null;
    
    
    // 3rd Party Syslog client
    private com.ice.syslog.Syslog syslogClient = null;
}
