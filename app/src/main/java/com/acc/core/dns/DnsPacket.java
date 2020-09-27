//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.acc.core.dns;

import java.nio.ByteBuffer;

public class DnsPacket {
    public DnsHeader Header;
    public Question[] Questions;
    public Resource[] Resources;
    public Resource[] AResources;
    public Resource[] EResources;
    public int Size;

    public DnsPacket() {
    }

    public static DnsPacket fromBytes(ByteBuffer buffer) {
        if (buffer.limit() < 12) {
            return null;
        } else if (buffer.limit() > 512) {
            return null;
        } else {
            DnsPacket packet = new DnsPacket();
            packet.Size = buffer.limit();
            packet.Header = DnsHeader.fromBytes(buffer);
            if (packet.Header.QuestionCount <= 2 && packet.Header.ResourceCount <= 50 && packet.Header.AResourceCount <= 50 && packet.Header.EResourceCount <= 50) {
                packet.Questions = new Question[packet.Header.QuestionCount];
                packet.Resources = new Resource[packet.Header.ResourceCount];
                packet.AResources = new Resource[packet.Header.AResourceCount];
                packet.EResources = new Resource[packet.Header.EResourceCount];

                int i;
                for(i = 0; i < packet.Questions.length; ++i) {
                    packet.Questions[i] = Question.fromBytes(buffer);
                }

                for(i = 0; i < packet.Resources.length; ++i) {
                    packet.Resources[i] = Resource.fromBytes(buffer);
                }

                for(i = 0; i < packet.AResources.length; ++i) {
                    packet.AResources[i] = Resource.fromBytes(buffer);
                }

                for(i = 0; i < packet.EResources.length; ++i) {
                    packet.EResources[i] = Resource.fromBytes(buffer);
                }

                return packet;
            } else {
                return null;
            }
        }
    }

    public static String readDomain(ByteBuffer buffer, int dnsHeaderOffset) {
        StringBuilder sb = new StringBuilder();
        int len = 0;

        while(buffer.hasRemaining() && (len = buffer.get() & 255) > 0) {
            if ((len & 192) == 192) {
                int pointer = buffer.get() & 255;
                pointer |= (len & 63) << 8;
                ByteBuffer newBuffer = ByteBuffer.wrap(buffer.array(), dnsHeaderOffset + pointer, dnsHeaderOffset + buffer.limit());
                sb.append(readDomain(newBuffer, dnsHeaderOffset));
                return sb.toString();
            }

            while(len > 0 && buffer.hasRemaining()) {
                sb.append((char)(buffer.get() & 255));
                --len;
            }

            sb.append(".");
        }

        if (len == 0 && sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public static void writeDomain(String domain, ByteBuffer buffer) {
        if (domain != null && !"".equals(domain.trim())) {
            String[] arr = domain.split("\\.");
            String[] var3 = arr;
            int var4 = arr.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String item = var3[var5];
                if (arr.length > 1) {
                    buffer.put((byte)item.length());
                }

                for(int i = 0; i < item.length(); ++i) {
                    buffer.put((byte)item.codePointAt(i));
                }
            }

        } else {
            buffer.put((byte)0);
        }
    }

    public void toBytes(ByteBuffer buffer) {
        this.Header.QuestionCount = 0;
        this.Header.ResourceCount = 0;
        this.Header.AResourceCount = 0;
        this.Header.EResourceCount = 0;
        if (this.Questions != null) {
            this.Header.QuestionCount = (short)this.Questions.length;
        }

        if (this.Resources != null) {
            this.Header.ResourceCount = (short)this.Resources.length;
        }

        if (this.AResources != null) {
            this.Header.AResourceCount = (short)this.AResources.length;
        }

        if (this.EResources != null) {
            this.Header.EResourceCount = (short)this.EResources.length;
        }

        this.Header.toBytes(buffer);

        int i;
        for(i = 0; i < this.Header.QuestionCount; ++i) {
            this.Questions[i].toBytes(buffer);
        }

        for(i = 0; i < this.Header.ResourceCount; ++i) {
            this.Resources[i].toBytes(buffer);
        }

        for(i = 0; i < this.Header.AResourceCount; ++i) {
            this.AResources[i].toBytes(buffer);
        }

        for(i = 0; i < this.Header.EResourceCount; ++i) {
            this.EResources[i].toBytes(buffer);
        }

    }
}
