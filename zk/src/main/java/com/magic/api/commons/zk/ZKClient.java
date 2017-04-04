package com.magic.api.commons.zk;

import com.magic.api.commons.ApiLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Zookeeper工具
 * @author zz
 */
public class ZKClient implements Client {

    private CuratorFramework client;

    /**
     * 初始化工具
     * @param connect IP和端口 例：ip:prot,ip:prot,ip:prot
     */
    public ZKClient(String connect) {
        this(connect, 1000, 3);
    }

    /**
     * 初始化工具
     * @param connect           IP和端口 例：ip:prot,ip:prot,ip:prot
     * @param baseSleepTimeMs   超时时间
     * @param maxRetries        最大重试次数
     */
    public ZKClient(String connect, int baseSleepTimeMs, int maxRetries) {
        this.client = CuratorFrameworkFactory.newClient(
                connect, new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries));
        client.start();
    }

    /**
     * 监听节点变化
     * @param path      节点
     * @param listener  监听回调
     */
    public void listener(final String path, final Listener listener) {
        final Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                if(!listener.callback(event)) {
                    return;
                }
                try {
                    client.checkExists().usingWatcher(this).forPath(path);
                } catch (Exception e) {
                    ApiLogger.error("监听节点 " + path + " 发生异常 " + listener, e);
                }
            }
        };

        try {
            client.checkExists().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            ApiLogger.error("监听节点 " + path + " 发生异常 " + listener, e);
        }
    }

    /**
     * 创建节点
     * @param path  节点
     * @param data  数据
     * @return  true创建成功 false失败
     */
    public boolean create(String path, String data) {
        String forPath = null;
        try {
            forPath = client.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
        } catch (Exception e) {
            ApiLogger.error("创建节点发生异常 " + path + " data " + data, e);
        }
        return StringUtils.isNotEmpty(forPath);
    }

    /**
     * 检查节点是否存在
     * @param path  节点
     * @return  true存在 false不存在
     */
    public boolean checkExists(String path) {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(path);
        } catch (Exception e) {
            ApiLogger.error("检查节点发生异常 " + path, e);
        }
        return null != stat;
    }

    /**
     * 获取节点下数据
     * @param path  节点
     * @return  数据
     */
    public String get(String path) {
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().forPath(path);
        } catch (Exception e) {
            ApiLogger.error("获取节点数据发生异常 " + path, e);
        }
        return null != bytes ? new String(bytes) : null;
    }

    /**
     * 获取父节点节点下所有子节点
     * @param path  节点
     * @return
     */
    public List<String> getChildren(String path) {
        List<String> children = null;
        try {
            children = client.getChildren().forPath(path);
        } catch (Exception e) {
            ApiLogger.error("获取节点数据发生异常 " + path, e);
        }
        return children;
    }

    /**
     * 节点插入数据
     * @param path  节点
     * @param data  数据
     * @return  true成功 false失败
     */
    public boolean set(String path, String data) {
        Stat stat = null;
        try {
            stat = client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            ApiLogger.error("节点插入数据发生异常 " + path + " data " + data, e);
        }
        return null != stat;
    }

    /**
     * 删除节点以及子节点
     * @param path  节点
     */
    public void delete(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            ApiLogger.error("删除节点发生异常 " + path, e);
        }
    }
}
