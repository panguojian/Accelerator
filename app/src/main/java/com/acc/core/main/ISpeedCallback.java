//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.main;

import com.acc.core.helper.EnSpeedProtocol;

public interface ISpeedCallback {
    void onConnectSuccess(EnSpeedProtocol var1);

    void onConnectFailure(EnSpeedProtocol var1);

    void onCheckNetworkReturn(EnSpeedProtocol var1, boolean var2);

    void onSpeedDelay(int var1);

    void onPermissionCancel();

    void onPermissionOk();
}
