//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tunnel;

import com.acc.core.helper.Util;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class TmtV2Tunnel<T> extends Tunnel {
    protected byte[] logoutByte = "*logout*".getBytes();

    public TmtV2Tunnel() {
    }

    public abstract void beforeWrite(ByteBuffer var1);

    public abstract byte[] beforeWrite(byte[] var1);

    public abstract void afterRead(ByteBuffer var1);

    public abstract void readTimeOut();

    public abstract void cancelReadTimeOut();

    public abstract void sendHeartPacketLoop();

    public abstract void cancelSendHeartPacket();

    public abstract void logOutTunnel();

    public abstract void decrypt(ByteBuffer var1);

    public abstract void encrypt(ByteBuffer var1);

    public abstract byte[] encrypt(byte[] var1);

    public abstract void connect();

    public abstract void retryConnect();

    public abstract void changeOutputStream(OutputStream var1);

    public boolean isLogout(byte[] buffer) {
        return Arrays.equals(buffer, this.logoutByte);
    }

    public boolean isHeartPacket(byte[] buffer) {
        boolean isHeartPacket = false;

        for(int i = 0; i < 8; ++i) {
            isHeartPacket = buffer[i] == Util.checkHandle[i] || buffer[i] == 0;
        }

        return isHeartPacket;
    }

    public abstract void exit();
}
