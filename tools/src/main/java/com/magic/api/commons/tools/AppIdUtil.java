package com.magic.api.commons.tools;

import com.magic.api.commons.ApiLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * APPID获取工具类
 * @author zz
 */
public class AppIdUtil {

    /**
     * APPID header
     */
    private static final String APP_ID_HEADER = "X-APP-ID";

    /**
     * 工具类私有构造
     */
    private AppIdUtil() {}

    /**
     * 获取APPID
     * @param request   HttpServletRequest
     * @return APPID
     */
    public static final int getAppId(HttpServletRequest request) {
        try {
            String header = request.getHeader(APP_ID_HEADER);
            if (StringUtils.isNoneBlank(header)) {
                return Integer.valueOf(header);
            }
        } catch (NumberFormatException e) {
            ApiLogger.error("获取APPID发生异常", e);
        }
        return -1;
    }
}
