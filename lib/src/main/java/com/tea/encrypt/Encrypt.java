//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea.encrypt;

import com.tea.MD5;
import com.tea.TeaEncrypt;

public class Encrypt {
    public static final int CMD_TYPE = 1;
    public static final int UDP_TYPE = 4;
    public static final int TCP_TYPE = 8;
    private TeaEncrypt mEncrypt;
    private String key;

    public Encrypt(String key) {
        this.key = key;
        byte[] key1 = MD5.MD5(key, 0);
        byte[] key2 = MD5.MD5(key + key, 0);
        this.mEncrypt = new TeaEncrypt(key1, key2);
        this.mEncrypt.resetLength1();
        this.mEncrypt.resetLength2();
    }

    public byte[] decrypt(byte[] data, int type) {
        switch(type) {
            case 1:
                return this.mEncrypt.decryptCmd(data);
            case 4:
                return this.mEncrypt.decryptPacket(data, data.length);
            case 8:
                return this.mEncrypt.decryptStream(data, data.length);
            default:
                return null;
        }
    }

    public byte[] encrypt(byte[] data, int type) {
        switch(type) {
            case 1:
                return this.mEncrypt.encryptCmd(data);
            case 4:
                return this.mEncrypt.encryptPacket(data, data.length);
            case 8:
                return this.mEncrypt.encryptStream(data, data.length);
            default:
                return null;
        }
    }

    public void destroy() {
        this.mEncrypt.destroy();
    }
}
