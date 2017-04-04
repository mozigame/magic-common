package com.magic.api.commons.tools;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 客户端SDK数据解析工具
 * @author zz
 */
public class ClientSDKUtil {

    /**
     * 工具类私有构造
     */
    private ClientSDKUtil() {}

    /**
     * 获取设备ID
     * @param request   HttpServletRequest
     * @return 设备ID
     */
    public static String getDeviceId(HttpServletRequest request) {
        return request.getHeader("ndeviceid");
    }

    /**
     * 获取客户端类型
     * @param request   HttpServletRequest
     * @return  客户端类型
     */
    public static String getClientId(HttpServletRequest request) {
        String value = request.getHeader("X-Client-ID");
        if (StringUtils.isNoneBlank(value)) {
            String[] array = value.split("-");
            return array[array.length-1];
        }
        return null;
    }

    /**
     * 获取客户端版本
     * @param request   HttpServletRequest
     * @return  客户端版本
     */
    public static String getVersionId(HttpServletRequest request) {
        String value = request.getHeader("X-WVersion");
        if (StringUtils.isNoneBlank(value)) {
            String[] array = value.split("-");
            return array[1];
        }
        return null;
    }
}
