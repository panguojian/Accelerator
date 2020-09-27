//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.util.LogUtils;

public class DisConnectTimeOutHelper extends BaseTimer {
    static final String TAG = "DisConnectTimeOutHelper";
    private DisConnectTimeOutHelper.IDisConnectTimeoutCallback mCallback;

    public DisConnectTimeOutHelper(DisConnectTimeOutHelper.IDisConnectTimeoutCallback callback) {
        this.mCallback = callback;
        this.setMillSecond(10000);
    }

    @Override
    public void onTimeOut() {
        LogUtils.INSTANCE.e("DisConnectTimeOutHelper", "Timeout");
        this.mCallback.onDisConnectTimeout();
    }

    public interface IDisConnectTimeoutCallback {
        void onDisConnectTimeout();
    }
}
