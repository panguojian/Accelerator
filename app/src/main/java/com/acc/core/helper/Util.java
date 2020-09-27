//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.tunnel.udptunnel.TmtV2Udp.ReadHeader;
import com.tea.Crc;
import java.nio.ByteBuffer;
import java.util.Random;

public class Util {
    public static final byte[] checkHandle = new byte[]{-7, -121, 9, -119, 103, 69, 35, 33};
    private static ByteBuffer byteBuffer = ByteBuffer.allocate(8000);

    public Util() {
    }

    public static short bytesToShort(byte[] data, int offset, boolean isChange) {
        if (isChange) {
            byte temp = data[offset];
            data[offset] = data[offset + 1];
            data[offset + 1] = temp;
        }

        return (short)((data[offset] & 255) << 8 | data[offset + 1] & 255);
    }

    public static byte[] shortToByte(short s, boolean isChange) {
        byte[] temp = new byte[2];
        if (isChange) {
            temp[1] = (byte)(s >> 8 & 255);
            temp[0] = (byte)(s & 255);
        } else {
            temp[0] = (byte)(s >> 8 & 255);
            temp[1] = (byte)(s & 255);
        }

        return temp;
    }

    public static String bytesGetString(byte[] data, int offset, int length) {
        byte[] strByte = new byte[length];
        System.arraycopy(data, offset, strByte, 0, length);
        return new String(strByte);
    }

    public static byte[] buildTcpPacket(byte[] data) {
        byte[] byteLength = shortToByte((short)data.length, true);
        byte[] temp = new byte[byteLength.length + data.length];
        System.arraycopy(byteLength, 0, temp, 0, byteLength.length);
        System.arraycopy(data, 0, temp, byteLength.length, data.length);
        return temp;
    }

    public static byte[] buildUdpPacket(byte[] data, byte[] session) {
        byteBuffer.clear();
        byteBuffer.put(data);
        byteBuffer.put(session);
        byteBuffer.put(shortToByte((short)session.length, true));
        byteBuffer.flip();
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        byteBuffer.clear();
        byte[] crc = Crc.getCrc(tempBuffer);
        byteBuffer.put(tempBuffer);
        byteBuffer.put(crc);
        byteBuffer.putInt(random());
        byteBuffer.flip();
        tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        return tempBuffer;
    }

    public static void unpackUdpPacket(byte[] data, ReadHeader readHeader) {
        byte[] crc = new byte[4];
        System.arraycopy(data, data.length - 8, crc, 0, 4);
        readHeader.crc = byteToInt(crc);
        byte[] len = new byte[2];
        System.arraycopy(data, data.length - 10, len, 0, 2);
        int length = bytesToShort(len, 0, true);
        byte[] sessionBuffer = new byte[length];
        System.arraycopy(data, data.length - 10 - length, sessionBuffer, 0, length);
        readHeader.session = sessionBuffer;
        byte[] tempBuffer = new byte[data.length - 10 - length];
        System.arraycopy(data, 0, tempBuffer, 0, tempBuffer.length);
        readHeader.buffer = tempBuffer;
    }

    public static int byteToInt(byte[] data) {
        return data[3] << 24 & '\uffff' | data[2] << 16 & '\uffff' | data[1] << 8 & '\uffff' | data[0] & '\uffff';
    }

    public static int random() {
        Random random = new Random();
        return random.nextInt();
    }

    public static String randomString() {
        String str = "0123456789abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWSYZ";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0; i < 100; ++i) {
            int randomInt = random.nextInt(str.length());
            stringBuffer.append(str.charAt(randomInt));
        }

        String randomStr = stringBuffer.toString();
        return randomStr;
    }
}
