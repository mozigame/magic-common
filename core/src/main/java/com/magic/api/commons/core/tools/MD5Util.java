package com.magic.api.commons.core.tools;

import com.magic.api.commons.ApiLogger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.zip.CRC32;

/**
 * zj
 */
public class MD5Util {

    private static ThreadLocal<MessageDigest> MD5;

    /**
     * 对设备号进行加盐加密。
     *
     * @param appId
     * @param deviceId
     * @return
     */
    public static String saltPassword(int appId, String deviceId) {
        String prefix = String.valueOf(appId);
        String suffix = String.valueOf(appId);
        prefix = String.valueOf(getCrc32(prefix));
        suffix = String.valueOf(getCrc32(suffix));
        String newPwd = new StringBuilder(32)
                .append(prefix)
                .append(deviceId)
                .append(suffix).toString();
        try {
            return md5Digest(newPwd.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            ApiLogger.error(String.format("Salt deviceId false, appId=%s", appId), e);
            return null;
        }
    }

    private static long getCrc32(String str) {
        try {
            byte[] b = str.getBytes("utf-8");
            CRC32 crc = (CRC32)crc32Provider.get();
            crc.reset();
            crc.update(b);
            return crc.getValue();
        } catch (UnsupportedEncodingException var2) {
            return -1L;
        }
    }

    private static ThreadLocal<CRC32> crc32Provider = new ThreadLocal() {
        protected CRC32 initialValue() {
            return new CRC32();
        }
    };

    public static String md5Digest(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            byte[] digest = md5.digest();
            return encodeHex(digest);
        }catch (Exception e) {
            ApiLogger.error("Monitor:MD5Util.md5Digest getInstance exception: ", e);
            return null;
        }
    }

    private static String encodeHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length + bytes.length);

        for(int i = 0; i < bytes.length; ++i) {
            if((bytes[i] & 255) < 16) {
                buf.append("0");
            }

            buf.append(Long.toString((long)(bytes[i] & 255), 16));
        }

        return buf.toString();
    }

}
