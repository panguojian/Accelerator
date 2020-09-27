//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.tunnel;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Tunnel<T> {
    protected AtomicBoolean tunnelSwitchStatute = new AtomicBoolean(false);
    protected AtomicBoolean tunnelStatute = new AtomicBoolean(true);
    protected AtomicBoolean isForceBroke = new AtomicBoolean(false);

    public Tunnel() {
    }

    public abstract T write(byte[] var1);

    public abstract T read(ByteBuffer var1);

    public void openTunnel() {
        this.tunnelSwitchStatute.set(true);
    }

    public void closeTunnel() {
        this.tunnelSwitchStatute.set(false);
    }

    public void pauseTunnel() {
        this.tunnelStatute.set(false);
    }

    public void continues() {
        this.tunnelStatute.set(true);
    }

    public boolean getTunnelSwitchStatute() {
        return this.tunnelSwitchStatute.get();
    }

    public boolean getTunnelStatute() {
        return this.tunnelStatute.get();
    }

    public void setForceBroke(boolean broke) {
        this.isForceBroke.set(broke);
    }
}
