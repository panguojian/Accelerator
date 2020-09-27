//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.util.LogUtils;

public class ConnectTimeOutHelper extends BaseTimer {
    static final String TAG = "ConnectTimeOutHelper";
    private ConnectTimeOutHelper.IConnectTimeoutCallback mCallback;

    public ConnectTimeOutHelper(ConnectTimeOutHelper.IConnectTimeoutCallback callback) {
        this.mCallback = callback;
        this.setMillSecond(10000);
    }

    @Override
    public void onTimeOut() {
        LogUtils.INSTANCE.e("ConnectTimeOutHelper", "vpn连接超时");
        this.mCallback.onConnectTimeout();
    }

    public interface IConnectTimeoutCallback {
        void onConnectTimeout();
    }
}
