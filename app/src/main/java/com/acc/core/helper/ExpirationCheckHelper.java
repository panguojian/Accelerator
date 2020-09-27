//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.util.LogUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpirationCheckHelper extends BaseTimer {
    static final String TAG = "ExpirationCheckHelper";
    private ExpirationCheckHelper.IExpirationCheckCallback mCallback;
    private boolean mIsRunning = false;

    public ExpirationCheckHelper(ExpirationCheckHelper.IExpirationCheckCallback callback) {
        this.mCallback = callback;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void start(String expiration) {
        LogUtils.INSTANCE.v("ExpirationCheckHelper", "start");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date;
        try {
            date = simpleDateFormat.parse(expiration);
        } catch (ParseException var8) {
            var8.printStackTrace();
            return;
        }

        this.mIsRunning = true;
        int mExpireTime = (int)(date.getTime() / 1000L);
        int mNowTime = (int)(Calendar.getInstance().getTimeInMillis() / 1000L);
        int count = mExpireTime - mNowTime;
        LogUtils.INSTANCE.v("ExpirationCheckHelper", "exp count=" + count);
        if (count > 86400) {
            this.mIsRunning = false;
        } else {
            int millSecond = count * 1000;
            if (millSecond < 1) {
                LogUtils.INSTANCE.e("ExpirationCheckHelper", "millSecond error");
                this.mIsRunning = false;
            } else {
                this.setMillSecond(millSecond);
                super.start();
            }
        }
    }

    public void stop() {
        super.stop();
        this.mIsRunning = false;
    }

    @Override
    public void onTimeOut() {
        LogUtils.INSTANCE.e("ExpirationCheckHelper", "vpn试用时间到期");
        this.mCallback.onExpirationTimeOut();
    }

    public interface IExpirationCheckCallback {
        void onExpirationTimeOut();
    }
}
