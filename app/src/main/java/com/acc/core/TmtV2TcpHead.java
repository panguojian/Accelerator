//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import java.nio.ByteBuffer;
import java.util.Random;

public class TmtV2TcpHead {
    public static final int upload_cmd = 1;
    public static final int download_cmd = 0;
    public static final int invalid_cmd = 2;
    public byte[] cmd;
    public short nameLen = 0;
    public short passLen = 0;
    public short urlLen = 0;
    public short clientLen = 0;
    public byte[] name;
    public byte[] password;
    public byte[] url;
    public byte[] client;
    public short port = 0;

    public TmtV2TcpHead() {
    }

    public byte[] getHanderByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16 + this.nameLen + this.passLen + this.urlLen);
        byteBuffer.clear();
        byteBuffer.put(this.cmd);
        byteBuffer.putShort(this.nameLen);
        byteBuffer.putShort(this.passLen);
        byteBuffer.putShort(this.urlLen);
        if (this.name != null) {
            byteBuffer.put(this.name);
        }

        if (this.password != null) {
            byteBuffer.put(this.password);
        }

        if (this.url != null) {
            byteBuffer.put(this.url);
        }

        byteBuffer.putShort(this.port);
        byteBuffer.flip();
        return this.bigToSmall(byteBuffer.array());
    }

    public void genCmd(int c) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte)c);

        for(int i = 1; i < 7; ++i) {
            byteBuffer.put((byte)this.random());
        }

        byteBuffer.put((byte)c);
        this.cmd = new byte[8];
        byteBuffer.flip();
        byteBuffer.get(this.cmd);
    }

    int random() {
        Random random = new Random();
        return random.nextInt();
    }

    byte[] bigToSmall(byte[] data) {
        byte temp;
        if (this.nameLen != 0) {
            temp = data[8];
            data[8] = data[9];
            data[9] = temp;
        }

        if (this.passLen != 0) {
            temp = data[10];
            data[10] = data[11];
            data[11] = temp;
        }

        if (this.urlLen != 0) {
            temp = data[12];
            data[12] = data[13];
            data[13] = temp;
        }

        if (this.port != 0) {
            temp = data[data.length - 2];
            data[data.length - 2] = data[data.length - 1];
            data[data.length - 1] = temp;
        }

        return data;
    }
}
