//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

public class DnsFlag {
    public boolean QR;
    public int OpCode;
    public boolean AA;
    public boolean TC;
    public boolean RD;
    public boolean RA;
    public int Zero;
    public int Rcode;

    public DnsFlag() {
    }

    public static DnsFlag Parse(short value) {
        int flags = value & '\uffff';
        DnsFlag flag = new DnsFlag();
        flag.QR = (flags >> 15 & 1) == 1;
        flag.OpCode = flags >> 11 & 15;
        flag.AA = (flags >> 10 & 1) == 1;
        flag.TC = (flags >> 9 & 1) == 1;
        flag.RD = (flags >> 8 & 1) == 1;
        flag.RA = (flags >> 7 & 1) == 1;
        flag.Zero = flags >> 4 & 7;
        flag.Rcode = flags & 15;
        return flag;
    }

    public short ToShort() {
        int flags = 0;
        flags = flags | (this.QR ? 1 : 0) << 15;
        flags |= (this.OpCode & 15) << 11;
        flags |= (this.AA ? 1 : 0) << 10;
        flags |= (this.TC ? 1 : 0) << 9;
        flags |= (this.RD ? 1 : 0) << 8;
        flags |= (this.RA ? 1 : 0) << 7;
        flags |= (this.Zero & 7) << 4;
        flags |= this.Rcode & 15;
        return (short)flags;
    }
}
