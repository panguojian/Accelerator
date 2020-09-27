//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea;

public class TeaEncrypt {
    private long ptr;

    private native long init(byte[] var1, byte[] var2);

    private native byte[] decryptCmd(long var1, byte[] var3);

    private native byte[] encryptCmd(long var1, byte[] var3);

    private native void resetLength1(long var1);

    private native void resetLength2(long var1);

    private native byte[] encryptPacket(long var1, byte[] var3, int var4);

    private native byte[] decryptPacket(long var1, byte[] var3, int var4);

    private native byte[] encryptStream(long var1, byte[] var3, int var4);

    private native byte[] decryptStream(long var1, byte[] var3, int var4);

    private native void destroy(long var1);

    public TeaEncrypt(byte[] key1, byte[] key2) {
        this.ptr = this.init(key1, key2);
    }

    public byte[] decryptCmd(byte[] data) {
        return this.decryptCmd(this.ptr, data);
    }

    public byte[] encryptCmd(byte[] data) {
        return this.encryptCmd(this.ptr, data);
    }

    public void resetLength1() {
        this.resetLength1(this.ptr);
    }

    public void resetLength2() {
        this.resetLength2(this.ptr);
    }

    public byte[] encryptPacket(byte[] data, int length) {
        return this.encryptPacket(this.ptr, data, length);
    }

    public byte[] decryptPacket(byte[] data, int length) {
        return this.decryptPacket(this.ptr, data, length);
    }

    public byte[] encryptStream(byte[] data, int length) {
        return this.encryptStream(this.ptr, data, length);
    }

    public byte[] decryptStream(byte[] data, int length) {
        return this.decryptStream(this.ptr, data, length);
    }

    public void destroy() {
        this.destroy(this.ptr);
        this.ptr = 0L;
    }

    static {
        System.loadLibrary("confusion-lib");
    }
}
