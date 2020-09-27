//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.util.LogUtils;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseTimer {
    static final String TAG = "BaseTimer";
    private int mMillSecond = 0;
    private TimerTask mTask;
    private Timer mTimer;

    public BaseTimer() {
    }

    public void setMillSecond(int millSecond) {
        this.mMillSecond = millSecond;
    }

    public void start() {
        LogUtils.INSTANCE.v("BaseTimer", "start");
        if (this.mMillSecond == 0) {
            LogUtils.INSTANCE.e("BaseTimer", "timer start error");
        } else {
            this.mTask = new TimerTask() {
                public void run() {
                    if (BaseTimer.this.mTimer != null) {
                        BaseTimer.this.onTimeOut();
                    }

                }
            };
            this.mTimer = new Timer();
            this.mTimer.schedule(this.mTask, (long)this.mMillSecond);
        }
    }

    public abstract void onTimeOut();

    public void stop() {
        LogUtils.INSTANCE.v("BaseTimer", "stop");
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }

        if (this.mTask != null) {
            this.mTask.cancel();
            this.mTask = null;
        }

    }
}
