//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import android.text.TextUtils;
import android.util.Log;
import com.acc.core.helper.Util;
import com.acc.core.tunnel.udptunnel.TmtV2Udp.ReadHeader;
import com.acc.core.util.LogUtils;
import com.tea.MD5;
import com.tea.encrypt.Encrypt;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoginVerify {
    private String TAG = "LoginVerify";
    private Socket socket;
    private DatagramPacket mDatagramPacket;
    private DatagramSocket mDatagramSocket;
    private Encrypt encrypt;
    private LoginVerify.ICallback verifyCallback;
    private AtomicBoolean mIsStartLogin = new AtomicBoolean(false);

    public LoginVerify() {
    }

    public void setVpnStatusCallback(LoginVerify.ICallback verifyCallback) {
        this.verifyCallback = verifyCallback;
    }

    public void stopLogin() {
        Log.e("stopLogin", "stopLogin");
        if (this.mIsStartLogin.get()) {
            if (this.mDatagramSocket != null) {
                this.mDatagramSocket.close();
            }

            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

    public void login(String name, String password, String host, int port, String key, boolean isUdp) {
        if (isUdp) {
            this.loginVpnUdp(name, password, host, port, key);
        } else {
            this.loginVpnTcp(name, password, host, port, key);
        }

    }

    private void loginVpnUdp(String name, String password, String host, int port, String key) {
        Long startTime = System.currentTimeMillis();
        this.mIsStartLogin.set(true);
        this.encrypt = new Encrypt(key);
        String enPassword = this.encryptPassword(password);
        byte[] dataBuffer = this.genUdpLoginPacket(name, enPassword);
        dataBuffer = this.encrypt.encrypt(dataBuffer, 4);
        byte[] len = new byte[2];

        try {
            this.mDatagramSocket = new DatagramSocket(0);
            this.mDatagramSocket.setSoTimeout(3000);
            this.mDatagramPacket = new DatagramPacket(dataBuffer, dataBuffer.length, InetAddress.getByName(host), port);
            this.mDatagramSocket.send(this.mDatagramPacket);

            while(true) {
                byte[] receiveData = new byte[128];
                this.mDatagramPacket.setData(receiveData);
                this.mDatagramSocket.receive(this.mDatagramPacket);
                byte[] tempBuffer = this.mDatagramPacket.getData();
                receiveData = new byte[this.mDatagramPacket.getLength()];
                System.arraycopy(tempBuffer, 0, receiveData, 0, receiveData.length);
                receiveData = this.encrypt.decrypt(receiveData, 4);
                System.arraycopy(receiveData, 0, len, 0, 2);
                short length = Util.bytesToShort(len, 0, true);
                byte[] tagBuffer = new byte[length];
                System.arraycopy(receiveData, 2, tagBuffer, 0, length);
                String tag = new String(tagBuffer);
                LogUtils.INSTANCE.v(this.TAG, "Login tag==>" + tag);
                if (tag.equals("login")) {
                    TunConfig tunConfig = this.analyzeTunConfigData(receiveData, length);
                    boolean portVerify = this.checkPort(tunConfig);
                    if (portVerify) {
                        if (this.verifyCallback != null) {
                            this.verifyCallback.verifyCallback(200, tunConfig, true);
                        }
                    } else {
                        this.loginVpnUdp(name, password, host, port, key);
                    }
                    break;
                }

                if (tag.equals("false")) {
                    if (this.verifyCallback != null) {
                        this.verifyCallback.verifyCallback(201, (TunConfig)null, true);
                    }
                    break;
                }

                if (tag.equals("88888888")) {
                }
            }
        } catch (SocketException var22) {
            var22.printStackTrace();
            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(500, (TunConfig)null, true);
            }
        } catch (UnknownHostException var23) {
            var23.printStackTrace();
            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(502, (TunConfig)null, true);
            }
        } catch (IOException var24) {
            var24.printStackTrace();
            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(502, (TunConfig)null, true);
            }
        } finally {
            this.mDatagramSocket.close();
            this.encrypt.destroy();
        }

        Long endTime = System.currentTimeMillis();
        Long duration = endTime - startTime;
        LogUtils.INSTANCE.v(this.TAG, "Duration:" + duration + " Start:" + startTime + " End:" + endTime);
    }

    private void loginVpnTcp(String name, String password, String host, int port, String key) {
        this.mIsStartLogin.set(true);
        this.encrypt = new Encrypt(key);
        byte[] dataBuffer = this.genTcpLoginPacket(name, password);
        dataBuffer = this.encrypt.encrypt(dataBuffer, 8);
        this.socket = new Socket();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        byte[] readData = new byte[512];
        byte[] cmd = new byte[8];
        byte[] len = new byte[8];

        try {
            this.socket.setKeepAlive(false);
            this.socket.setSoTimeout(1000);
            this.socket.connect(new InetSocketAddress(host, port), 3000);
            outputStream = this.socket.getOutputStream();
            inputStream = this.socket.getInputStream();
            outputStream.write(dataBuffer);
            outputStream.flush();
            int length = inputStream.read(readData);
            if (length != -1) {
                byte[] getData = new byte[length];
                System.arraycopy(readData, 0, getData, 0, length);
                getData = this.encrypt.decrypt(getData, 8);
                System.arraycopy(getData, 0, cmd, 0, cmd.length);
                System.arraycopy(getData, 10, len, 0, 2);
                int length = Util.bytesToShort(len, 0, true);
                byte[] tag = new byte[length];
                System.arraycopy(getData, 12, tag, 0, length);
                String loginTag = new String(tag);
                cmd = this.encrypt.decrypt(cmd, 1);
                switch(cmd[0]) {
                    case 0:
                        if (TextUtils.equals(loginTag, "login")) {
                            TunConfig tunConfig = this.analyzeTunConfigData(getData, (short)(10 + length));
                            if (this.verifyCallback != null) {
                                this.verifyCallback.verifyCallback(200, tunConfig, false);
                                return;
                            }
                        } else if (TextUtils.equals(loginTag, "false") && this.verifyCallback != null) {
                            this.verifyCallback.verifyCallback(201, (TunConfig)null, false);
                            return;
                        }

                        return;
                    case 2:
                        if (this.verifyCallback != null) {
                            this.verifyCallback.verifyCallback(202, (TunConfig)null, false);
                        }

                        return;
                    default:
                        return;
                }
            }

            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(502, (TunConfig)null, false);
            }

            try {
                this.socket.close();
            } catch (IOException var29) {
                var29.printStackTrace();
            }

            this.encrypt.destroy();
        } catch (SocketException var30) {
            var30.printStackTrace();
            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(502, (TunConfig)null, false);
            }

            return;
        } catch (IOException var31) {
            var31.printStackTrace();
            if (this.verifyCallback != null) {
                this.verifyCallback.verifyCallback(502, (TunConfig)null, false);
            }

            return;
        } finally {
            try {
                this.socket.close();
            } catch (IOException var28) {
                var28.printStackTrace();
            }

            this.encrypt.destroy();
        }

    }

    private byte[] genTcpLoginPacket(String name, String password) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        TmtV2TcpHead tmtV2TcpHander = new TmtV2TcpHead();
        tmtV2TcpHander.name = "*login*".getBytes();
        tmtV2TcpHander.password = null;
        tmtV2TcpHander.url = null;
        tmtV2TcpHander.nameLen = (short)"*login*".getBytes().length;
        tmtV2TcpHander.genCmd(1);
        tmtV2TcpHander.cmd = this.encrypt.encrypt(tmtV2TcpHander.cmd, 1);
        int nameLen = name.getBytes().length;
        int passLen = password.getBytes().length;
        byteBuffer.put(Util.shortToByte((short)nameLen, true));
        byteBuffer.put(name.getBytes());
        byteBuffer.put(Util.shortToByte((short)passLen, true));
        byteBuffer.put(password.getBytes());
        byteBuffer.put(Util.shortToByte((short)"android".getBytes().length, true));
        byteBuffer.put("android".getBytes());
        if (!LauncherTmtV2.AndroidId.isEmpty()) {
            byteBuffer.put(Util.shortToByte((short)LauncherTmtV2.AndroidId.getBytes().length, true));
            byteBuffer.put(LauncherTmtV2.AndroidId.getBytes());
        }

        byteBuffer.put(Util.randomString().getBytes());
        byteBuffer.flip();
        byte[] buffer = new byte[byteBuffer.limit()];
        byteBuffer.get(buffer);
        byteBuffer.clear();
        byteBuffer.put(Util.shortToByte((short)buffer.length, true));
        byteBuffer.put(buffer);
        byteBuffer.flip();
        buffer = new byte[byteBuffer.limit()];
        byteBuffer.get(buffer);
        byteBuffer.clear();
        byteBuffer.put(tmtV2TcpHander.getHanderByte());
        byteBuffer.put(buffer);
        byteBuffer.flip();
        buffer = new byte[byteBuffer.limit()];
        byteBuffer.get(buffer);
        byteBuffer.clear();
        return buffer;
    }

    private boolean checkPort(TunConfig tunConfig) {
        byte[] checkBuffer = Util.buildUdpPacket(Util.checkHandle, tunConfig.session.getBytes());
        checkBuffer = this.encrypt.encrypt(checkBuffer, 4);

        try {
            this.mDatagramSocket = new DatagramSocket(0);
            this.mDatagramSocket.setSoTimeout(3000);
            this.mDatagramPacket.setData(checkBuffer);
            this.mDatagramPacket.setPort(Integer.parseInt(tunConfig.port));
            this.mDatagramSocket.send(this.mDatagramPacket);
            this.mDatagramSocket.receive(this.mDatagramPacket);
            byte[] tempBuffer = this.mDatagramPacket.getData();
            checkBuffer = new byte[this.mDatagramPacket.getLength()];
            System.arraycopy(tempBuffer, 0, checkBuffer, 0, checkBuffer.length);
            checkBuffer = this.encrypt.decrypt(checkBuffer, 4);
            ReadHeader readHeader = new ReadHeader();
            Util.unpackUdpPacket(checkBuffer, readHeader);

            for(int i = 0; i < 8; ++i) {
                if (Util.checkHandle[i] != readHeader.buffer[i]) {
                    this.mDatagramSocket.close();
                    return false;
                }
            }

            this.mDatagramSocket.close();
            return true;
        } catch (SocketException var6) {
            var6.printStackTrace();
            return false;
        } catch (IOException var7) {
            var7.printStackTrace();
            return false;
        }
    }

    private TunConfig analyzeTunConfigData(byte[] data, short length) {
        TunConfig tunConfig = new TunConfig();
        short sessionLen = Util.bytesToShort(data, length + 2, true);
        String session = Util.bytesGetString(data, length + 4, sessionLen);
        tunConfig.session = session;
        short portLen = Util.bytesToShort(data, length + 4 + sessionLen, true);
        String port = Util.bytesGetString(data, length + 6 + sessionLen, portLen);
        tunConfig.port = port;
        short ipLen = Util.bytesToShort(data, length + 6 + sessionLen + portLen, true);
        String ip = Util.bytesGetString(data, length + 8 + sessionLen + portLen, ipLen);
        tunConfig.ip = ip;
        short gatewayLen = Util.bytesToShort(data, length + 8 + sessionLen + portLen + ipLen, true);
        String gateway = Util.bytesGetString(data, length + 10 + sessionLen + portLen + ipLen, gatewayLen);
        tunConfig.gateway = gateway;
        String[] dnss = new String[2];
        short dns1Len = Util.bytesToShort(data, length + 10 + sessionLen + portLen + ipLen + gatewayLen, true);
        String dns1 = Util.bytesGetString(data, length + 12 + sessionLen + portLen + ipLen + gatewayLen, dns1Len);
        dnss[0] = dns1;
        short dns2Len = Util.bytesToShort(data, length + 12 + sessionLen + portLen + ipLen + gatewayLen + dns1Len, true);
        String dns2 = Util.bytesGetString(data, length + 14 + sessionLen + portLen + ipLen + gatewayLen + dns1Len, dns2Len);
        dnss[1] = dns2;
        tunConfig.dns = dnss;
        return tunConfig;
    }

    private byte[] genUdpLoginPacket(String name, String password) {
        TmtV2Packet tmtV2Packet = new TmtV2Packet();
        tmtV2Packet.name = name;
        tmtV2Packet.password = password;
        tmtV2Packet.session = "*login*";
        return tmtV2Packet.getTmtV2UdpPacketHandle();
    }

    private String encryptPassword(String password) {
        String encryptPassword = MD5.MD5(password);
        encryptPassword = encryptPassword + "dongjing";
        encryptPassword = MD5.MD5(encryptPassword);
        return encryptPassword;
    }

    public interface ICallback {
        void verifyCallback(int var1, TunConfig var2, boolean var3);
    }
}
