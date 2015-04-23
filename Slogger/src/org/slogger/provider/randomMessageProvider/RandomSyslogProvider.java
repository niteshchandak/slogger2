/*
 * RandomMessageProvider.java
 *
 * Created on April 8, 2006, 6:05 PM
 * 
 */

package org.slogger.provider.randomMessageProvider;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;

import org.slogger.*;

/**
 *
 * @author preetham
 */
public class RandomSyslogProvider implements ISyslogProvider {
    public static final String RMP_GLOBAL_KEY = "rmp";
    
    public static final String RMP_SEPERATOR  = ",";
    
    public static final String RMP_COUNT        ="messageCount";
    public static final String RMP_INPUT_SYSLOGS_FILE = "inputSyslogFile";
    public static final String RMP_MESSAGE_TEXT ="messageText";
    public static final String RMP_MESSAGE_MULTIPLIER = "multiplier";
    public static final String RMP_SOURCES  ="sources";
    public static final String RMP_LEVELS  ="levels";
    public static final String RMP_FACILITIES  ="facility";
    public static final String RMP_SRC_PORTS  ="sourcePort";
    public static final String RMP_DST_HOST  ="targetHost";
    public static final String RMP_DST_PORT  ="targetPort";
    public static final String RMP_TIMESTAMP  ="timeStamp";
    
    public static final String RMP_USE_EMBLEM =  "useEmblem";
    
    
    /** Creates a new instance of RandomMessageProvider */
    public RandomSyslogProvider() {
        count = 0;
    }
    
    public void initialize(Properties properties) throws Exception{
        String countStr = properties.getProperty(RMP_GLOBAL_KEY + "."  + RMP_COUNT, "0");                
        useEmblem = Boolean.valueOf(
                properties.getProperty(RMP_GLOBAL_KEY + "."  + RMP_USE_EMBLEM , "TRUE")).booleanValue();                
        
        numOfMessages = Integer.parseInt(countStr);
        if (numOfMessages <= 0)
            numOfMessages = Integer.MAX_VALUE;
        
        // Create global providers
        createGlobalProviders(properties);
        syslogs = new ArrayList<RmpMessage>(); 
        
        Set messages = getMessageList(properties);
        Iterator it = messages.iterator();
        while (it.hasNext()) {
            String messageName = it.next().toString();
            // Get overriddens providers
            IValueProvider messageText = createMessageTextProvider(properties, messageName);
            IValueProvider mLevel = getOverideProvider(properties, messageName, RMP_LEVELS, level) ;
            IValueProvider mSourceHost = getOverideProvider(properties, messageName, RMP_SOURCES, sourceHost) ;
            RmpMessage message = new RmpMessage(messageText, 
                    facility, mLevel, 
                    mSourceHost, sourcePort, 
                    targetHost, targetPort, timeStamp);
            
            int multiplier = getMultiplierForMessage(properties, messageName);
            while (multiplier-- > 0)
            	syslogs.add(message);
            
        }
        
        // Check if a file of syslog has been specified 
        String syslogFile = properties.getProperty(RMP_GLOBAL_KEY + "."  + RMP_INPUT_SYSLOGS_FILE);
        if (syslogFile != null)
        	addSyslogs(syslogFile, syslogs);
    }
    
    
    
    /** 
     * adds syslogs from a file to this
     */
    private void addSyslogs(String syslogFile, List syslogs) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(syslogFile));
		
		String line = br.readLine();
		while (line != null) {
			if (!line.trim().matches("%(\\w)+-\\d-(\\d)+:.*")) 
				continue;
			String[] pieces = line.split("-", 3);
			String fac = pieces[0];
			String sev = pieces[1];
			String mssg = pieces[2];
			
			RmpMessage message = new RmpMessage(
					new FixedValueProvider(mssg),
					facility,
					new FixedValueProvider(sev), 
                    sourceHost, sourcePort, 
                    targetHost, targetPort, timeStamp);
            
			syslogs.add(message);
			line = br.readLine();
		}
	}

	/**
	 * Returns the multiplier factor for this message. This can be used to control how often 
	 * a message should arrive
	 */
	private int getMultiplierForMessage(Properties properties,String messageName) {
		int ret = 1;
		
		String fullKey = messageName + "." + RMP_MESSAGE_MULTIPLIER;
		String s = properties.getProperty(fullKey);
		if (s != null)		
			ret = Integer.parseInt(s.trim());
		
		return ret;
	}

	public boolean hasNext() {
        return (count < numOfMessages);
    }
    
    public ISyslogMessage getNextSyslog() {
        RmpMessage message = (RmpMessage)syslogs.get(
                GenericRandomValueProvider.getRandom(0, syslogs.size()));
        count++;
        return message.getSyslogMessage(useEmblem);
    }
    
    public void shutDown() throws Exception{
    }
    
    /** returns the overridden provider for messageName and key. If it is not
     * overrideen, the defaultProvider will be returned
     */
    private IValueProvider getOverideProvider(Properties p, String messageName, String key, IValueProvider defaultProvider) {
        IValueProvider ret = defaultProvider;
        String fullKey = messageName + "." + key;
        String value = p.getProperty(fullKey);
        if (value != null) 
            ret = createProvider(p, fullKey );
        return ret;        
    }
    
    private void createGlobalProviders(Properties properties) {
        facility= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_FACILITIES);
        level= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_LEVELS);
        sourceHost= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_SOURCES);
        sourcePort= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_SRC_PORTS);
        targetHost= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_DST_HOST);
        targetPort= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_DST_PORT);
        timeStamp= createProvider(properties, RMP_GLOBAL_KEY + "." + RMP_TIMESTAMP);
    }
    
    private IValueProvider createProvider(Properties properties, String key) {
        IValueProvider ret = null;
        String value = properties.getProperty(key);
        if (value == null)
            ret = null;
        else {
            StringTokenizer tokens = new StringTokenizer(value, RMP_SEPERATOR);
            ArrayList options = new ArrayList();
            while (tokens.hasMoreElements())  {
                options.add(tokens.nextToken());
            }
            ret = new GenericRandomValueProvider(options);
        }
        return ret;
    }

    /** returns the list of unique message names. 
     * Messages are assumed to be of the format
     * message.<name>.<property>. 
     * The name of this message is "message.<name>"
     */
    private Set getMessageList(Properties properties) {
        Set uniqueMessages = new HashSet();
        Iterator it = properties.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (key.startsWith("rmp.message.")) {
                uniqueMessages.add(getMessageName(key));
            }
        }
        return uniqueMessages;
    }

    private static String getMessageName(String key) {
        return key.substring(0, key.lastIndexOf('.') );
    }
    
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new 
                FileInputStream("C:/tmp/slogger/src/org/slogger/slogger.properties");
        Properties p = new Properties();
        p.load(fis);
        RandomSyslogProvider prv = new RandomSyslogProvider();
        prv.initialize(p);
        
        while (prv.hasNext())
            prv.getNextSyslog();
        
    }

    private IValueProvider createMessageTextProvider(Properties properties, String messageName) {
        String fullKey = messageName + "." + RMP_MESSAGE_TEXT;        
        return new MessageTextProvider(properties.getProperty(fullKey));
    }
    
    
    
    private List<RmpMessage> syslogs;    
    private int count;
    private int numOfMessages;    
    private boolean useEmblem; // If true, messages are generated in emblem format
    
    // global providers
    private IValueProvider facility;    
    private IValueProvider level;    
    private IValueProvider sourceHost;
    private IValueProvider sourcePort;
    private IValueProvider targetHost;
    private IValueProvider targetPort;
    private IValueProvider timeStamp;
    
}


