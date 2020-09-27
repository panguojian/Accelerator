//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import com.acc.core.tcpip.CommonMethods;
import java.nio.ByteBuffer;

public class DnsHeader {
    static final short offset_ID = 0;
    static final short offset_Flags = 2;
    static final short offset_QuestionCount = 4;
    static final short offset_ResourceCount = 6;
    static final short offset_AResourceCount = 8;
    static final short offset_EResourceCount = 10;
    public short ID;
    public DnsFlag flags;
    public short QuestionCount;
    public short ResourceCount;
    public short AResourceCount;
    public short EResourceCount;
    public byte[] mData;
    public int mOffset;

    public DnsHeader(byte[] data, int offset) {
        this.mData = data;
        this.mOffset = offset;
    }

    public static DnsHeader fromBytes(ByteBuffer buffer) {
        DnsHeader header = new DnsHeader(buffer.array(), buffer.arrayOffset() + buffer.position());
        header.ID = buffer.getShort();
        header.flags = DnsFlag.Parse(buffer.getShort());
        header.QuestionCount = buffer.getShort();
        header.ResourceCount = buffer.getShort();
        header.AResourceCount = buffer.getShort();
        header.EResourceCount = buffer.getShort();
        return header;
    }

    public void toBytes(ByteBuffer buffer) {
        buffer.putShort(this.ID);
        buffer.putShort(this.flags.ToShort());
        buffer.putShort(this.QuestionCount);
        buffer.putShort(this.ResourceCount);
        buffer.putShort(this.AResourceCount);
        buffer.putShort(this.EResourceCount);
    }

    public short getID() {
        return CommonMethods.readShort(this.mData, this.mOffset + 0);
    }

    public void setID(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 0, value);
    }

    public short getFlags() {
        return CommonMethods.readShort(this.mData, this.mOffset + 2);
    }

    public void setFlags(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 2, value);
    }

    public short getQuestionCount() {
        return CommonMethods.readShort(this.mData, this.mOffset + 4);
    }

    public void setQuestionCount(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 4, value);
    }

    public short getResourceCount() {
        return CommonMethods.readShort(this.mData, this.mOffset + 6);
    }

    public void setResourceCount(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 6, value);
    }

    public short getAResourceCount() {
        return CommonMethods.readShort(this.mData, this.mOffset + 8);
    }

    public void setAResourceCount(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 8, value);
    }

    public short getEResourceCount() {
        return CommonMethods.readShort(this.mData, this.mOffset + 10);
    }

    public void setEResourceCount(short value) {
        CommonMethods.writeShort(this.mData, this.mOffset + 10, value);
    }
}
