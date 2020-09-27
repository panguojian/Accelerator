//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea.encrypt;

import com.tea.MD5;
import com.tea.Tea;

public class StreamEncrypt {
    private byte[] key1;
    private byte[] key2;
    private byte[] keyData1;
    private byte[] keyData2;
    private long dataLength1 = 0L;
    private long dataLength2 = 0L;
    private Tea tea;

    public StreamEncrypt(String key) {
        this.key1 = MD5.MD5(key, 0);
        this.key2 = MD5.MD5(key + key, 0);
        this.tea = new Tea();
    }

    public void initKeyDate() {
        this.keyData1 = new byte[8];
        this.keyData2 = new byte[8];

        int i;
        for(i = 0; i < this.keyData1.length; ++i) {
            this.keyData1[i] = (byte)(i * 3);
        }

        for(i = 0; i < this.keyData2.length; ++i) {
            this.keyData2[i] = (byte)(i * 3);
        }

    }

    public byte[] encryptCmd(byte[] data) {
        return this.tea.encry(data, this.key1);
    }

    public byte[] decryptCmd(byte[] data) {
        return this.tea.decry(data, this.key1);
    }

    public byte[] encrypt(byte[] realDate) {
        byte[] udpData = new byte[8];
        byte[] temp = new byte[8];
        byte[] illusoryDate = new byte[realDate.length];
        int length = realDate.length;

        int i;
        for(i = 0; i < udpData.length; ++i) {
            udpData[i] = realDate[length - 8 + i];
            temp[i] = udpData[i];
        }

        for(i = 0; i < length - 8; ++i) {
            if (i % 8 == 0) {
                udpData = this.tea.decry(udpData, this.key1);
                udpData = this.tea.decry(udpData, this.key2);
            }

            illusoryDate[i] = (byte)(udpData[i % 8] ^ realDate[i]);
            udpData[i % 8] = illusoryDate[i];
        }

        for(i = 0; i < 8; ++i) {
            illusoryDate[length - 8 + i] = temp[i];
        }

        return illusoryDate;
    }

    public byte[] decrypt(byte[] illusoryDate) {
        byte[] udpData = new byte[8];
        byte[] temp = new byte[8];
        byte[] realDate = new byte[illusoryDate.length];
        int length = illusoryDate.length;

        int i;
        for(i = 0; i < udpData.length; ++i) {
            udpData[i] = illusoryDate[length - 8 + i];
            temp[i] = udpData[i];
        }

        for(i = 0; i < length - 8; ++i) {
            if (i % 8 == 0) {
                udpData = this.tea.decry(udpData, this.key1);
                udpData = this.tea.decry(udpData, this.key2);
            }

            realDate[i] = (byte)(udpData[i % 8] ^ illusoryDate[i]);
            udpData[i % 8] = illusoryDate[i];
        }

        for(i = 0; i < 8; ++i) {
            realDate[length - 8 + i] = temp[i];
        }

        return realDate;
    }

    public void resetLength1() {
        this.dataLength1 = 0L;
    }

    public void resetLength2() {
        this.dataLength2 = 0L;
    }

    public byte[] encryptStream(byte[] data) {
        int length = data.length;

        for(int i = 0; i < length; ++i) {
            if (((long)i + this.dataLength1) % 8L == 0L) {
                this.keyData1 = this.tea.decry(this.keyData1, this.key1);
                this.keyData1 = this.tea.decry(this.keyData1, this.key2);
            }

            data[i] ^= this.keyData1[(int)((long)i + this.dataLength1) % 8];
            this.keyData1[(int)((long)i + this.dataLength1) % 8] = data[i];
        }

        this.dataLength1 += (long)length;
        this.dataLength1 %= 8L;
        return data;
    }

    public byte[] decryptStream(byte[] data) {
        int length = data.length;

        for(int i = 0; i < length; ++i) {
            if (((long)i + this.dataLength2) % 8L == 0L) {
                this.keyData2 = this.tea.decry(this.keyData2, this.key1);
                this.keyData2 = this.tea.decry(this.keyData2, this.key2);
            }

            byte temp = data[i];
            data[i] ^= this.keyData2[(int)((long)i + this.dataLength2) % 8];
            this.keyData2[(int)((long)i + this.dataLength2) % 8] = temp;
        }

        this.dataLength2 += (long)length;
        this.dataLength2 %= 8L;
        return data;
    }
}
