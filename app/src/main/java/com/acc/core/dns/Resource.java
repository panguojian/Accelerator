//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import java.nio.ByteBuffer;

public class Resource {
    public String Domain;
    public short Type;
    public short Class;
    public int TTL;
    public short DataLangth;
    public byte[] Data;
    private int mOffset;
    private int mLength;

    public Resource() {
    }

    public static Resource fromBytes(ByteBuffer buffer) {
        Resource r = new Resource();
        r.mOffset = buffer.arrayOffset() + buffer.position();
        r.Domain = DnsPacket.readDomain(buffer, buffer.arrayOffset());
        r.Type = buffer.getShort();
        r.Class = buffer.getShort();
        r.TTL = buffer.getInt();
        r.DataLangth = buffer.getShort();
        r.Data = new byte[r.DataLangth & '\uffff'];
        buffer.get(r.Data);
        r.mLength = buffer.arrayOffset() + buffer.position() - r.mOffset;
        return r;
    }

    public int Offset() {
        return this.mOffset;
    }

    public int Length() {
        return this.mLength;
    }

    public void toBytes(ByteBuffer buffer) {
        if (this.Data == null) {
            this.Data = new byte[0];
        }

        this.DataLangth = (short)this.Data.length;
        this.mOffset = buffer.position();
        DnsPacket.writeDomain(this.Domain, buffer);
        buffer.putShort(this.Type);
        buffer.putShort(this.Class);
        buffer.putInt(this.TTL);
        buffer.putShort(this.DataLangth);
        buffer.put(this.Data);
        this.mLength = buffer.position() - this.mOffset;
    }
}
