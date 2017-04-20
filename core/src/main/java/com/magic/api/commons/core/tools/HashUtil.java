package com.magic.api.commons.core.tools;

import org.apache.commons.lang3.StringUtils;

/**
 * 哈希工具
 */
public class HashUtil {

    /**
     * 工具类 私有构造
     */
    private HashUtil() {}

    /**
     * 获取index
     * @param key   待处理数据
     * @return index
     */
    public static int getIndex(String key) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("key不可为空");
        }
        int hashCode = key.hashCode();
        if (Integer.MIN_VALUE != hashCode) {
            return Math.abs(hashCode) % 100;
        } else {
            return Math.abs(hashCode % 100);
        }
    }
}
