//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea;

public class Crc {
    public Crc() {
    }

    public static native int adler32(byte[] var0, int var1);

    public static byte[] getCrc(byte[] buffer) {
        int crcInt = adler32(buffer, buffer.length);
        byte[] src = new byte[]{(byte)(crcInt & 255), (byte)(crcInt >> 8 & 255), (byte)(crcInt >> 16 & 255), (byte)(crcInt >> 24 & 255)};
        return src;
    }

    static {
        System.loadLibrary("crc-lib");
    }
}
