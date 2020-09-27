//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import com.tea.Crc;
import java.nio.ByteBuffer;
import java.util.Random;

public class TmtV2Packet {
    public String name;
    public String password;
    public String session;
    public String client;
    private int nameLen;
    private int passwordLen;
    private int clientTypeLen;
    private int androidIdLen;
    private int sessionLen;
    private int clientLen;

    public TmtV2Packet() {
    }

    public byte[] getTmtV2UdpPacketHandle() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1500);
        byte[] nameByte = this.name.getBytes();
        byte[] passwordByte = this.password.getBytes();
        byte[] androidId = LauncherTmtV2.AndroidId.getBytes();
        byte[] sessionByte = this.session.getBytes();
        byte[] clientByte = null;
        if (this.client != null) {
            clientByte = this.client.getBytes();
            this.clientLen = clientByte.length;
        }

        this.nameLen = nameByte.length;
        this.passwordLen = passwordByte.length;
        this.androidIdLen = androidId.length;
        this.sessionLen = sessionByte.length;
        byteBuffer.putShort((short)this.nameLen);
        byteBuffer.put(nameByte);
        byteBuffer.putShort((short)this.passwordLen);
        byteBuffer.put(passwordByte);
        String clientType = "Android";
        this.clientTypeLen = clientType.getBytes().length;
        byteBuffer.putShort((short)this.clientTypeLen);
        byteBuffer.put(clientType.getBytes());
        byteBuffer.putShort((short)this.androidIdLen);
        byteBuffer.put(androidId);
        if (this.androidIdLen > 0) {
        }

        if (this.client != null) {
            byteBuffer.putShort((short)this.clientLen);
            byteBuffer.put(clientByte);
        }

        byteBuffer.putInt(this.random());
        byteBuffer.put(sessionByte);
        byteBuffer.putShort((short)this.sessionLen);
        byteBuffer.flip();
        byte[] handleBuffer = new byte[this.nameLen + this.passwordLen + this.clientTypeLen + this.androidIdLen + this.sessionLen + 14];
        byteBuffer.get(handleBuffer);
        byte[] buffer = this.bigToSmall(handleBuffer);
        byte[] crcByte = Crc.getCrc(buffer);
        byteBuffer.clear();
        byteBuffer.put(buffer);
        byteBuffer.put(crcByte);
        byteBuffer.putInt(this.random());
        byteBuffer.flip();
        byte[] buffer1 = new byte[buffer.length + crcByte.length + 4];
        byteBuffer.get(buffer1);
        return buffer1;
    }

    private int random() {
        Random random = new Random();
        return random.nextInt();
    }

    private byte[] bigToSmall(byte[] data) {
        byte temp = data[0];
        data[0] = data[1];
        data[1] = temp;
        temp = data[this.nameLen + 2];
        data[this.nameLen + 2] = data[this.nameLen + 3];
        data[this.nameLen + 3] = temp;
        temp = data[this.nameLen + this.passwordLen + 4];
        data[this.nameLen + this.passwordLen + 4] = data[this.nameLen + this.passwordLen + 5];
        data[this.nameLen + this.passwordLen + 5] = temp;
        temp = data[this.nameLen + this.passwordLen + this.clientTypeLen + 6];
        data[this.nameLen + this.passwordLen + this.clientTypeLen + 6] = data[this.nameLen + this.passwordLen + this.clientTypeLen + 7];
        data[this.nameLen + this.passwordLen + this.clientTypeLen + 7] = temp;
        temp = data[this.nameLen + this.passwordLen + this.clientTypeLen + this.androidIdLen + 8];
        data[this.nameLen + this.passwordLen + this.clientTypeLen + this.androidIdLen + 8] = data[this.nameLen + this.passwordLen + this.clientTypeLen + this.androidIdLen + 9];
        data[this.nameLen + this.passwordLen + this.clientTypeLen + this.androidIdLen + 9] = temp;
        if (this.client != null) {
            temp = data[this.nameLen + this.passwordLen + this.clientLen + 6];
            data[this.nameLen + this.passwordLen + this.clientLen + 6] = data[this.nameLen + this.passwordLen + this.clientLen + 7];
            data[this.nameLen + this.passwordLen + this.clientLen + 7] = temp;
        }

        temp = data[data.length - 2];
        data[data.length - 2] = data[data.length - 1];
        data[data.length - 1] = temp;
        return data;
    }
}
