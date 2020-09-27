//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tcpip;

public class UDPHeader {
    static final short offset_src_port = 0;
    static final short offset_dest_port = 2;
    static final short offset_tlen = 4;
    static final short offset_crc = 6;
    public byte[] mData;
    public int mOffset;

    public UDPHeader(byte[] data, int offset) {
        this.mData = data;
        this.mOffset = offset;
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

    public int getTotalLength() {
        return CommonMethods.readShort(this.mData, this.mOffset + 4) & '\uffff';
    }

    public void setTotalLength(int value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 4, (short)value);
    }

    public short getCrc() {
        return CommonMethods.readShort(this.mData, this.mOffset + 6);
    }

    public void setCrc(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 6, value);
    }

    public String toString() {
        return String.format("%d->%d", this.getSourcePort() & '\uffff', this.getDestinationPort() & '\uffff');
    }
}
