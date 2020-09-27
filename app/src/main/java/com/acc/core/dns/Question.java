//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import java.nio.ByteBuffer;

public class Question {
    public String Domain;
    public short Type;
    public short Class;
    private int mOffset;
    private int mLength;

    public Question() {
    }

    public static Question fromBytes(ByteBuffer buffer) {
        Question q = new Question();
        q.mOffset = buffer.arrayOffset() + buffer.position();
        q.Domain = DnsPacket.readDomain(buffer, buffer.arrayOffset());
        q.Type = buffer.getShort();
        q.Class = buffer.getShort();
        q.mLength = buffer.arrayOffset() + buffer.position() - q.mOffset;
        return q;
    }

    public int Offset() {
        return this.mOffset;
    }

    public int Length() {
        return this.mLength;
    }

    public void toBytes(ByteBuffer buffer) {
        this.mOffset = buffer.position();
        DnsPacket.writeDomain(this.Domain, buffer);
        buffer.putShort(this.Type);
        buffer.putShort(this.Class);
        this.mLength = buffer.position() - this.mOffset;
    }
}
