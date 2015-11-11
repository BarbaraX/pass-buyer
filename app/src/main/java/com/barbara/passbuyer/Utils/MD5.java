package com.barbara.passbuyer.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by barbara on 7/3/15.
 * 用于对网络文件名进行MD5加密，出于对安全的考虑
 */
public class MD5 {
    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();

        for (byte b: digest.digest()) {
            builder.append(Integer.toHexString( ((b>>4)&0x0f) ));
            builder.append(Integer.toHexString( (b&0x0f) ));
        }

        return builder.toString();
    }
}
