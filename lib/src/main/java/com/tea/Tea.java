//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea;

public class Tea {
    public static final String key = "defautlKey%$#@^%^$";
    public static final String udpkey = "%$^%#$%^&fiusdofju";

    public Tea() {
    }

    public native byte[] encry(byte[] var1, byte[] var2);

    public native byte[] decry(byte[] var1, byte[] var2);

    static {
        System.loadLibrary("tea-lib");
    }
}
