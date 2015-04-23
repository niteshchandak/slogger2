/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.slogger.provider.randomMessageProvider;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Utils methods for IP Address/ports etc
 * @author preetham
 */
public class IpUtils {

    public final static String DOT = ".";

    /**
     * Returns the specified 32-bit int as a dotted-decimal string.
     * @param address a 32-bit int representing an IP address.
     * @return the dotted-decimal represention of the IP address,
     * e.g. <TT>255.1.128.0</TT>
     */
    public static String toIPv4String(int address) {
    	StringBuilder sb = new StringBuilder(16);
        for (int ii = 3; ii >= 0; ii--) {
            sb.append((int) (0xFF & (address >> (8 * ii))));
            if (ii > 0) {
                sb.append(DOT);
            }
        }
        return sb.toString();
    }

    /**
     * Returns and compact string converting each byte into 2 hex codes
     * So 10.1.2.255 would be 0a0102255
     * @param bytes
     * @return
     */
    public static String toCompactString(byte[] bytes) {
         char[] c= new char[bytes.length*2];
        for (int i = 0; i < bytes.length; i++) {
            c[i*2] = IpUtils.getHexCharsForByte(bytes[i])[0];
            c[i*2 + 1] = IpUtils.getHexCharsForByte(bytes[i])[1];
        }
        return new String(c);
    }


    public static InetAddress ipFromCompactString(String str) throws UnknownHostException {        
        Long l = Long.parseLong(str, 16);
        return InetAddress.getByAddress(ipV4ToBytes(l));
    }
    

    public static byte[] ipV6ToByteArray(String s) {
        throw new UnsupportedOperationException("Only IpV4 supported by IpAddressField");        
    }

    public static byte[] ipV4ToByteArray(String s) {
        if (s == null) {
            throw new NullPointerException();
        }

        /**
         * I parse the string by myself and not with StringTokenizer
         * since the string empty string is not count so in the
         * string 2..2..2..2 have 4 token which is wrong.
         */
        int sub_address = 0;
        boolean empty_token = true;
        byte[] s_bytes = s.getBytes();
        byte[] address = new byte[4];
        int index = 0;
        int token_index = 0;

        while (index < s_bytes.length) {
            if (s_bytes[index] == '.') {
                if ((empty_token == true) || (sub_address > 255) || (token_index > 3)) {
                    throw new IllegalArgumentException("invalid address value");
                }

                address[token_index] = (byte) sub_address;
                token_index++;
                sub_address = 0x0;
                empty_token = true;
            } else {
                if (s_bytes[index] >= '0' && s_bytes[index] <= '9') {
                    empty_token = false;
                    sub_address *= 10;
                    sub_address += (s_bytes[index] - '0');
                } else {
                    //invalid character
                    throw new IllegalArgumentException("invalid character");
                }

            }
            index++;
        }

        //end of the string add the last token
        if ((empty_token == true) || (sub_address > 255) || (token_index > 3)) {
            throw new IllegalArgumentException("invalid address value");
        }

        address[token_index] = (byte) sub_address;

        if (token_index != 3) {
            throw new IllegalArgumentException("invalid address value");
        }
        return address;

    }

    /**
     * Returns an integer representing the dotted-decimal IP address string.
     * @param s a dotted-decimal string.
     * @return an integer representing a 32-bit address.
     */
    public static int ipV4ToInt(String s) {        
        return ipV4ToInt(ipV4ToByteArray(s));
    }

