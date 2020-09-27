//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings.Secure;
import com.acc.core.TmtV2VpnService.VpnBinder;
import com.acc.core.interfaces.IDelayCallback;
import com.acc.core.manager.GameUpdateRuleManager;
import com.acc.core.util.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherTmtV2 {
    private static String TAG = "TmtV2VpnService";
    public static final int TmtV2VpnUdp = 560;
    public static final int TmtV2VpnTcp = 280;
    public static String AndroidId = "";
    private TmtV2VpnService tmtV2VpnService;
    private Activity activity;
    private TmtV2Profile tmtV2Profile;
    private IDelayCallback mDelayCallback;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            LauncherTmtV2.this.tmtV2VpnService = ((VpnBinder)service).getService();
            LauncherTmtV2.this.tmtV2VpnService.setDelayCallback(LauncherTmtV2.this.mDelayCallback);
            LauncherTmtV2.this.tmtV2VpnService.setActivity(LauncherTmtV2.this.activity);
        }

        public void onServiceDisconnected(ComponentName name) {
            LauncherTmtV2.this.tmtV2VpnService = null;
        }
    };

    public LauncherTmtV2() {
    }

    public void startVpn(Activity activity, String conID, String name, String password, String ip, int port, String key, boolean model, List<String> packetNames, boolean isUdp, LauncherTmtV2.SpeedParam param) {
        LogUtils.INSTANCE.v(TAG, "Start vpn in launcher");
        this.activity = activity;
        this.tmtV2Profile = new TmtV2Profile();
        this.tmtV2Profile.name = name;
        this.tmtV2Profile.password = password;
        this.tmtV2Profile.ip = ip;
        this.tmtV2Profile.port = port;
        this.tmtV2Profile.key = key;
        this.tmtV2Profile.isGlobalModel = model;
        this.tmtV2Profile.packetNames = packetNames;
        this.tmtV2Profile.isUdp = isUdp;
        this.tmtV2Profile.conID = conID;
        if (AndroidId.isEmpty()) {
            AndroidId = Secure.getString(activity.getContentResolver(), "android_id");
        }

        Intent intent = new Intent(activity, TmtV2VpnService.class);
        intent.putExtra("TmtProfile", this.tmtV2Profile);
        this.startVpn(intent);
    }

    public void startVpn(Intent intent) {
        this.postEvent("vpnBindService", new HashMap());
        this.activity.bindService(intent, this.serviceConnection, 1);
    }

    public void stopVpn() {
        LogUtils.INSTANCE.v(TAG, "stop vpn");
        GameUpdateRuleManager.inst().clear();

        try {
            this.activity.unbindService(this.serviceConnection);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void setDelayCallback(IDelayCallback callback) {
        this.mDelayCallback = callback;
    }

    private void postEvent(String eventId, Map<String, String> params) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnConnectionEvent, new Object[]{eventId, params});
    }

    public static class SpeedParam {
        public List<String> ruleList = new ArrayList();
        public String downNodeHost;

        public SpeedParam() {
        }
    }
}
