package org.slogger.provider.randomMessageProvider;

import java.util.Random;


public class RangeValueProvider implements IValueProvider {
    Random r = new Random();
    private long start;
    private long end;
    private long diff;
    private boolean isIp;
    
    public RangeValueProvider(String range) {
        String[] ranges = range.split("-");
        start = getLongValue(ranges[0]);
        end = getLongValue(ranges[1]);
        diff = end - start;
        
    }

    public RangeValueProvider (long start, long end) {
        this.start = start;
        this.end = end;
    }

    public Object getValue() {
        long num = r.nextLong();
        num = (num < 0) ? -1*num :num;
        long val = start + ( num % diff );
        
        return isIp ? IpUtils.toIPv4String((int)val): Long.toString(val);
    }
    
    /** str can be a long string or an IP address */
    private long getLongValue(String str) {
        long ret = 0;
        str = str.trim();
        try {
            ret = Long.parseLong(str);
            isIp = false;
        } catch (NumberFormatException nfe) {
            ret = IpUtils.ipV4IntToLong(IpUtils.ipV4ToInt(str));
            isIp = true;
        }        
        return ret; 
    }	

	

}
