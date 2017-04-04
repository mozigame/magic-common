package com.magic.api.commons.config;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.CacheUtil;
import com.magic.api.commons.zk.Listener;
import com.magic.api.commons.zk.ZKClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.WatchedEvent;
import org.ehcache.Cache;

/**
 * ZK配置工具
 * @author zz
 */
public class ZKConfig implements Config {

    /**
     * 配置文件基础节点
     */
    private String basePath;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置信息最大占用内存
     */
    private int heap;

    /**
     * IP和端口 例：ip:prot,ip:prot,ip:prot
     */
    private String zkConnect;

    /**
     * 配置信息缓存
     */
    private Cache<String, String> cache;

    /**
     * ZK客户端
     */
    private ZKClient zkClient;

    /**
     * ZK路径分隔符
     */
    private static final String PATH_SPLIT = "/";

    /**
     * 构造
     * @param configName    配置名称
     * @param basePath      配置文件基础节点
     * @param heap          占用内存
     * @param zkConnect     IP和端口 例：ip:prot,ip:prot,ip:prot
     */
    public ZKConfig(String configName, String basePath, int heap, String zkConnect) {
        if (!PATH_SPLIT.equals(basePath.substring(basePath.length() - 1))) {
            basePath += PATH_SPLIT;
        }
        this.configName = configName;
        this.basePath = basePath;
        this.heap = heap;
        this.cache = CacheUtil.getCache(configName, String.class, String.class, heap);
        this.zkClient = new ZKClient(zkConnect);
    }

    /**
     * 获取配置
     * @param key   配置项名
     * @return  配置值
     */
    @Override
    public String get(String key) {
        String value = cache.get(key);
        if (null != value) {
            return value;
        }
        synchronized (key) {
            if (null != value) {
                return value;
            }
            String conofigPath = basePath + key;
            value = zkClient.get(conofigPath);
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException("缺少配置项 configName " + configName + " basePath " + basePath + " Key " + key);
            }
            zkClient.listener(conofigPath, new Listener() {
                @Override
                public boolean callback(WatchedEvent event) {
                    String[] paths = event.getPath().split(PATH_SPLIT);
                    String path = paths[paths.length - 1];
                    ApiLogger.debug("配置发生变化 configName " + configName + " key " + path);
                    cache.remove(path);
                    return true;
                }
            });
            cache.put(key, value);
            ApiLogger.debug("加载配置 configName " + configName + " key " + key + " value " + value);
        }
        return value;
    }

}
