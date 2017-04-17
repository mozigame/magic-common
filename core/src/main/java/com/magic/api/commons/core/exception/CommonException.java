package com.magic.api.commons.core.exception;

import com.magic.api.commons.ApiLogger;

/**
 * @author zz
 */
public class CommonException extends RuntimeException {

    /**
     * 系统级异常
     */
    public static final int ERROR_LEVEL_SYSTEM = 1;

    /**
     * 服务级异常
     */
    public static final int ERROR_LEVEL_SERVICE = 2;

    /**
     * 最大异常Code
     */
    public static final long LEVE_MAX = 9;

    /**
     * 最大系统异常Code
     */
    public static final long MAX = 99;

    /**
     * 最大系统业务异常Code
     */
    public static final long SERVICE_MAX = 999;

    /**
     * 客户端
     */
    public static final long SHOW_MAX = 9;

    public static final long LEVEL_FACTOR = 1000000;

    public static final int SYSTEMCODE_FACTOR = 10000;

    public static final int SHOWCODE_FACTOR = 1000;


    /**
     * 1系统级异常 2服务级异常
     * @see com.magic.api.commons.core.exception.CommonException#ERROR_LEVEL_SYSTEM
     * @see com.magic.api.commons.core.exception.CommonException#ERROR_LEVEL_SERVICE
     */
    private int level;

    /**
     * 系统异常Code 例：用户系统 101
     */
    private int systemCode;

    /**
     * 业务异常Code 例：密码错误 1001
     */
    private int serviceCode;

    /**
     * 客户端展示方式Code 例：闪现3秒 1
     */
    private int showCode;

    /**
     * http状态码
     */
    private int httpCode;

    /**
     * 错误提示
     */
    private String enMessage;

    /**
     * 中文错误提示
     */
    private String cnMessage;


    /**
     * 构造异常
     * @param level         异常级别
     * @see com.magic.api.commons.core.exception.CommonException#level
     * @param systemCode
     * @param serviceCode
     * @param showCode
     * @param httpCode
     * @param enMessage
     * @param cnMessage
     */
    public CommonException(int level, int systemCode, int showCode, int serviceCode, int httpCode, String enMessage, String cnMessage) {
        super(enMessage);
        if (level > LEVE_MAX || systemCode > MAX || showCode > SHOW_MAX || serviceCode > SERVICE_MAX ) {
            ApiLogger.error("exception code error! " + cnMessage + " level " + level + " systemCode " + systemCode + " serviceCode " + serviceCode + " showCode " + showCode);
            throw ExceptionFactor.ERROR_CODE_EXCEPTION;
        }
        this.level = level;
        this.systemCode = systemCode;
        this.showCode = showCode;
        this.serviceCode = serviceCode;
        this.httpCode = httpCode;
        this.enMessage = enMessage;
        this.cnMessage = cnMessage;
    }

    /**
     * 获取错误编码
     * @return  错误编码
     */
    public long getErrorCode() {
        return LEVEL_FACTOR * level + SYSTEMCODE_FACTOR * systemCode + SHOWCODE_FACTOR * showCode + serviceCode;
    }

    public int getLevel() {
        return level;
    }

    public int getSystemCode() {
        return systemCode;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public String getCnMessage() {
        return cnMessage;
    }
}
