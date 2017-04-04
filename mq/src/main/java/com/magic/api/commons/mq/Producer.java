package com.magic.api.commons.mq;

import com.magic.api.commons.mq.api.Topic;

public interface Producer {

    /**
     * 发送消息
     * @param topic 话题
     * @param key   消息关键词(查询消息使用)
     * @param value 消息内容
     * @return  发送true成/false功失败
     */
    boolean send(Topic topic, String key, String value);

    /**
     * 发送消息
     * @param topic 话题
     * @param key   消息关键词(查询消息使用)
     * @param tag   标签
     * @param value 消息内容
     * @return  发送true成/false功失败
     */
    boolean send(Topic topic, String key, String tag, String value);
}
