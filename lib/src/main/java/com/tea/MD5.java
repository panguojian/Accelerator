//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tea;

import java.security.MessageDigest;

public class MD5 {
    public MD5() {
    }

    public static String MD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte[] b = md.digest();
            StringBuffer buf = new StringBuffer("");

            for(int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            return new String(buf.toString());
        } catch (Exception var6) {
            var6.printStackTrace();
            return "";
        }
    }

    public static byte[] MD5(String text, int cmd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            return md.digest();
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
