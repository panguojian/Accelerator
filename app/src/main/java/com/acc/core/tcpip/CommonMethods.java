//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tcpip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommonMethods {
    public CommonMethods() {
    }

    public static InetAddress ipIntToInet4Address(int ip) {
        byte[] ipAddress = new byte[4];
        writeInt(ipAddress, 0, ip);

        try {
            return Inet4Address.getByAddress(ipAddress);
        } catch (UnknownHostException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String ipIntToString(int ip) {
        return String.format("%s.%s.%s.%s", ip >> 24 & 255, ip >> 16 & 255, ip >> 8 & 255, ip & 255);
    }

    public static String ipBytesToString(byte[] ip) {
        return String.format("%s.%s.%s.%s", ip[0] & 255, ip[1] & 255, ip[2] & 255, ip[3] & 255);
    }

    public static int ipStringToInt(String ip) {
        String[] arrStrings = ip.split("\\.");
        int r = Integer.parseInt(arrStrings[0]) << 24 | Integer.parseInt(arrStrings[1]) << 16 | Integer.parseInt(arrStrings[2]) << 8 | Integer.parseInt(arrStrings[3]);
        return r;
    }

    public static int readInt(byte[] data, int offset) {
        int r = (data[offset] & 255) << 24 | (data[offset + 1] & 255) << 16 | (data[offset + 2] & 255) << 8 | data[offset + 3] & 255;
        return r;
    }

    public static short readShort(byte[] data, int offset) {
        int r = (data[offset] & 255) << 8 | data[offset + 1] & 255;
        return (short)r;
    }

    public static void writeInt(byte[] data, int offset, int value) {
        data[offset] = (byte)(value >> 24);
        data[offset + 1] = (byte)(value >> 16);
        data[offset + 2] = (byte)(value >> 8);
        data[offset + 3] = (byte)value;
    }

    public static void writeShort(byte[] data, int offset, short value) {
        data[offset] = (byte)(value >> 8);
        data[offset + 1] = (byte)value;
    }

    public static short htons(short u) {
        int r = (u & '\uffff') << 8 | (u & '\uffff') >> 8;
        return (short)r;
    }

    public static short ntohs(short u) {
        int r = (u & '\uffff') << 8 | (u & '\uffff') >> 8;
        return (short)r;
    }

    public static int hton(int u) {
        int r = u >> 24 & 255;
        r |= u >> 8 & '\uff00';
        r |= u << 8 & 16711680;
        r |= u << 24 & -16777216;
        return r;
    }

    public static int ntoh(int u) {
        int r = u >> 24 & 255;
        r |= u >> 8 & '\uff00';
        r |= u << 8 & 16711680;
        r |= u << 24 & -16777216;
        return r;
    }

    public static short checksum(long sum, byte[] buf, int offset, int len) {
        for(sum += getsum(buf, offset, len); sum >> 16 > 0L; sum = (sum & 65535L) + (sum >> 16)) {
        }

        return (short)((int)(~sum));
    }

    public static long getsum(byte[] buf, int offset, int len) {
        long sum;
        for(sum = 0L; len > 1; len -= 2) {
            sum += (long)(readShort(buf, offset) & '\uffff');
            offset += 2;
        }

        if (len > 0) {
            sum += (long)((buf[offset] & 255) << 8);
        }

        return sum;
    }

    public static boolean ComputeIPChecksum(IPHeader ipHeader) {
        short oldCrc = ipHeader.getCrc();
        ipHeader.setCrc((short)0);
        short newCrc = checksum(0L, ipHeader.m_Data, ipHeader.m_Offset, ipHeader.getHeaderLength());
        ipHeader.setCrc(newCrc);
        return oldCrc == newCrc;
    }

    public static boolean ComputeTCPChecksum(IPHeader ipHeader, TCPHeader tcpHeader) {
        ComputeIPChecksum(ipHeader);
        int ipData_len = ipHeader.getDataLength();
        if (ipData_len < 0) {
            return false;
        } else {
            long sum = getsum(ipHeader.m_Data, ipHeader.m_Offset + 12, 8);
            sum += (long)(ipHeader.getProtocol() & 255);
            sum += (long)ipData_len;
            short oldCrc = tcpHeader.getCrc();
            tcpHeader.setCrc((short)0);
            short newCrc = checksum(sum, tcpHeader.mData, tcpHeader.mOffset, ipData_len);
            tcpHeader.setCrc(newCrc);
            return oldCrc == newCrc;
        }
    }

    public static boolean ComputeUDPChecksum(IPHeader ipHeader, UDPHeader udpHeader) {
        ComputeIPChecksum(ipHeader);
        int ipData_len = ipHeader.getDataLength();
        if (ipData_len < 0) {
            return false;
        } else {
            long sum = getsum(ipHeader.m_Data, ipHeader.m_Offset + 12, 8);
            sum += (long)(ipHeader.getProtocol() & 255);
            sum += (long)ipData_len;
            short oldCrc = udpHeader.getCrc();
            udpHeader.setCrc((short)0);
            short newCrc = checksum(sum, udpHeader.mData, udpHeader.mOffset, ipData_len);
            udpHeader.setCrc(newCrc);
            return oldCrc == newCrc;
        }
    }
}
