/*
 * Slogger.java
 *
 * Created on April 8, 2006, 5:35 PM
 *
 */

package org.slogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Slogger's main class. 
 * @author preetham
 */
public class Slogger {
    
    public static String PROVIDER_KEY="syslogProvider";
    public static String PUBLISHER_KEY="syslogPublisher";
    public static String DELAY = "delayBetweenMessages";
    public static String EPS_RATE = "maxEpsRate";
    public static String PROMPT_FOR_START = "promptForStart";
    public static String DISPLAY_COUNTERS = "displayCounters";
    public static String DISPLAY_SENT_MESSAGES = "displaySentMessages";
    
    
    
    private static final String VERSION = "v0.6";

   
    

    

    

    private void init(Properties props) throws Exception {
        provider.initialize(props);
        publisher.initialize(props);        

        promptForStart = Boolean.valueOf(props.getProperty(PROMPT_FOR_START, "FALSE")).booleanValue();
        displayCounter = Boolean.valueOf(props.getProperty(DISPLAY_COUNTERS, "FALSE")).booleanValue();
        
        int epsRate = Integer.parseInt(props.getProperty(EPS_RATE, "0"));
        this.epsLimiter = new EpsLimiter(epsRate);
    }
    
    
    public Slogger(Properties props, ISyslogPublisher publisher) throws Exception {
        provider = createProvider(props);        
        this.publisher = publisher;
        init(props);
        
    }
    
    /** Creates a new instance of Slogger */
    public Slogger(Properties props) throws Exception {
        // Create the Providers
        provider = createProvider(props);        
        publisher = createPublisher(props);
        
        init(props);
    }
    
    // Run the Slogger
    public void slog() throws Exception {
        if (promptForStart) {            
            System.out.println("Press ENTER to start");
            System.in.read();
        }
        
        long start = System.currentTimeMillis();
        long counter = 0;
        int eps = 0;
        while (provider.hasNext()) {
			if (epsLimiter.ok()) {
				ISyslogMessage syslog = provider.getNextSyslog();
				publisher.publishSyslog(syslog);
				counter++;
				if (displayCounter) {
					if (counter % 512 == 0) // dont calculate EPS everytime
						eps = (int) (counter * 1000 / (System
								.currentTimeMillis() - start));
					System.out.print("\r# syslogs generated " + counter + " @"
							+ eps + " eps");
				}
			} else
				Thread.sleep(25);
        }
        publisher.shutDown();
        epsLimiter.stopLimiter();
        long stop = System.currentTimeMillis();
        double time = (1.0*(stop - start))/1000;
        //int uniqueMessageCount = sentMessages.keySet().size();
        System.out.println("\nslogger generated " + counter +
        		" syslogs  in " + time + " secs @ " + (int)(counter/time) + " eps");
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            System.out.println("Slogger will attempt using ./slogger.properties");
            args = new String[1];
            args[0] = "slogger.properties";
        }
        
        System.out.println("Slogger " + VERSION + " running.");
        long start = System.currentTimeMillis();
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(args[0]);
            props.load(fis);
            Slogger slogger = new Slogger(props);
            slogger.slog();
            
        } catch (IOException ex) {            
            System.out.println(ex.getMessage() + ". Slogger exiting. ");
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage() + ". Slogger failed to initialize");
            e.printStackTrace();
            System.exit(0);
        }
        double timeInSecs = (System.currentTimeMillis() - start)*0.001;
        System.out.println("Completed in " + timeInSecs + " secs.");
    }

    private static void printUsage() {
        System.out.println("Slogger " + VERSION);
        System.out.println("Usage java org.slogger.Slogger <PropertiesFileName>");
        
    }

    private ISyslogProvider createProvider(Properties props) throws Exception {
        String className = props.getProperty(Slogger.PROVIDER_KEY);
        Class c = Class.forName(className);
        return (ISyslogProvider) c.newInstance();
    }

    private ISyslogPublisher createPublisher(Properties props) throws Exception {
        String className = props.getProperty(Slogger.PUBLISHER_KEY);
        Class c = Class.forName(className);        
        return (ISyslogPublisher) c.newInstance();
    }
    
    
    // Private data memebers
    private boolean promptForStart;
    private boolean displayCounter = true;
    private ISyslogProvider provider = null;
    private ISyslogPublisher publisher = null;    
    private EpsLimiter epsLimiter = null;
    
    
    
}
