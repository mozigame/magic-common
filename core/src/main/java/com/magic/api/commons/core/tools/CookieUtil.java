package com.magic.api.commons.core.tools;

import com.magic.api.commons.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 * @author zz
 */
public class CookieUtil {

    /**
     * cookie name
     */
    public static final String COOKIE_NAME = "n_t";

    /**
     * cookie age 1小时
     */
    public static final int COOKIE_AGE = 3600;

    /**
     * 添加mauth Cookie
     * @param response  HttpServletResponse
     * @param mauth     mauth
     */
    public static void setMauth(HttpServletResponse response, String mauth) {
        setMauth(response, mauth, COOKIE_AGE);
    }

    /**
     * 添加mauth Cookie
     * @param response  HttpServletResponse
     * @param mauth     mauth
     * @param age       有效期 秒
     */
    public static void setMauth(HttpServletResponse response, String mauth, int age) {
        Cookie cookie = new Cookie(COOKIE_NAME, mauth);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    /**
     * 获取mauth Cookie
     * @param request  HttpServletRequest
     * @return mauth
     */
    public static String getMauth(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName()) && StringUtils.isNotEmpty(cookie.getValue())) {
                return new String(cookie.getValue());
            }
        }
        return null;
    }
}
