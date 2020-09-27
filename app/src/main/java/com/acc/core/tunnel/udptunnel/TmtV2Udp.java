//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tunnel.udptunnel;

import android.net.VpnService;
import com.acc.core.TmtV2VpnService;
import com.acc.core.TunConfig;
import com.acc.core.helper.ThreadPool;
import com.acc.core.helper.Util;
import com.acc.core.interfaces.IDelayCallback;
import com.acc.core.tunnel.TmtV2Tunnel;
import com.tea.encrypt.Encrypt;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class TmtV2Udp extends TmtV2Tunnel implements Runnable {
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket = new DatagramPacket(new byte[0], 0);
    private DatagramPacket receiveDatagramPacket = new DatagramPacket(new byte[0], 0);
    private int port;
    private String ip;
    private OutputStream outputStream;
    private byte[] sendBuffer;
    private byte[] receiveBuffer;
    private InetAddress inetAddress;
    private String key;
    private String session;
    private Encrypt encrypt;
    private TmtV2Udp.ReadHeader readHeader;
    private Timer sendHeartPacketTimer;
    private TimerTask sendHeartPacketTimerTask;
    private Timer readTimeOutTimer;
    private TimerTask readTimeOutTimerTask;
    private VpnService vpnService;
    private IDelayCallback mDelayCallback;
    private long SEND_TIME = 0L;

    public TmtV2Udp(DatagramSocket mDatagramSocket, String ip, int port, OutputStream outputStream, String key, String session, VpnService vpnService, IDelayCallback callback) {
        this.mDatagramSocket = mDatagramSocket;
        this.port = port;
        this.ip = ip;
        this.outputStream = outputStream;
        this.key = key;
        this.session = session;
        this.vpnService = vpnService;
        this.mDelayCallback = callback;

        try {
            this.inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException var10) {
            var10.printStackTrace();
            this.closeTunnel();
        }

        ThreadPool.submit(this);
        this.receiveBuffer = new byte[2000];
        this.encrypt = new Encrypt(this.key);
        this.readHeader = new TmtV2Udp.ReadHeader();
        this.openTunnel();
        this.sendHeartPacketLoop();
    }

    public void beforeWrite(ByteBuffer byteBuffer) {
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        byteBuffer.clear();
        tempBuffer = Util.buildUdpPacket(tempBuffer, this.session.getBytes());
        byteBuffer.put(tempBuffer);
        byteBuffer.flip();
        this.encrypt(byteBuffer);
    }

    public byte[] beforeWrite(byte[] buffer) {
        return new byte[0];
    }

    public void beforeWrite(ByteBuffer byteBuffer, boolean logout) {
        if (logout) {
            byte[] tempBuffer = new byte[byteBuffer.limit()];
            byteBuffer.get(tempBuffer);
            byteBuffer.clear();
            tempBuffer = Util.buildUdpPacket(tempBuffer, this.session.getBytes());
            byteBuffer.put(tempBuffer);
            byteBuffer.flip();
            this.encrypt(byteBuffer);
        } else {
            this.beforeWrite(byteBuffer);
        }

    }

    public void afterRead(ByteBuffer byteBuffer) {
        this.decrypt(byteBuffer);
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        byteBuffer.clear();
        Util.unpackUdpPacket(tempBuffer, this.readHeader);
        if (this.isLogout(this.readHeader.session)) {
            this.closeTunnel();
            ((TmtV2VpnService)this.vpnService).onTunnelCallback(301, (TunConfig)null, true, 0);
        } else {
            if (this.readHeader.buffer.length == 8) {
                if (this.isHeartPacket(this.readHeader.buffer)) {
                    if (this.mDelayCallback != null) {
                        this.mDelayCallback.onDelayCallback((int)(System.currentTimeMillis() - this.SEND_TIME));
                    }

                    byteBuffer.limit(0);
                    return;
                }
            } else {
                byteBuffer.put(this.readHeader.buffer);
                byteBuffer.flip();
            }

        }
    }

    public void decrypt(ByteBuffer byteBuffer) {
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        tempBuffer = this.encrypt.decrypt(tempBuffer, 4);
        byteBuffer.clear();
        byteBuffer.put(tempBuffer);
        byteBuffer.flip();
    }

    public void encrypt(ByteBuffer byteBuffer) {
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        tempBuffer = this.encrypt.encrypt(tempBuffer, 4);
        byteBuffer.clear();
        byteBuffer.put(tempBuffer);
        byteBuffer.flip();
    }

    public byte[] encrypt(byte[] buffer) {
        return null;
    }

    public void connect() {
    }

    public void retryConnect() {
    }

    public void changeOutputStream(OutputStream vpnOutputStream) {
    }

    public void exit() {
    }

    public synchronized Object write(ByteBuffer byteBuffer) {
        if (this.getTunnelStatute() && this.getTunnelSwitchStatute()) {
            this.beforeWrite(byteBuffer);
            this.sendBuffer = new byte[byteBuffer.limit()];
            byteBuffer.get(this.sendBuffer);
            this.mDatagramPacket.setPort(this.port);
            this.mDatagramPacket.setAddress(this.inetAddress);
            this.mDatagramPacket.setData(this.sendBuffer, 0, byteBuffer.limit());

            try {
                if (this.mDatagramSocket != null && !this.mDatagramSocket.isClosed() && this.getTunnelSwitchStatute()) {
                    this.mDatagramSocket.send(this.mDatagramPacket);
                }
            } catch (IOException var3) {
                var3.printStackTrace();
            }

            byteBuffer.clear();
        }

        return null;
    }

    public Object write(byte[] buffer) {
        return null;
    }

    public Object write(ByteBuffer byteBuffer, boolean logout) {
        if (logout) {
            if (this.getTunnelStatute() && this.getTunnelSwitchStatute()) {
                this.beforeWrite(byteBuffer, logout);
                this.sendBuffer = new byte[byteBuffer.limit()];
                byteBuffer.get(this.sendBuffer);
                this.mDatagramPacket.setPort(this.port);
                this.mDatagramPacket.setAddress(this.inetAddress);
                this.mDatagramPacket.setData(this.sendBuffer, 0, byteBuffer.limit());

                try {
                    if (!this.mDatagramSocket.isClosed() && this.getTunnelSwitchStatute()) {
                        this.mDatagramSocket.send(this.mDatagramPacket);
                    }
                } catch (IOException var4) {
                    var4.printStackTrace();
                }

                byteBuffer.clear();
            }
        } else {
            this.write(byteBuffer);
        }

        return null;
    }

    public Object read(ByteBuffer byteBuffer) {
        byteBuffer.clear();
        if (this.getTunnelStatute() && this.getTunnelSwitchStatute()) {
            this.receiveDatagramPacket.setData(this.receiveBuffer);

            try {
                if (!this.mDatagramSocket.isClosed()) {
                    this.readTimeOut();
                    this.mDatagramSocket.receive(this.receiveDatagramPacket);
                    this.cancelReadTimeOut();
                }

                int length = this.receiveDatagramPacket.getLength();
                byteBuffer.put(this.receiveBuffer, 0, length);
                byteBuffer.flip();
                this.afterRead(byteBuffer);
                if (byteBuffer.limit() != 0) {
                    byte[] tempBuffer = new byte[byteBuffer.limit()];
                    byteBuffer.get(tempBuffer);
                    this.outputStream.write(tempBuffer);
                    byteBuffer.clear();
                }
            } catch (IOException var4) {
                var4.printStackTrace();
                this.closeTunnel();
            }
        }

        return null;
    }

    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2000);

        while(this.getTunnelStatute()) {
            while(!this.getTunnelSwitchStatute()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }
            }

            this.read(byteBuffer);
        }

    }

    public void closeTunnel() {
        super.closeTunnel();
        this.cancelReadTimeOut();
        this.cancelSendHeartPacket();
        if (this.mDatagramSocket != null && !this.mDatagramSocket.isClosed()) {
            this.mDatagramSocket.close();
            this.mDatagramSocket = null;
        }

        if (this.encrypt != null) {
            this.encrypt.destroy();
        }

        ((TmtV2VpnService)this.vpnService).onTunnelCallback(550, (TunConfig)null, true, 0);
    }

    public void logOutTunnel() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(200);
        byteBuffer.put(Util.shortToByte((short)this.session.getBytes().length, true));
        byteBuffer.put(this.session.getBytes());
        byteBuffer.flip();
        this.write(byteBuffer, true);
    }

    private void sendHeartPacket() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(200);
        byteBuffer.put(Util.checkHandle);
        byteBuffer.flip();
        this.write(byteBuffer);
        this.SEND_TIME = System.currentTimeMillis();
    }

    public void sendHeartPacketLoop() {
        this.sendHeartPacketTimer = new Timer();
        this.sendHeartPacketTimerTask = new TimerTask() {
            public void run() {
                TmtV2Udp.this.sendHeartPacket();
            }
        };
        this.sendHeartPacketTimer.schedule(this.sendHeartPacketTimerTask, 1000L, 3000L);
    }

    public void readTimeOut() {
        if (this.readTimeOutTimer == null) {
            this.readTimeOutTimer = new Timer();
        }

        this.readTimeOutTimerTask = new TimerTask() {
            public void run() {
                TmtV2Udp.this.closeTunnel();
                ((TmtV2VpnService)TmtV2Udp.this.vpnService).onTunnelCallback(500, (TunConfig)null, true, 0);
            }
        };
        this.readTimeOutTimer.schedule(this.readTimeOutTimerTask, 30000L);
    }

    public void cancelReadTimeOut() {
        if (this.readTimeOutTimer != null) {
            this.readTimeOutTimer.cancel();
            this.readTimeOutTimer = null;
        }

        if (this.readTimeOutTimerTask != null) {
            this.readTimeOutTimerTask.cancel();
            this.readTimeOutTimerTask = null;
        }

    }

    public void cancelSendHeartPacket() {
        if (this.sendHeartPacketTimer != null) {
            this.sendHeartPacketTimer.cancel();
            this.sendHeartPacketTimer = null;
        }

        if (this.sendHeartPacketTimerTask != null) {
            this.sendHeartPacketTimerTask.cancel();
            this.sendHeartPacketTimerTask = null;
        }

    }

    public static class ReadHeader {
        public int crc;
        public byte[] session;
        public byte[] buffer;

        public ReadHeader() {
        }
    }
}
