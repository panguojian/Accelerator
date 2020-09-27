//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tcpip;

public class TCPHeader {
    public static final int FIN = 1;
    public static final int SYN = 2;
    public static final int RST = 4;
    public static final int PSH = 8;
    public static final int ACK = 16;
    public static final int URG = 32;
    static final short offset_src_port = 0;
    static final short offset_dest_port = 2;
    static final int offset_seq = 4;
    static final int offset_ack = 8;
    static final byte offset_lenres = 12;
    static final byte offset_flag = 13;
    static final short offset_win = 14;
    static final short offset_crc = 16;
    static final short offset_urp = 18;
    public byte[] mData;
    public int mOffset;

    public TCPHeader(byte[] data, int offset) {
        this.mData = data;
        this.mOffset = offset;
    }

    public int getHeaderLength() {
        int lenres = this.mData[this.mOffset + 12] & 255;
        return (lenres >> 4) * 4;
    }

    public short getSourcePort() {
        return CommonMethods.readShort(this.mData, this.mOffset + 0);
    }

    public void setSourcePort(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 0, value);
    }

    public short getDestinationPort() {
        return CommonMethods.readShort(this.mData, this.mOffset + 2);
    }

    public void setDestinationPort(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 2, value);
    }

    public byte getFlag() {
        return this.mData[this.mOffset + 13];
    }

    public short getCrc() {
        return CommonMethods.readShort(this.mData, this.mOffset + 16);
    }

    public void setCrc(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 16, value);
    }

    public int getSeqID() {
        return CommonMethods.readInt(this.mData, this.mOffset + 4);
    }

    public int getAckID() {
        return CommonMethods.readInt(this.mData, this.mOffset + 8);
    }

    public String toString() {
        return String.format("%s%s%s%s%s%s %d->%d %s:%s", (this.getFlag() & 2) == 2 ? "SYN" : "", (this.getFlag() & 16) == 16 ? "ACK" : "", (this.getFlag() & 8) == 8 ? "PSH" : "", (this.getFlag() & 4) == 4 ? "RST" : "", (this.getFlag() & 1) == 1 ? "FIN" : "", (this.getFlag() & 32) == 32 ? "URG" : "", this.getSourcePort() & '\uffff', this.getDestinationPort() & '\uffff', this.getSeqID(), this.getAckID());
    }
}
