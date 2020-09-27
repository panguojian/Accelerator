//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.main;

import android.app.Activity;
import com.acc.core.LauncherTmtV2;
import com.acc.core.LauncherTmtV2.SpeedParam;
import com.acc.core.helper.EnSpeedProtocol;
import com.acc.core.util.LogUtils;
import java.util.List;

public class SpeedSDK {
    static final String TAG = "SpeedSDK";
    private LauncherTmtV2 mLauncherTmtV2 = new LauncherTmtV2();

    public SpeedSDK() {
    }

    public void stopNoWait() {
        LogUtils.INSTANCE.v("SpeedSDK", "停止不等待");
        this.stopVpn();
    }

    private void stopVpn() {
        this.mLauncherTmtV2.stopVpn();
    }

    public void startVpn(Activity activity, String conID, String name, String password, String ip, int port, String key, boolean model, List<String> packetNames, EnSpeedProtocol protocol, SpeedParam param) {
        boolean isUdp = protocol == EnSpeedProtocol.eTmtV2_UDP;
        this.mLauncherTmtV2.startVpn(activity, conID, name, password, ip, port, key, model, packetNames, isUdp, param);
    }
}
