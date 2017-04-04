package com.magic.api.commons.zk;

import org.apache.zookeeper.WatchedEvent;

/**
 * 节点监听
 *
 * @author zz
 */
public interface Listener {

    /**
     * 节点变化监听
     *
     * @param event WatchedEvent
     * @return true继续监听 false不再监听
     */
    boolean callback(WatchedEvent event);
}
