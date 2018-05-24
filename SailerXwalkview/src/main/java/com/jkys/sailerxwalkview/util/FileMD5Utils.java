package com.jkys.sailerxwalkview.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/7/25
 * Time: 14:55
 * Email:AndroidZern@163.com
 */
public class FileMD5Utils {

    public static String getFileMD5(InputStream in) {
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    // 获取到文件的MD5值
    public static String digest(InputStream in, String algorithm) {
        String digestStr;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] buf = new byte[1024 * 2];
            int c;
            while ((c = in.read(buf)) != -1) {
                if (c > 0) {
                    md.update(buf, 0, c);
                }
            }
            byte[] digest = md.digest();
            digestStr = hexString(digest);
            return digestStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String hexString(byte[] bs) {
        StringBuilder sb = new StringBuilder(bs.length * 2);
        int b;
        for (byte b1 : bs) {
            b = b1;
            if (b < 0) {
                b += 256;
            }
            if (b < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }
}
