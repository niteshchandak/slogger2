/*
 * MessageTextProvider.java
 *
 * Created on April 8, 2006, 10:19 PM
 * 
 */

package org.slogger.provider.randomMessageProvider;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author preetham
 */
public class MessageTextProvider implements IValueProvider {
    
    public static final String VAR_ELEMENT_PATTERN = "<<.*?>>";
    public static final String VAR_ELEMENT_START  = "<<";
    public static final String VAR_ELEMENT_END  = ">>";
    
    public static final String MSSG_FORMAT_START = "{";
    public static final String MSSG_FORMAT_END = "{";
    
    /** Creates a new instance of MessageTextProvider */
    public MessageTextProvider(String message) {
        this.rawMessage = message;
        this.messageFormat = message; // we will edit this later
        this.varValueProviders = new ArrayList();
        
         Pattern pattern = Pattern.compile(VAR_ELEMENT_PATTERN);
         Matcher matcher = pattern.matcher(message);
         int count = 0;
         while (matcher.find()) {
             String varElement = matcher.group();
             // Strip the leading and training angle brackets
             String varValues = varElement.replaceAll(VAR_ELEMENT_START, "").
                     replaceAll(VAR_ELEMENT_END, "");
 
             
             varValueProviders.add(new GenericRandomValueProvider(varValues));             
             messageFormat = messageFormat.replaceFirst(varElement, "{" + count++ + "}");
         }
         
         //System.out.println("MF is" + messageFormat);
         //System.out.println("LIST " + varValueProviders);
        
    }

    public Object getValue() {
        Object[] args = new Object[varValueProviders.size()];
        for (int i = 0; i < varValueProviders.size(); i++)
            args[i] = ((GenericRandomValueProvider)varValueProviders.get(i)).getValue();        
        return MessageFormat.format(messageFormat, args);
    }
    
    
    public static void main(String[] args) {
        
        String text = "access-list acl-inside permitted udp inside/<<100.1.1.1-100.1.1.100>>(<<100-200>>)" + "" +
        		" -> outside/100.0.0.10(137) hit-cnt 1 (first hit)";
        MessageTextProvider m = new MessageTextProvider(text);
        
        for (int i = 0; i < 20; i++)
            System.out.println( m.getValue());
    }
    
    private String rawMessage;
    private String messageFormat;
    private List varValueProviders;
}
