package com.magic.api.commons.core.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * 异常统一定义
 * @author zz
 */
public class ExceptionFactor {

    /**
     * 系统code须统一定义，暂定规则如下：
     * @see ：http://10.0.8.173/zentao/doc-view-204.html
     */
	private static int DEFAULT_SYSTEMCODE = 0;


    /**
     * 默认错误
     */
    public static final CommonException DEFAULT_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 0, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "system error!", "对不起,服务器内部发生错误,请稍后再试.");

    /**
     * 认证失败
     */
    public static final CommonException AUTH_FAILED_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 1, HttpServletResponse.SC_OK, "auth failed!", "认证失败!");

    /**
     * mauth超时
     */
    public static final CommonException TOKEN_EXPIRES_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 2, HttpServletResponse.SC_OK, "token expires!", "token失效!");

    /**
     * 无效用户ID
     */
    public static final CommonException INVALID_UID_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 3, HttpServletResponse.SC_OK, "invalid uid!", "无效用户ID!");

    /**
     * Matrix uid header 为空
     */
    public static final CommonException MATRIX_UID_HEADER_IS_EMPTY_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 4, HttpServletResponse.SC_OK, "Matrix uid header is empty.", "Matrix uid header 为空.!");

    /**
     * 非法版本规则
     */
    public static final CommonException VERSION_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 5, HttpServletResponse.SC_OK, "version error!", "非法版本规则!");

    /**
     * 非法异常Code
     */
    public static final CommonException ERROR_CODE_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 6, HttpServletResponse.SC_OK, "exception code error!", "非法异常Code!");

    /**
     * 找不到验证token
     */
    public static final CommonException MISS_AUTH_PROPERTIES_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 7, HttpServletResponse.SC_OK, "miss auth properties!", "找不到权限控制配置文件c!");

    /**
     * 未知环境变量
     */
    public static final CommonException UNKNOWN_ENV_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 8, HttpServletResponse.SC_OK, "unknown env!", "未知环境变量!");

    /**
     * 非法来源
     */
    public static final CommonException ILLEGAL_SOURCE_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 9, HttpServletResponse.SC_OK, "illegal source!", "非法来源!");

    /**
     * missIP
     */
    public static final CommonException MISS_IP_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 10, HttpServletResponse.SC_OK, "miss ip!", "找不到IP!");

    /**
     * 找不到authService
     */
    public static final CommonException MISS_AUTHSERVICE_CLASS_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 11, HttpServletResponse.SC_OK, "miss auth service class!", "找不到authService!");

    /**
     * 缺少权限验证方式
     * @see com.magic.api.commons.core.auth.Access.AccessType
     */
    public static final CommonException MISS_ACCESS_TYPE_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 12, HttpServletResponse.SC_OK, "miss access type!", "缺少权限验证方式!");

    /**
     * 锁异常
     */
    public static final CommonException LOCK_EXCEPTION = new CommonException(
            CommonException.ERROR_LEVEL_SYSTEM, DEFAULT_SYSTEMCODE, 13, HttpServletResponse.SC_OK, "lock exception!", "锁异常!");
}