//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class NotificationCenter {
    private static int totalEvents = 1;
    public static final int vpnConnectedSuccessfully;
    public static final int vpnConnectFailed;
    public static final int vpnDisconnected;
    public static final int vpnPermissionDenied;
    public static final int vpnConnectionEvent;
    public static final int vpnVerifyFailed;
    public static final int toStopVpn;
    public static final int switchTab;
    public static final int vipExpired;
    public static final int noneNetwork;
    public static final int accountChanged;
    public static final int multipleAcc;
    public static final int networkChanged;
    public static final int disableTabs;
    public static final int enableTabs;
    public static final int purchaseSuccessfully;
    public static final int purchasePadding;
    public static final int purchaseException;
    public static final int changeModel;
    private final HashMap<Integer, ArrayList<Object>> observers = new HashMap();
    private final HashMap<Integer, Object> removeAfterBroadcast = new HashMap();
    private final HashMap<Integer, Object> addAfterBroadcast = new HashMap();
    private int broadcasting = 0;
    private static volatile NotificationCenter instance;

    public NotificationCenter() {
    }

    public static NotificationCenter getInstance() {
        NotificationCenter localInstance = instance;
        if (localInstance == null) {
            Class var1 = NotificationCenter.class;
            synchronized(NotificationCenter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new NotificationCenter();
                }
            }
        }

        return localInstance;
    }

    public void postNotificationName(int id, Object... args) {
        synchronized(this.observers) {
            ++this.broadcasting;
            ArrayList<Object> objects = (ArrayList)this.observers.get(id);
            Iterator var5;
            if (objects != null) {
                var5 = objects.iterator();

                while(var5.hasNext()) {
                    Object obj = var5.next();
                    ((NotificationCenter.NotificationCenterDelegate)obj).didReceivedNotification(id, args);
                }
            }

            --this.broadcasting;
            if (this.broadcasting == 0) {
                Entry entry;
                if (!this.removeAfterBroadcast.isEmpty()) {
                    var5 = this.removeAfterBroadcast.entrySet().iterator();

                    while(var5.hasNext()) {
                        entry = (Entry)var5.next();
                        this.removeObserver(entry.getValue(), (Integer)entry.getKey());
                    }

                    this.removeAfterBroadcast.clear();
                }

                if (!this.addAfterBroadcast.isEmpty()) {
                    var5 = this.addAfterBroadcast.entrySet().iterator();

                    while(var5.hasNext()) {
                        entry = (Entry)var5.next();
                        this.addObserver(entry.getValue(), (Integer)entry.getKey());
                    }

                    this.addAfterBroadcast.clear();
                }
            }

        }
    }

    public void addObserver(Object observer, int id) {
        synchronized(this.observers) {
            if (this.broadcasting != 0) {
                this.addAfterBroadcast.put(id, observer);
            } else {
                ArrayList<Object> objects = (ArrayList)this.observers.get(id);
                if (objects == null) {
                    this.observers.put(id, objects = new ArrayList());
                }

                if (!objects.contains(observer)) {
                    objects.add(observer);
                }
            }
        }
    }

    public void removeObserver(Object observer, int id) {
        synchronized(this.observers) {
            if (this.broadcasting != 0) {
                this.removeAfterBroadcast.put(id, observer);
            } else {
                ArrayList<Object> objects = (ArrayList)this.observers.get(id);
                if (objects != null) {
                    objects.remove(observer);
                    if (objects.size() == 0) {
                        this.observers.remove(id);
                    }
                }

            }
        }
    }

    static {
        vpnConnectedSuccessfully = totalEvents++;
        vpnConnectFailed = totalEvents++;
        vpnDisconnected = totalEvents++;
        vpnPermissionDenied = totalEvents++;
        vpnConnectionEvent = totalEvents++;
        vpnVerifyFailed = totalEvents++;
        toStopVpn = totalEvents++;
        switchTab = totalEvents++;
        vipExpired = totalEvents++;
        noneNetwork = totalEvents++;
        accountChanged = totalEvents++;
        multipleAcc = totalEvents++;
        networkChanged = totalEvents++;
        disableTabs = totalEvents++;
        enableTabs = totalEvents++;
        purchaseSuccessfully = totalEvents++;
        purchasePadding = totalEvents++;
        purchaseException = totalEvents++;
        changeModel = totalEvents++;
        instance = null;
    }

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int var1, Object... var2);
    }
}
