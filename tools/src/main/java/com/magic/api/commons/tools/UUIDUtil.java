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

    /**
     * 生成code   12位
     * @return
     */
    public static final String getCode(){
        return RC4.encry_RC4_string("100000", UUID.randomUUID().toString().replaceAll("-","")).toUpperCase();
    }
    /**
     * 生成code   6位
     * @return
     */
    public static final String getSpreadCode(){
        return RC4.encry_RC4_string("100", UUID.randomUUID().toString().replaceAll("-","")).toLowerCase();
    }


}
