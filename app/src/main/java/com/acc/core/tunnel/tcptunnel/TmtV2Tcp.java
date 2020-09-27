//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tunnel.tcptunnel;

import android.net.VpnService;
import com.acc.core.TmtV2TcpHead;
import com.acc.core.TmtV2VpnService;
import com.acc.core.TunConfig;
import com.acc.core.dns.DnsParser;
import com.acc.core.dns.DnsParser.DnsResponseData;
import com.acc.core.helper.ThreadPool;
import com.acc.core.helper.Util;
import com.acc.core.interfaces.IDelayCallback;
import com.acc.core.manager.GameUpdateRuleManager;
import com.acc.core.tcpip.CommonMethods;
import com.acc.core.tcpip.IPHeader;
import com.acc.core.tcpip.TCPHeader;
import com.acc.core.tcpip.UDPHeader;
import com.acc.core.tunnel.TmtV2Tunnel;
import com.acc.core.util.LogUtils;
import com.tea.encrypt.Encrypt;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class TmtV2Tcp extends TmtV2Tunnel implements Runnable {
    private static final String TAG = "TmtV2Tcp";
    private static final int ReTryConnectMaxCount = 1000;
    private int retryConnectCount = 0;
    private InetSocketAddress inetAddress;
    private VpnService vpnService;
    private String key;
    private OutputStream vpnOutputStream;
    private InputStream socketInputStream;
    private OutputStream socketOutputStream;
    private String session;
    private Socket socket;
    private Encrypt encrypt;
    private AtomicBoolean isReadCmdAtomic;
    private AtomicBoolean canReConnectAtomic;
    private TmtV2TcpHead tmtV2TcpHead;
    private Timer sendHeartPacketTimer;
    private Timer readTimeOutTimer;
    private TimerTask readTimeOutTimerTask;
    private Timer reconnectTimer;
    private TimerTask reconnectTimerTask;
    private IDelayCallback mDelayCallback;
    private int mId = 0;
    private boolean mExitFlag = false;
    private long SEND_TIME = 0L;
    private byte[] len = new byte[2];
    private byte[] dataBuffer;

    public TmtV2Tcp(String ip, int port, OutputStream vpnOutputStream, String key, String session, VpnService vpnService, IDelayCallback callback, int id) {
        this.mId = id;
        this.vpnService = vpnService;
        this.key = key;
        this.vpnOutputStream = vpnOutputStream;
        this.session = session;
        this.inetAddress = new InetSocketAddress(ip, port);
        this.isReadCmdAtomic = new AtomicBoolean(false);
        this.tmtV2TcpHead = new TmtV2TcpHead();
        this.mDelayCallback = callback;
    }

    public void beforeWrite(ByteBuffer byteBuffer) {
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        tempBuffer = Util.buildTcpPacket(tempBuffer);
        byteBuffer.clear();
        byteBuffer.put(tempBuffer);
        byteBuffer.flip();
        this.encrypt(byteBuffer);
    }

    public byte[] beforeWrite(byte[] buffer) {
        byte[] tempBuffer = Util.buildTcpPacket(buffer);
        byte[] data = this.encrypt(tempBuffer);
        return data;
    }

    public void afterRead(ByteBuffer byteBuffer) {
    }

    public void readTimeOut() {
        if (this.readTimeOutTimer == null) {
            this.readTimeOutTimer = new Timer();
        }

        this.readTimeOutTimerTask = new TimerTask() {
            public void run() {
                try {
                    if (TmtV2Tcp.this.socket != null) {
                        TmtV2Tcp.this.socket.close();
                    }
                } catch (IOException var2) {
                    var2.printStackTrace();
                }

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

    private void sendHeartPacket() {
        this.write(Util.checkHandle);
        this.SEND_TIME = System.currentTimeMillis();
    }

    public void sendHeartPacketLoop() {
        this.sendHeartPacketTimer = new Timer();
        TimerTask sendHeartPacketTimerTask = new TimerTask() {
            public void run() {
                TmtV2Tcp.this.sendHeartPacket();
            }
        };
        if (this.isDownload()) {
            this.sendHeartPacketTimer.schedule(sendHeartPacketTimerTask, 10000L, 10000L);
        } else {
            this.sendHeartPacketTimer.schedule(sendHeartPacketTimerTask, 1000L, 3000L);
        }

    }

    public void cancelSendHeartPacket() {
        if (this.sendHeartPacketTimer != null) {
            this.sendHeartPacketTimer.cancel();
            this.sendHeartPacketTimer = null;
        }

    }

    public void logOutTunnel() {
        LogUtils.INSTANCE.v("TmtV2Tcp", "logOutTunnel");
        this.wirteLog("发送logout");
        this.write(this.logoutByte);
    }

    public void decrypt(ByteBuffer byteBuffer) {
    }

    public byte[] encrypt(byte[] buffer) {
        byte[] tempBuffer = this.encrypt.encrypt(buffer, 8);
        return tempBuffer;
    }

    public void encrypt(ByteBuffer byteBuffer) {
        byte[] tempBuffer = new byte[byteBuffer.limit()];
        byteBuffer.get(tempBuffer);
        tempBuffer = this.encrypt.encrypt(tempBuffer, 8);
        byteBuffer.clear();
        byteBuffer.put(tempBuffer);
        byteBuffer.flip();
    }

    public void wirteLog(String text) {
        String title = this.isDownload() ? " 下 " : " 主 ";
    }

    public boolean connect(boolean canTry) {
        this.socket = new Socket();

        try {
            this.socket.setKeepAlive(true);
            this.vpnService.protect(this.socket);
            if (this.socket != null && this.inetAddress != null) {
                this.wirteLog("socket 连接,ip:" + this.inetAddress.getAddress().getHostName() + " port:" + this.inetAddress.getPort());
                this.socket.connect(this.inetAddress, 3000);
                if (this.socket != null) {
                    this.socket.setTcpNoDelay(true);
                }

                this.openTunnel();
                if (this.socket == null) {
                    return false;
                } else {
                    this.socketInputStream = this.socket.getInputStream();
                    if (this.socketInputStream == null) {
                        return false;
                    } else {
                        this.socketOutputStream = this.socket.getOutputStream();
                        this.encrypt = new Encrypt(this.key);
                        this.isReadCmdAtomic.set(false);
                        ThreadPool.submit(this);
                        this.wirteLog("连接成功");
                        this.onConnected();
                        this.retryConnectCount = 0;
                        this.sendHeartPacketLoop();
                        return true;
                    }
                }
            } else {
                return false;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
            LogUtils.INSTANCE.v("TmtV2Tcp", " connect " + var3.getMessage());
            if (canTry) {
                this.tunnelException();
            }

            return false;
        }
    }

    public void connect() {
        this.connect(true);
    }

    private void onConnected() {
        this.tmtV2TcpHead.genCmd(1);
        this.tmtV2TcpHead.name = this.session.getBytes();
        this.tmtV2TcpHead.nameLen = (short)this.session.getBytes().length;
        this.tmtV2TcpHead.url = null;
        this.tmtV2TcpHead.password = null;
        this.tmtV2TcpHead.port = 0;
        this.tmtV2TcpHead.cmd = this.encrypt.encrypt(this.tmtV2TcpHead.cmd, 1);
        byte[] headBuffer = this.encrypt.encrypt(this.tmtV2TcpHead.getHanderByte(), 8);

        try {
            this.socketOutputStream.write(headBuffer);
            this.socketOutputStream.flush();
        } catch (IOException var3) {
            var3.printStackTrace();
            LogUtils.INSTANCE.v("TmtV2Tcp", "onConnected " + var3.getMessage());
        }

    }

    public boolean isDownload() {
        if (!TmtV2VpnService.ENABLE_DOWNLOAD) {
            return false;
        } else {
            return this.mId != 0;
        }
    }

    public void retryConnect() {
        LogUtils.INSTANCE.v("TmtV2Tcp", "retryConnect");
        this.reconnectTimer = new Timer();
        this.reconnectTimerTask = new TimerTask() {
            public void run() {
                TmtV2Tcp.this.wirteLog("准备重连");
                LogUtils.INSTANCE.v("TmtV2Tcp", "retryConnect");
                TmtV2Tcp.this.connect();
                TmtV2Tcp.this.retryConnectCount++;
            }
        };
        this.reconnectTimer.schedule(this.reconnectTimerTask, 1000L);
    }

    public void changeOutputStream(OutputStream vpnOutputStream) {
        this.vpnOutputStream = vpnOutputStream;
    }

    public synchronized Object write(byte[] buffer) {
        if (this.getTunnelStatute() && this.getTunnelSwitchStatute()) {
            byte[] data = this.beforeWrite(buffer);

            try {
                if (!this.socket.isClosed()) {
                    if (this.getTunnelStatute()) {
                        this.socketOutputStream.write(data);
                        this.socketOutputStream.flush();
                    }
                } else {
                    this.tunnelException();
                }
            } catch (IOException var4) {
                var4.printStackTrace();
                this.tunnelException();
            }
        }

        return null;
    }

    private void tunnelException() {
        LogUtils.INSTANCE.v("TmtV2Tcp", "tunnelException");
        this.closeTunnel();
        if (this.retryConnectCount < 1000 && !this.isForceBroke.get()) {
            this.retryConnect();
        } else if (this.canReConnectAtomic.get()) {
            ((TmtV2VpnService)this.vpnService).onTunnelCallback(502, (TunConfig)null, false, this.mId);
        }

    }

    public Object read(ByteBuffer byteBuffer) {
        byteBuffer.clear();

        try {
            while(this.getTunnelSwitchStatute()) {
                while(!this.getTunnelStatute()) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var7) {
                        var7.printStackTrace();
                    }
                }

                if (this.getTunnelSwitchStatute() && this.getTunnelStatute()) {
                    int length;
                    if (!this.isReadCmdAtomic.get()) {
                        this.isReadCmdAtomic.set(true);
                        byte[] cmd = new byte[8];
                        length = 0;
                        if (!this.socket.isClosed()) {
                            if (!this.getTunnelSwitchStatute()) {
                                return null;
                            }

                            this.readTimeOut();
                            length = this.socketInputStream.read(cmd, 0, 8);
                            this.cancelReadTimeOut();
                        } else {
                            this.tunnelException();
                        }

                        if (length == -1) {
                            return null;
                        }

                        cmd = this.encrypt.decrypt(cmd, 8);
                        cmd = this.encrypt.decrypt(cmd, 1);
                        if (cmd[0] != 0) {
                            this.wirteLog("cmd[0] != 0");
                            this.closeTunnel();
                            ((TmtV2VpnService)this.vpnService).onTunnelCallback(502, (TunConfig)null, false, this.mId);
                        } else if (cmd[0] == 2) {
                            this.wirteLog("cmd[0] == 2");
                            this.closeTunnel();
                            ((TmtV2VpnService)this.vpnService).onTunnelCallback(202, (TunConfig)null, false, this.mId);
                        }

                        if (cmd[0] > 2) {
                        }
                    } else {
                        int size = 0;
                        if (!this.socket.isClosed()) {
                            if (!this.getTunnelSwitchStatute()) {
                                return null;
                            }

                            this.readTimeOut();
                            size = this.socketInputStream.read(this.len, 0, 2);
                            this.cancelReadTimeOut();
                        } else {
                            this.tunnelException();
                        }

                        if (size == -1) {
                            return null;
                        }

                        for(length = 0; size != 2; size += length) {
                            byte[] temp = new byte[2 - size];
                            if (!this.socket.isClosed()) {
                                if (!this.getTunnelSwitchStatute()) {
                                    return null;
                                }

                                this.readTimeOut();
                                length = this.socketInputStream.read(temp, 0, 2 - size);
                                this.cancelReadTimeOut();
                            } else {
                                this.tunnelException();
                            }

                            if (length == -1) {
                                return null;
                            }

                            System.arraycopy(temp, 0, this.len, size, length);
                        }

                        this.len = this.encrypt.decrypt(this.len, 8);
                        length = Util.bytesToShort(this.len, 0, true);
                        this.dataBuffer = new byte[length];
                        int readLength = 0;
                        if (!this.socket.isClosed()) {
                            if (!this.getTunnelSwitchStatute()) {
                                return null;
                            }

                            this.readTimeOut();
                            readLength = this.socketInputStream.read(this.dataBuffer, 0, length);
                            this.cancelReadTimeOut();
                        } else {
                            this.tunnelException();
                        }

                        if (readLength == -1) {
                            return null;
                        }

                        for(int tempLength = 0; readLength != length; readLength += tempLength) {
                            byte[] temp = new byte[length - readLength];
                            if (!this.socket.isClosed()) {
                                if (!this.getTunnelSwitchStatute()) {
                                    return null;
                                }

                                this.readTimeOut();
                                tempLength = this.socketInputStream.read(temp, 0, length - readLength);
                                this.cancelReadTimeOut();
                            } else {
                                this.tunnelException();
                            }

                            if (tempLength == -1) {
                                return null;
                            }

                            System.arraycopy(temp, 0, this.dataBuffer, readLength, tempLength);
                        }

                        this.dataBuffer = this.encrypt.decrypt(this.dataBuffer, 8);
                        if (length == 8) {
                            if (this.isLogout(this.dataBuffer)) {
                                LogUtils.INSTANCE.v("TmtV2Tcp", "IsDownload:" + this.isDownload() + " 收到 logout: " + new String(this.dataBuffer));
                                if (!this.mExitFlag) {
                                    this.tunnelException();
                                }
                            } else if (!this.isHeartPacket(this.dataBuffer)) {
                                this.writeToApp(this.dataBuffer);
                            } else if (this.isHeartPacket(this.dataBuffer) && this.mDelayCallback != null) {
                                this.mDelayCallback.onDelayCallback((int)(System.currentTimeMillis() - this.SEND_TIME));
                            }
                        } else {
                            this.writeToApp(this.dataBuffer);
                        }
                    }
                }
            }
        } catch (IOException var8) {
            LogUtils.INSTANCE.v("TmtV2Tcp", "read " + var8.getMessage());
            var8.printStackTrace();
            this.tunnelException();
        }

        return null;
    }

    public void exit() {
        this.mExitFlag = true;
        this.logOutTunnel();
        (new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(30L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }

                TmtV2Tcp.this.retryConnectCount = 1000;
                TmtV2Tcp.this.closeTunnel();
                ((TmtV2VpnService)TmtV2Tcp.this.vpnService).onTunnelCallback(301, (TunConfig)null, false, TmtV2Tcp.this.mId);
            }
        })).start();
    }

    public void writeToApp(byte[] dataBuffer) {
        if (this.isDownload()) {
            try {
                IPHeader ipHeader = new IPHeader(dataBuffer, 0);
                TCPHeader tcpHeader = new TCPHeader(dataBuffer, 20);
                UDPHeader udpHeader = new UDPHeader(dataBuffer, 20);
                int ipInt = CommonMethods.ipStringToInt(TmtV2VpnService.ipSpeed);
                ipHeader.setDestinationIP(ipInt);
                if (ipHeader.getProtocol() == 6) {
                    CommonMethods.ComputeTCPChecksum(ipHeader, tcpHeader);
                } else if (ipHeader.getProtocol() == 17) {
                    CommonMethods.ComputeUDPChecksum(ipHeader, udpHeader);
                }

                this.vpnOutputStream.write(dataBuffer);
            } catch (IOException var7) {
                LogUtils.INSTANCE.v("TmtV2Tcp", "writeToApp " + var7.getMessage());
                var7.printStackTrace();
            }
        } else {
            try {
                DnsResponseData dnsData = DnsParser.parseResponse(dataBuffer);
                GameUpdateRuleManager.inst().recvDnsData(dnsData);
                this.vpnOutputStream.write(dataBuffer);
            } catch (IOException var6) {
                var6.printStackTrace();
                LogUtils.INSTANCE.v("TmtV2Tcp", "writeToApp " + var6.getMessage());
            }
        }

    }

    public void closeTunnel() {
        LogUtils.INSTANCE.v("TmtV2Tcp", "closeTunnel");
        this.wirteLog("closeTunnel");
        super.closeTunnel();
        this.cancelReadTimeOut();
        this.cancelSendHeartPacket();

        try {
            if (this.socket != null) {
                if (this.socket.isConnected()) {
                    this.socket.shutdownInput();
                    this.socket.shutdownOutput();
                }

                if (!this.socket.isClosed()) {
                    this.socket.close();
                    this.socket = null;
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        } finally {
            this.encrypt.destroy();
        }

    }

    public void closeTunnel(boolean cancelRetryConnect) {
        this.wirteLog("closeTunnel cancelRetryConnect:" + cancelRetryConnect);
        this.closeTunnel();
    }

    public void run() {
        LogUtils.INSTANCE.v("TmtV2Tcp", "run read begin");
        ByteBuffer byteBuffer = ByteBuffer.allocate(8000);
        this.read(byteBuffer);
        LogUtils.INSTANCE.v("TmtV2Tcp", "run read end");
    }
}
