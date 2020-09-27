//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import com.acc.core.tcpip.CommonMethods;
import com.acc.core.tcpip.IPHeader;
import com.acc.core.tcpip.UDPHeader;
import com.acc.core.util.LogUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DnsParser {
    public static String TAG = "dnsparser";

    public DnsParser() {
    }

    public static void parseRequest(byte[] dataBuffer) {
        IPHeader ipHeader = new IPHeader(dataBuffer, 0);
        if (ipHeader.getProtocol() == 17) {
            UDPHeader udpHeader = new UDPHeader(dataBuffer, 20);
            udpHeader.mOffset = ipHeader.getHeaderLength();
            if (udpHeader.getDestinationPort() == 53) {
                ByteBuffer dnsBuffer = ((ByteBuffer)ByteBuffer.wrap(dataBuffer).position(28)).slice();
                dnsBuffer.clear();
                dnsBuffer.limit(udpHeader.getTotalLength() - 8);

                try {
                    DnsPacket dnsPacket = DnsPacket.fromBytes(dnsBuffer);
                    if (dnsPacket != null && dnsPacket.Header.QuestionCount > 0) {
                        LogUtils.INSTANCE.v(TAG, "Query " + dnsPacket.Questions[0].Domain);
                    } else {
                        LogUtils.INSTANCE.v(TAG, "dnsPacket == null");
                    }
                } catch (Exception var5) {
                    LogUtils.INSTANCE.v(TAG, "Parse dns error: " + var5);
                }
            }
        }

    }

    public static DnsParser.DnsResponseData parseResponse(byte[] rootBuffer) {
        byte[] dataBuffer = new byte[rootBuffer.length];
        System.arraycopy(rootBuffer, 0, dataBuffer, 0, rootBuffer.length);
        IPHeader ipHeader = new IPHeader(dataBuffer, 0);
        byte[] RECEIVE_BUFFER = new byte[20000];
        ipHeader.Default();
        int ipHeaderLength = 20;
        if (ipHeader.getProtocol() == 17) {
            UDPHeader udpHeader = new UDPHeader(dataBuffer, 20);
            udpHeader.mOffset = ipHeader.getHeaderLength();
            if (udpHeader.getSourcePort() == 53) {
                int udpHeaderLenght = 8;
                ByteBuffer dnsBuffer = ByteBuffer.wrap(RECEIVE_BUFFER);
                dnsBuffer.position(ipHeaderLength + udpHeaderLenght);
                dnsBuffer = dnsBuffer.slice();
                int packSize = dataBuffer.length - 28;
                System.arraycopy(dataBuffer, 28, RECEIVE_BUFFER, 28, packSize);
                dnsBuffer.clear();
                dnsBuffer.limit(packSize);

                try {
                    DnsPacket dnsPacket = DnsPacket.fromBytes(dnsBuffer);
                    if (dnsPacket != null) {
                        if (dnsPacket.Header.QuestionCount > 0 && dnsPacket.Header.ResourceCount > 0) {
                            return printAll(dnsPacket);
                        }
                    } else {
                        LogUtils.INSTANCE.v(TAG, "dnsPacket == null");
                    }
                } catch (Exception var10) {
                    LogUtils.INSTANCE.v(TAG, "Parse dns error: " + var10);
                }
            }
        }

        return null;
    }

    public static DnsParser.DnsResponseData printAll(DnsPacket dnsPacket) {
        DnsParser.DnsResponseData data = new DnsParser.DnsResponseData();
        data.questionDomain = dnsPacket.Questions[0].Domain;
        data.ipList = new ArrayList();
        data.answerDomain = new ArrayList();

        int i;
        for(i = 0; i < dnsPacket.Header.QuestionCount; ++i) {
            Question question = dnsPacket.Questions[i];
            LogUtils.INSTANCE.v(TAG, "question : " + question.Domain + " ==>");
        }

        for(i = 0; i < dnsPacket.Header.ResourceCount; ++i) {
            Resource resource = dnsPacket.Resources[i];
            if (resource.Type == 1) {
                int ip = CommonMethods.readInt(resource.Data, 0);
                LogUtils.INSTANCE.v(TAG, "ip: " + CommonMethods.ipIntToString(ip) + " domain: " + resource.Domain);
                data.ipList.add(CommonMethods.ipIntToString(ip));
                data.answerDomain.add(resource.Domain);
            }
        }

        return data;
    }

    public static int getFirstIP(DnsPacket dnsPacket) {
        for(int i = 0; i < dnsPacket.Header.ResourceCount; ++i) {
            Resource resource = dnsPacket.Resources[i];
            if (resource.Type == 1) {
                int ip = CommonMethods.readInt(resource.Data, 0);
                return ip;
            }
        }

        return 0;
    }

    public static class DnsResponseData {
        public String questionDomain;
        public List<String> ipList;
        public List<String> answerDomain;

        public DnsResponseData() {
        }
    }
}
