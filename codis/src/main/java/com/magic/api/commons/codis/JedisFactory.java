package com.magic.api.commons.codis;

import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Codis
 * @author zz
 */
public class JedisFactory {

    /**
     * ZK地址:端口 例：ip:prot,ip:prot,ip:prot
     */
    private String zkAddr;

    /**
     * zk超时时间
     */
    private int zkSessionTimeoutMs;

    /**
     * proxy所在目录
     */
    private String zkProxyDir;

    /**
     * jedis池配置
     */
    private JedisPoolConfig jedisPoolConfig;

    /**
     * jedis池
     */
    private RoundRobinJedisPool roundRobinJedisPool;

    /**
     * ThreadLocal Jedis
     */
    private ThreadLocal<Jedis> jedisThreadLocal = new ThreadLocal<>();

    /**
     * 初始化
     * @param zkAddr                ZK地址:端口 例：ip:prot,ip:prot,ip:prot
     * @param zkSessionTimeoutMs    zk超时时间
     * @param zkProxyDir            proxy所在目录
     * @param jedisPoolConfig       jedis池配置
     */
    public JedisFactory(String zkAddr, int zkSessionTimeoutMs, String zkProxyDir, JedisPoolConfig jedisPoolConfig) {
        this.zkAddr = zkAddr;
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
        this.zkProxyDir = zkProxyDir;
        this.jedisPoolConfig = jedisPoolConfig;
        this.roundRobinJedisPool = RoundRobinJedisPool.create()
                .curatorClient(zkAddr, zkSessionTimeoutMs)
                .poolConfig(jedisPoolConfig)
                .zkProxyDir(zkProxyDir)
                .build();
    }

    /**
     * 获取Jedis
     * @return  Jedis
     */
    public Jedis getInstance() {
        Jedis jedis = jedisThreadLocal.get();
        if (null == jedis) {
            jedis = this.roundRobinJedisPool.getResource();
            jedisThreadLocal.set(jedis);
        }
        return jedis;
    }

    /**
     * 归还对象
     */
    public void close() {
        Jedis jedis = jedisThreadLocal.get();
        if (null != jedis) {
            jedis.close();
            jedisThreadLocal.remove();
        }
    }
}
