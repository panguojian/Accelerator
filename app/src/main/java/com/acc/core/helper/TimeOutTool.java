//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.helper;

import com.acc.core.util.LogUtils;
import java.util.Timer;
import java.util.TimerTask;

public class TimeOutTool {
    static final String TAG = "TimeOutTool";
    private TimerTask mTask;
    private Timer mTimer;

    public TimeOutTool() {
    }

    public void start(int millSecond, final TimeOutTool.ITimeOutCallback callback) {
        LogUtils.INSTANCE.v("TimeOutTool", "start");
        if (millSecond < 1) {
            LogUtils.INSTANCE.e("TimeOutTool", "timer start error");
        } else {
            this.mTask = new TimerTask() {
                public void run() {
                    callback.onTimeOut();
                }
            };
            this.mTimer = new Timer();
            this.mTimer.schedule(this.mTask, (long)millSecond);
        }
    }

    public void startByRepeat(int millSecond, final TimeOutTool.ITimeOutCallback callback) {
        if (millSecond < 1) {
            LogUtils.INSTANCE.e("TimeOutTool", "timer start error");
        } else {
            this.mTask = new TimerTask() {
                public void run() {
                    callback.onTimeOut();
                }
            };
            this.mTimer = new Timer();
            this.mTimer.schedule(this.mTask, (long)millSecond, (long)millSecond);
        }
    }

    public void stop() {
        if (this.mTimer != null) {
            LogUtils.INSTANCE.v("TimeOutTool", "stop");
        }

        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }

        if (this.mTask != null) {
            this.mTask.cancel();
            this.mTask = null;
        }

    }

    public interface ITimeOutCallback {
        void onTimeOut();
    }
}
