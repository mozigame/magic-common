package com.magic.api.commons.config;

/**
 * 系统配置接口
 * @author zz
 */
public interface Config {

    /**
     * 获取配置
     * @param key   配置项名
     * @return  配置值
     */
    String get(String key);
}