    /**
     * Returns an integer representing the 4-byte IP address.
     * @param bytes the 8-bit bytes representing an IP address.
     * @return an integer representing a 32-bit address.
     */
    public static int ipV4ToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException(new String(bytes));
        }
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }


     public static long ipV4ToLong(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException(new String(bytes));
        }
        long addr = bytes[3] & 0xFF;
         
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }


    public static byte[] ipV4ToBytes(long address) {
        return ipV4ToBytes((int)address);
    }
    /**
     * Returns the dotted-decimal string representation of the specified int.
     * @param address an integer representing a 32-bit address.
     * @return a dotted-decimal string.
     */
    public static byte[] ipV4ToBytes(int address) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((address >> 24) & 0xFF);
        bytes[1] = (byte) ((address >> 16) & 0xFF);
        bytes[2] = (byte) ((address >> 8) & 0xFF);
        bytes[3] = (byte) (address & 0xFF);
        return bytes;
    }


    /** Gets a long value corresponing to this IP
     * 0.0.0.0 is 0
     * 0.0.0.1 is 1
     * 127.255.255.255 is Integer.MAX_VALUE
     * 128.0.0.0 is Integer.MAX_VALUE + 1
     * @param address The int value of the IpV4 address
     * @return
     */
    public static long ipV4IntToLong(int address) {
        long ret = address;
        if (address < 0) {
            // address < 0 means this address gt than 127.255.255.255 (Integer.MAX)
            // 128.0.0.0 is Inetger.MIN. Hence, to get an absolute number, we will
            // need to add an offset from Integer.MIN to Inetger.MAX
            int offSetFromIntMin = address - Integer.MIN_VALUE;
            ret = (long) Integer.MAX_VALUE + offSetFromIntMin + 1;
        }
        return ret;
    }

    public static char[] getHexCharsForByte(byte b) {
        int idx = b < 0 ? b + 256 : b;
        return HEX_CODES_FOR_BYTES[idx];
    }


    /*
     * Initiaze an array with the hex code for values from 0 to 255. This will
     * make converting byte to hex string very fast
     */
    private static final char[][] HEX_CODES_FOR_BYTES = initHexCodesForBytes();
    private static char[][] initHexCodesForBytes() {
        char[][] hexCodes = new char[256][2];
        for (int i = 0; i < 256; i++) {
            String s = Integer.toHexString(i);
            if (s.length() == 1) {
                hexCodes[i][0] = '0';
                hexCodes[i][1] = s.charAt(0);
            } else {
                hexCodes[i][0] = s.charAt(0);
                hexCodes[i][1] = s.charAt(1);
            }
        }
        return hexCodes;
    }

    /**
     * Checks if the ip address ip is in the range of start and end
     * @param ip
     * @param start
     * @param end
     * @return
     */
    public static boolean isIpInRange(InetAddress ip, InetAddress start, InetAddress end) {
        BigInteger bIp = getIpAsBigInt(ip.getAddress());
        BigInteger bStart = getIpAsBigInt(start.getAddress());
        
        if (end != null) {
            BigInteger bEnd = getIpAsBigInt(end.getAddress());
            return ( (bIp.compareTo(bStart) >= 0) && (bIp.compareTo(bEnd) <= 0) );
        } else
            return (bIp.compareTo(bStart) == 0);

    }


    public static BigInteger getIpAsBigInt(byte[] ip) {
        byte[] tmp = new byte[ip.length + 1]; // Keep the high order byte as 0 so that it does not become negetive
        System.arraycopy(ip, 0, tmp, 1, ip.length);
        return new BigInteger(tmp);
    }
    

    public static void main(String[] args) throws Exception {
        System.out.println("!!!=" + ipFromCompactString("0bece580"));

        InetAddress a = InetAddress.getByName("127.255.255.255");
        System.out.println(">" + ipV4ToLong(a.getAddress()));
        System.out.println(">" + getIpAsBigInt(a.getAddress()));

         a = InetAddress.getByName("128.0.0.0");
        System.out.println(">" + ipV4ToLong(a.getAddress()));
        System.out.println(">" + getIpAsBigInt(a.getAddress()));

         InetAddress a1 = InetAddress.getByName("128.0.0.0");
         InetAddress a2 = InetAddress.getByName("129.0.0.0");

         a = InetAddress.getByName("228.1.5.6");
         System.out.println(">" + isIpInRange(a, a1, a2));



    }

    
}
