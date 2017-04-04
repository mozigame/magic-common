package com.magic.api.commons.zk;

import java.util.List;

/**
 * Zookeeper工具
 * @author zz
 */
public interface Client {

    /**
     * 监听节点变化
     * @param path      节点
     * @param listener  监听回调
     */
    void listener(String path, Listener listener);

    /**
     * 创建节点
     * @param path  节点
     * @param data  数据
     * @return  true创建成功 false失败
     */
    boolean create(String path, String data);

    /**
     * 检查节点是否存在
     * @param path  节点
     * @return  true存在 false不存在
     */
    boolean checkExists(String path);

    /**
     * 获取节点下数据
     * @param path  节点
     * @return  数据
     */
    String get(String path);

    /**
     * 获取父节点节点下所有子节点
     * @param path  节点
     * @return
     */
    List<String> getChildren(String path);

    /**
     * 节点插入数据
     * @param path  节点
     * @param data  数据
     * @return  true成功 false失败
     */
    boolean set(String path, String data);

    /**
     * 删除节点以及子节点
     * @param path  节点
     */
    void delete(String path);

}
