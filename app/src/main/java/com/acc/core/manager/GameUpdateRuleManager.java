//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.manager;

import android.text.TextUtils;
import com.acc.core.TmtV2VpnService;
import com.acc.core.dns.DnsParser.DnsResponseData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class GameUpdateRuleManager {
    public List<String> mDomainList = new ArrayList();
    public List<String> mIpList = new ArrayList();
    public String mDownloadHost;
    private List<String> mRegexList = new ArrayList();
    private static GameUpdateRuleManager mObject;

    public GameUpdateRuleManager() {
    }

    public String getDownloadHost() {
        return this.mDownloadHost;
    }

    public static GameUpdateRuleManager inst() {
        if (mObject == null) {
            mObject = new GameUpdateRuleManager();
        }

        return mObject;
    }

    public void setDownloadHost(String host) {
        this.mDownloadHost = host;
    }

    public boolean isDomainExist(String domain) {
        Iterator var2 = this.mDomainList.iterator();

        String temp;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            temp = (String)var2.next();
        } while(!temp.equals(domain));

        return true;
    }

    public void setRegexs(List<String> regexs) {
        if (regexs != null) {
            this.mRegexList.clear();
            Iterator var2 = regexs.iterator();

            while(var2.hasNext()) {
                String reg = (String)var2.next();
                this.mRegexList.add(reg);
            }

        }
    }

    private void addDomain(String domain) {
        if (!TextUtils.isEmpty(domain)) {
            if (!this.isDomainExist(domain)) {
                this.mDomainList.add(domain);
            }
        }
    }

    private void addIp(String ip) {
        if (!TextUtils.isEmpty(ip)) {
            if (!this.isIpExist(ip)) {
                this.mIpList.add(ip);
            }
        }
    }

    public void clear() {
        this.mDomainList.clear();
        this.mIpList.clear();
        this.mRegexList.clear();
        this.mDownloadHost = null;
    }

    public void recvDnsData(DnsResponseData dnsData) {
        if (dnsData != null) {
            if (this.isDownloadAnswer(dnsData.questionDomain)) {
                Iterator var2 = dnsData.ipList.iterator();

                String domain;
                while(var2.hasNext()) {
                    domain = (String)var2.next();
                    this.addIp(domain);
                }

                var2 = dnsData.answerDomain.iterator();

                while(var2.hasNext()) {
                    domain = (String)var2.next();
                    this.addDomain(domain);
                }
            }

        }
    }

    private boolean isDownloadAnswer(String question) {
        if (this.mRegexList.size() < 1) {
            return false;
        } else {
            Iterator var2 = this.mRegexList.iterator();

            boolean isMatch;
            do {
                String domain;
                if (!var2.hasNext()) {
                    var2 = this.mDomainList.iterator();

                    do {
                        if (!var2.hasNext()) {
                            return false;
                        }

                        domain = (String)var2.next();
                    } while(!domain.equals(question));

                    return true;
                }

                domain = (String)var2.next();
                isMatch = Pattern.matches(domain, question);
            } while(!isMatch);

            return true;
        }
    }

    private boolean isIpExist(String ip) {
        Iterator var2 = this.mIpList.iterator();

        String temp;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            temp = (String)var2.next();
        } while(!temp.equals(ip));

        return true;
    }

    public boolean isDownloadIp(String ip) {
        if (!TmtV2VpnService.ENABLE_DOWNLOAD) {
            return false;
        } else {
            Iterator var2 = this.mIpList.iterator();

            String downloadIp;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                downloadIp = (String)var2.next();
            } while(!downloadIp.equals(ip));

            return true;
        }
    }
}
