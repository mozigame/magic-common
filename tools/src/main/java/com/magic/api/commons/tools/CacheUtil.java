package com.magic.api.commons.tools;

import com.magic.api.commons.ApiLogger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

/**
 * 缓存工具
 * @author zz
 */
public class CacheUtil {

    private static final CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build(true);

    /**
     * 获取缓存
     * @param cacheName     缓存名称 不可重复
     * @param key           缓存键的类型
     * @param value         缓存值的类型
     * @param heap          占用内存大小
     * @param <K>           缓存键的类型
     * @param <V>           缓存值的类型
     * @return  缓存
     */
    public static <K, V> Cache<K, V> getCache(String cacheName, Class<K> key, Class<V> value, int heap) {
        return getCache(cacheName, key, value, heap, MemoryUnit.MB);
    }

    /**
     * 获取缓存
     * @param cacheName     缓存名称 不可重复
     * @param key           缓存键的类型
     * @param value         缓存值的类型
     * @param heap          占用内存大小
     * @param memoryUnit    占用内存的单位
     * @see org.ehcache.config.units.MemoryUnit
     * @param <K>           缓存键的类型
     * @param <V>           缓存值的类型
     * @return  缓存
     */
    public static <K, V> Cache<K, V> getCache(String cacheName, Class<K> key, Class<V> value, int heap, MemoryUnit memoryUnit) {
        Cache<K, V> cache = CACHE_MANAGER.getCache(cacheName, key, value);
        if (null != cache) {
            return cache;
        }
        if (memoryUnit != MemoryUnit.MB && memoryUnit != MemoryUnit.KB) {
            throw new RuntimeException("请检查缓存单位 请使用MB KB");
        }
        synchronized (CacheUtil.class) {
            if (null != cache) {
                return cache;
            }
            cache = CACHE_MANAGER.createCache(cacheName, CacheConfigurationBuilder.newCacheConfigurationBuilder(key, value, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(heap, memoryUnit)));
            ApiLogger.debug("CacheUtil初始化缓存：" + cacheName + " Key " + key + " value " + value + " heap " + heap + " memoryUnit " + memoryUnit);
        }
        return cache;
    }



}
