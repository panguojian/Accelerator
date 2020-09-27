//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.List;

public class TmtV2Profile implements Parcelable {
    public String conID;
    public String name;
    public String password;
    public String ip;
    public String key;
    public boolean isGlobalModel;
    public boolean isUdp;
    public int port;
    public List<String> packetNames;
    public static final Creator<TmtV2Profile> CREATOR = new Creator<TmtV2Profile>() {
        public TmtV2Profile createFromParcel(Parcel in) {
            return new TmtV2Profile(in);
        }

        public TmtV2Profile[] newArray(int size) {
            return new TmtV2Profile[size];
        }
    };

    public TmtV2Profile() {
    }

    protected TmtV2Profile(Parcel in) {
        this.conID = in.readString();
        this.name = in.readString();
        this.password = in.readString();
        this.ip = in.readString();
        this.key = in.readString();
        this.isGlobalModel = in.readByte() != 0;
        this.isUdp = in.readByte() != 0;
        this.port = in.readInt();
        this.packetNames = in.createStringArrayList();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.conID);
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeString(this.ip);
        dest.writeString(this.key);
        dest.writeInt((byte)(this.isGlobalModel ? 1 : 0));
        dest.writeByte((byte)(this.isUdp ? 1 : 0));
        dest.writeInt(this.port);
        dest.writeStringList(this.packetNames);
    }

    public int describeContents() {
        return 0;
    }
}
