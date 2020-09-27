//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tcpip;

public class IPHeader {
    public static final short IP = 2048;
    public static final byte ICMP = 1;
    public static final byte TCP = 6;
    public static final byte UDP = 17;
    static final byte offset_ver_ihl = 0;
    static final byte offset_tos = 1;
    static final short offset_tlen = 2;
    static final short offset_identification = 4;
    static final short offset_flags_fo = 6;
    static final byte offset_ttl = 8;
    public static final byte offset_proto = 9;
    static final short offset_crc = 10;
    public static final int offset_src_ip = 12;
    public static final int offset_dest_ip = 16;
    static final int offset_op_pad = 20;
    public byte[] m_Data;
    public int m_Offset;

    public IPHeader(byte[] data, int offset) {
        this.m_Data = data;
        this.m_Offset = offset;
    }

    public void Default() {
        this.setHeaderLength(20);
        this.setTos((byte)0);
        this.setTotalLength(0);
        this.setIdentification(0);
        this.setFlagsAndOffset((short)0);
        this.setTTL((byte)64);
    }

    public int getDataLength() {
        return this.getTotalLength() - this.getHeaderLength();
    }

    public int getHeaderLength() {
        return (this.m_Data[this.m_Offset + 0] & 15) * 4;
    }

    public void setHeaderLength(int value) {
        this.m_Data[this.m_Offset + 0] = (byte)(64 | value / 4);
    }

    public byte getTos() {
        return this.m_Data[this.m_Offset + 1];
    }

    public void setTos(byte value) {
        this.m_Data[this.m_Offset + 1] = value;
    }

    public int getTotalLength() {
        return CommonMethods.readShort(this.m_Data, this.m_Offset + 2) & '\uffff';
    }

    public void setTotalLength(int value) {
        CommonMethods.writeShort(this.m_Data, this.m_Offset + 2, (short)value);
    }

    public int getIdentification() {
        return CommonMethods.readShort(this.m_Data, this.m_Offset + 4) & '\uffff';
    }

    public void setIdentification(int value) {
        CommonMethods.writeShort(this.m_Data, this.m_Offset + 4, (short)value);
    }

    public short getFlagsAndOffset() {
        return CommonMethods.readShort(this.m_Data, this.m_Offset + 6);
    }

    public void setFlagsAndOffset(short value) {
        CommonMethods.writeShort(this.m_Data, this.m_Offset + 6, value);
    }

    public byte getTTL() {
        return this.m_Data[this.m_Offset + 8];
    }

    public void setTTL(byte value) {
        this.m_Data[this.m_Offset + 8] = value;
    }

    public byte getProtocol() {
        return this.m_Data[this.m_Offset + 9];
    }

    public void setProtocol(byte value) {
        this.m_Data[this.m_Offset + 9] = value;
    }

    public short getCrc() {
        return CommonMethods.readShort(this.m_Data, this.m_Offset + 10);
    }

    public void setCrc(short value) {
        CommonMethods.writeShort(this.m_Data, this.m_Offset + 10, value);
    }

    public int getSourceIP() {
        return CommonMethods.readInt(this.m_Data, this.m_Offset + 12);
    }

    public void setSourceIP(int value) {
        CommonMethods.writeInt(this.m_Data, this.m_Offset + 12, value);
    }

    public int getDestinationIP() {
        return CommonMethods.readInt(this.m_Data, this.m_Offset + 16);
    }

    public void setDestinationIP(int value) {
        CommonMethods.writeInt(this.m_Data, this.m_Offset + 16, value);
    }

    public String toString() {
        return String.format("%s->%s Pro=%s,HLen=%d", CommonMethods.ipIntToString(this.getSourceIP()), CommonMethods.ipIntToString(this.getDestinationIP()), this.getProtocol(), this.getHeaderLength());
    }
}
