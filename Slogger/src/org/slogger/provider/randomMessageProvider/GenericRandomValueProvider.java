/*
 * GenericRandomValueProvider.java
 *
 * Created on April 8, 2006, 6:51 PM
 * 
 */

package org.slogger.provider.randomMessageProvider;

import java.util.*;


/**
 * Provides a value randomly choosen from the list of values
 * @author preetham
 */
public class GenericRandomValueProvider implements IValueProvider{
    
    /** Creates a new instance of GenericRandomValueProvider */
    public GenericRandomValueProvider(List list) {
        this.list = list;
    }
    
    /** Creates a GenericRandonValueProvider from a comma seperated string of 
     * options. Eg "a,b,c"
     */
    public GenericRandomValueProvider(String options) {        
        this.list = new ArrayList();
        if (options == null)
            return;
       String[] tmp = options.split(",") ;       
       for (int i = 0; i < tmp.length; i++ ) {
    	   if (tmp[i].indexOf('-') > 0)
    		   list.add(new RangeValueProvider(tmp[i]));
    	   else 
    		   list.add(tmp[i]);
       }
           
    }
    
    public Object getValue() {
        if (list == null)
            return null;
        else {
            Object o = list.get(getRandom(0, list.size()));
            if (o instanceof RangeValueProvider)
            	return ((RangeValueProvider)o).getValue();
            else
            	return o;
            
        }
    }
    
    public String toString() {
        return (list == null ? null : list.toString());
    }
    
    
    public static int getRandom(int start, int end) {
        int ret = 0;
        ret = start + (int)(Math.random() * (end - start));
        return ret;
    }
    
    
    public static void main(String[] args) throws Exception {
        ArrayList l = new ArrayList();
        l.add("1");
        l.add("2");
        l.add("3");
        l.add("4");
        l.add("5");
        
        //GenericRandomValueProvider g = new GenericRandomValueProvider(l);
        GenericRandomValueProvider g = new GenericRandomValueProvider("a,b,c,d");
        
        for (int i = 0; i < 10; i++)
            System.out.println(g.getValue());

    }
    
    private List list = null;    
    
    
}
