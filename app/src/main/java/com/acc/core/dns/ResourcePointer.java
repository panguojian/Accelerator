//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import com.acc.core.tcpip.CommonMethods;

public class ResourcePointer {
    static final short offset_Domain = 0;
    static final short offset_Type = 2;
    static final short offset_Class = 4;
    static final int offset_TTL = 6;
    static final int offset_DataLength = 10;
    static final int offset_IP = 12;
    byte[] mData;
    int mOffset;

    public ResourcePointer(byte[] data, int offset) {
        this.mData = data;
        this.mOffset = offset;
    }

    public void setDomain(short value) {
        CommonMethods.writeInt(this.mData, this.mOffset + 0, value);
    }

    public short getType() {
        return CommonMethods.readShort(this.mData, this.mOffset + 2);
    }

    public void setType(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 2, value);
    }

    public short getClass(short value) {
        return CommonMethods.readShort(this.mData, this.mOffset + 4);
    }

    public void setClass(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 4, value);
    }

    public int getTTL() {
        return CommonMethods.readInt(this.mData, this.mOffset + 6);
    }

    public void setTTL(int value) {
        CommonMethods.writeInt(this.mData, this.mOffset + 6, value);
    }

    public short getDataLength() {
        return CommonMethods.readShort(this.mData, this.mOffset + 10);
    }

    public void setDataLength(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 10, value);
    }

    public int getIP() {
        return CommonMethods.readInt(this.mData, this.mOffset + 12);
    }

    public void setIP(int value) {
        CommonMethods.writeInt(this.mData, this.mOffset + 12, value);
    }
}
