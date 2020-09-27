//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tunnel;

import android.net.VpnService;
import com.acc.core.interfaces.IDelayCallback;
import com.acc.core.tunnel.tcptunnel.TmtV2Tcp;
import com.acc.core.tunnel.udptunnel.TmtV2Udp;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TunnelFactory {
    public TunnelFactory() {
    }

    public static TmtV2Tunnel warp(VpnService vpnService, OutputStream outputStream, String ip, int port, String key, String session, boolean isUdp, IDelayCallback callback, int id) {
        if (isUdp) {
            try {
                DatagramSocket datagramSocket = new DatagramSocket(0);
                vpnService.protect(datagramSocket);
                return new TmtV2Udp(datagramSocket, ip, port, outputStream, key, session, vpnService, callback);
            } catch (SocketException var12) {
                var12.printStackTrace();
                return null;
            }
        } else {
            int count = 0;

            for(TmtV2Tcp tmtV2Tcp = new TmtV2Tcp(ip, port, outputStream, key, session, vpnService, callback, id); count <= 3; ++count) {
                boolean isConnect = tmtV2Tcp.connect(false);
                if (isConnect) {
                    return tmtV2Tcp;
                }
            }

            return null;
        }
    }
}
