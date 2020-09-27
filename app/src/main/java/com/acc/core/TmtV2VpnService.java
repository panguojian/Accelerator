//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.VpnService;
import android.net.VpnService.Builder;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.Build.VERSION;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.acc.core.LoginVerify.ICallback;
import com.acc.core.NotificationCenter.NotificationCenterDelegate;
import com.acc.core.R.string;
import com.acc.core.helper.ThreadPool;
import com.acc.core.interfaces.IDelayCallback;
import com.acc.core.tcpip.CommonMethods;
import com.acc.core.tunnel.TmtV2Tunnel;
import com.acc.core.tunnel.TunnelFactory;
import com.acc.core.tunnel.tcptunnel.TmtV2Tcp;
import com.acc.core.util.LogUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TmtV2VpnService extends VpnService implements Runnable, ICallback, NotificationCenterDelegate {
    public static final String TAG = "TmtV2VpnService";
    public static final String TmtProfile = "TmtProfile";
    public static boolean ENABLE_DOWNLOAD = true;
    private static final int PRIORITY_MIN = -2;
    private static final int PRIORITY_DEFAULT = 0;
    private static final int PRIORITY_MAX = 2;
    private AtomicBoolean vpnRunning = new AtomicBoolean(false);
    public static String ipSpeed;
    private TmtV2Profile tmtV2Profile;
    private TunConfig mTunConfig;
    private LoginVerify loginVerify;
    private TmtV2Tunnel tmtV2Tunnel;
    private ParcelFileDescriptor fd;
    private InputStream inputStream;
    private OutputStream outputStream;
    private AtomicBoolean isGlobalModel = new AtomicBoolean(true);
    private WakeLock wakeLock;
    private IDelayCallback mDelayCallback;
    private EventListener eventListener;
    private Activity activity;
    private Intent mIntent;
    private AtomicBoolean rebuildVirtualNetCard = new AtomicBoolean(false);
    private long startVpnTime = 0L;
    private long endVpnTime = 0L;

    public TmtV2VpnService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initService();
    }

    private void initService() {
        this.loginVerify = new LoginVerify();
        this.vpnRunning.set(false);
        this.wakeLock = ((PowerManager)this.getSystemService("power")).newWakeLock(1, this.getClass().getSimpleName());
        this.loginVerify.setVpnStatusCallback(this);
    }

    private void onAddObservers() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.changeModel);
    }

    private void onRemoveObservers() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.changeModel);
    }

    @RequiresApi(26)
    private String getNotificationChannel(NotificationManager notificationManager) {
        String channelId = "channelid";
        String channelName = this.getResources().getString(string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, 4);
        channel.setImportance(0);
        channel.setLockscreenVisibility(0);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public void setDelayCallback(IDelayCallback callback) {
        this.mDelayCallback = callback;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.INSTANCE.v("TmtV2VpnService", "onStartCommand");
        this.initProfileFromIntent(intent);
        this.startVpn();
        return 2;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.onRemoveObservers();
        LogUtils.INSTANCE.v("TmtV2VpnService", "---------onUnbind------");
        this.loginVerify.stopLogin();
        this.tmtV2Tunnel.setForceBroke(true);
        if (this.vpnRunning.get()) {
            this.closeVpn();
        }

        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.onAddObservers();
        LogUtils.INSTANCE.v("TmtV2VpnService", "----------onBind------------");
        if (intent.getAction() == null) {
            this.initProfileFromIntent(intent);
            this.startVpn();
            this.mIntent = intent;
        }

        return new TmtV2VpnService.VpnBinder();
    }

    private void startVpn() {
        Log.e("onBing", "开启登陆校验");
        ThreadPool.submit(new Runnable() {
            public void run() {
                Log.e("onBing", "开启登陆校验线程");
                TmtV2VpnService.this.loginVerify.login(TmtV2VpnService.this.tmtV2Profile.name, TmtV2VpnService.this.tmtV2Profile.password, TmtV2VpnService.this.tmtV2Profile.ip, TmtV2VpnService.this.tmtV2Profile.port, TmtV2VpnService.this.tmtV2Profile.key, TmtV2VpnService.this.tmtV2Profile.isUdp);
            }
        });
    }

    private void postLoginEvent() {
        Map<String, String> paramMap = new HashMap();
        paramMap.put("name", this.tmtV2Profile.name);
        paramMap.put("ip", this.tmtV2Profile.ip);
        paramMap.put("port", "" + this.tmtV2Profile.port);
        this.postEvent("vpnInitialLoginThread", paramMap);
    }

    private void initProfileFromIntent(Intent intent) {
        if (intent.hasExtra("TmtProfile")) {
            this.tmtV2Profile = (TmtV2Profile)intent.getParcelableExtra("TmtProfile");
            this.isGlobalModel.set(this.tmtV2Profile.isGlobalModel);
        }

    }

    public void onDestroy() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "onDestroy");
        super.onDestroy();
    }

    public void onTunnelCallback(int statusCode, TunConfig tunConfig, boolean isUDP, int id) {
    }

    public void onLoginCallback(int statusCode, TunConfig tunConfig, boolean isUDP) {
        switch(statusCode) {
            case 200:
                Map<String, String> paramMap = new HashMap();
                paramMap.put("ip", tunConfig.ip);
                paramMap.put("port", tunConfig.port);
                paramMap.put("gateway", tunConfig.gateway);
                paramMap.put("session", tunConfig.session);
                this.postEvent("vpnLoginSuccess", paramMap);
                LogUtils.INSTANCE.v("TmtV2VpnService", "加速节点验证成功");
                this.mTunConfig = tunConfig;
                ipSpeed = tunConfig.ip;
                ThreadPool.submit(this);
                break;
            default:
                Map<String, String> failedParamMap = new HashMap();
                failedParamMap.put("stausCode", "" + statusCode);
                this.postEvent("vpnLoginFailed", failedParamMap);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnVerifyFailed, new Object[]{this.tmtV2Profile.conID});
        }

    }

    private ParcelFileDescriptor buildVirtualCard() {
        LogUtils.INSTANCE.v("TmtV2VpnService", String.format("Build a virtual net card==> address:%s", this.mTunConfig.ip));
        Builder builder = new Builder(this);
        if (this.tmtV2Profile.isUdp) {
            builder.setSession(this.getString(string.app_name));
        } else {
            builder.setSession(this.getString(string.app_name));
        }

        builder.setMtu(1500);

        for(int i = 0; i < this.mTunConfig.dns.length; ++i) {
            builder.addDnsServer(this.mTunConfig.dns[i]);
        }

        builder.addAddress(this.mTunConfig.ip, 24);
        builder.addRoute("0.0.0.0", 0);
        this.setApplicationPackage(builder, this.tmtV2Profile.packetNames);
        if (this.activity != null) {
            this.setConfigureIntent(builder, this.activity.getClass());
        }

        ParcelFileDescriptor fd = builder.establish();
        return fd;
    }

    private void setApplicationPackage(Builder builder, List<String> packetList) {
        if (VERSION.SDK_INT >= 21) {
            if (!this.isGlobalModel.get()) {
                if (packetList == null) {
                    packetList = new ArrayList();
                }

                ((List)packetList).add("a.b.c");
                Iterator var3 = ((List)packetList).iterator();

                while(var3.hasNext()) {
                    String packName = (String)var3.next();
                    LogUtils.INSTANCE.v("TmtV2VpnService", String.format("Allowed application:%s", packName));

                    try {
                        builder.addAllowedApplication(packName);
                    } catch (NameNotFoundException var7) {
                        var7.printStackTrace();
                    }
                }
            } else {
                try {
                    builder.addDisallowedApplication(this.getPackageName());
                } catch (NameNotFoundException var6) {
                    var6.printStackTrace();
                }
            }

        }
    }

    private void setConfigureIntent(Builder builder, Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setConfigureIntent(pendingIntent);
    }

    public void run() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "Start to run in vpn thread");
        String errorMessage = "";

        try {
            this.wakeLock.acquire();
            this.fd = this.buildVirtualCard();
            this.inputStream = new FileInputStream(this.fd.getFileDescriptor());
            this.outputStream = new FileOutputStream(this.fd.getFileDescriptor());
            this.vpnRunning.set(true);
            this.tmtV2Tunnel = this.createProxyTunnel();
            if (this.tmtV2Tunnel == null) {
                LogUtils.INSTANCE.v("TmtV2VpnService", "Create vpn tunnel failed");
                this.vpnRunning.set(false);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnConnectFailed, new Object[]{this.tmtV2Profile.conID});
                this.clear();
                return;
            }

            NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnConnectedSuccessfully, new Object[]{this.tmtV2Profile.conID});
            this.startVpnTime = System.currentTimeMillis();
            byte[] buffer = new byte[8000];
            int size = 0;

            while(this.tmtV2Tunnel != null && this.vpnRunning.get() && size != -1) {
                while(this.vpnRunning.get() && (size = this.inputStream.read(buffer)) > 0) {
                    String srcIp = this.readResourceIp(buffer);
                    this.readDestIp(buffer);
                    if (srcIp.equals(this.mTunConfig.ip)) {
                        this.outputData(buffer, size);
                    }

                    if (this.rebuildVirtualNetCard.get()) {
                        this.rebuildVirtualNetCard();
                        this.rebuildVirtualNetCard.set(false);
                    }
                }

                try {
                    Thread.sleep(2L);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }
            }

            this.clear();
            this.closeTunnel();
        } catch (IOException var7) {
            errorMessage = var7.getMessage();
            var7.printStackTrace();
        }

        this.endVpnTime = System.currentTimeMillis();
        long duration = this.endVpnTime - this.startVpnTime;
        Map<String, String> eventParams = new HashMap();
        eventParams.put("errorMessage", errorMessage);
        eventParams.put("duration", "" + duration);
        eventParams.put("vpnIsRunning", "" + this.vpnRunning);
        LogUtils.INSTANCE.v("TmtV2VpnService", "Finish vpn thread");
        this.postEvent("vpnEndVpnTunnel", eventParams);
    }

    private TmtV2Tunnel createProxyTunnel() {
        long startTime = System.currentTimeMillis();
        LogUtils.INSTANCE.v("TmtV2VpnService", String.format("Create proxy tunnel==> %s:%s", this.tmtV2Profile.ip, this.mTunConfig.port));
        TmtV2Tunnel tunnel = TunnelFactory.warp(this, this.outputStream, this.tmtV2Profile.ip, Integer.parseInt(this.mTunConfig.port), this.tmtV2Profile.key, this.mTunConfig.session, this.tmtV2Profile.isUdp, this.mDelayCallback, 0);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LogUtils.INSTANCE.v("TmtV2VpnService", "Duration:" + duration);
        Map<String, String> eventParams = new HashMap();
        eventParams.put("duration", "" + duration);
        if (tunnel == null) {
            this.postEvent("vpnOpenTunnelFailed", eventParams);
        } else {
            this.postEvent("vpnOpenTunnel", eventParams);
        }

        return tunnel;
    }

    private String readResourceIp(byte[] buffer) {
        int intIp = CommonMethods.readInt(buffer, 12);
        return CommonMethods.ipIntToString(intIp);
    }

    private String readDestIp(byte[] buffer) {
        int intIp = CommonMethods.readInt(buffer, 16);
        return CommonMethods.ipIntToString(intIp);
    }

    private void outputData(byte[] buffer, int size) {
        byte[] temp = new byte[size];
        System.arraycopy(buffer, 0, temp, 0, size);
        this.tmtV2Tunnel.write(temp);
    }

    private void rebuildVirtualNetCard() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "Rebuild virtual net card");
        ParcelFileDescriptor newFD = this.buildVirtualCard();
        FileInputStream newFileInputStream = new FileInputStream(newFD.getFileDescriptor());
        FileOutputStream newFileOutputStream = new FileOutputStream(newFD.getFileDescriptor());
        this.tmtV2Tunnel.changeOutputStream(newFileOutputStream);
        this.clear();
        this.fd = newFD;
        this.inputStream = newFileInputStream;
    }

    private void clear() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "Clear all resource");
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }

        if (this.fd != null) {
            try {
                this.fd.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    private void closeTunnel() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "Close tunnel");
        this.tmtV2Tunnel.exit();
        if (this.tmtV2Tunnel instanceof TmtV2Tcp) {
            ((TmtV2Tcp)this.tmtV2Tunnel).closeTunnel(true);
        } else {
            this.tmtV2Tunnel.closeTunnel();
        }

    }

    private void closeVpn() {
        LogUtils.INSTANCE.v("TmtV2VpnService", "Close vpn");
        this.vpnRunning.set(false);
        this.loginVerify.setVpnStatusCallback((ICallback)null);
        String conId = this.tmtV2Profile == null ? "" : this.tmtV2Profile.conID;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnDisconnected, new Object[]{conId});
    }

    private void postEvent(String eventId, Map<String, String> params) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.vpnConnectionEvent, new Object[]{eventId, params});
    }

    public void verifyCallback(int statusCode, TunConfig tunConfig, boolean isUDP) {
        LogUtils.INSTANCE.v("TmtV2VpnService", String.format("VerifyCallBack status code%d", statusCode));
        this.onLoginCallback(statusCode, tunConfig, isUDP);
    }

    public void onRevoke() {
        super.onRevoke();
        this.stopForeground(true);
        LogUtils.INSTANCE.v("TmtV2VpnService", "onRevoke");
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.changeModel) {
            LogUtils.INSTANCE.v("TmtV2VpnService", "To change model");
            boolean model = (Boolean)args[0];
            this.isGlobalModel.set(model);
            List<String> packageList = (List)args[1];
            this.tmtV2Profile.packetNames = packageList;
            this.rebuildVirtualNetCard.set(true);
        }

    }

    public class VpnBinder extends Binder {
        public VpnBinder() {
        }

        public TmtV2VpnService getService() {
            return TmtV2VpnService.this;
        }
    }
}
