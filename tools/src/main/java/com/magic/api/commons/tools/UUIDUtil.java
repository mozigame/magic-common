package com.magic.api.commons.tools;

import java.util.UUID;

/**
 * UUIDͳ统一生成类
 * @author zz
 */
public final class UUIDUtil {

    /**
     * 工具类私有构造
     */
    private UUIDUtil(){}

    /**
     * 生成UUID
     * @return  UUID
     */
    public static final String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
