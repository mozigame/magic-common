package com.magic.api.commons.tools;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author zz 
 */
public class URLUtil {

    public static final String DEFAULT_CHARSET = "utf-8";

    public static String encode(String string) {
        try {
            return URLEncoder.encode(string, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("encode发生异常", e);
        }
    }

    public static String decode(String string) {
        try {
            return URLDecoder.decode(string, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("decode发生异常", e);
        }
    }
}
