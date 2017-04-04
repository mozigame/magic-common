package com.magic.api.commons.mq.api;

/**
 * 消费者接口
 * @author zz
 */
public interface Consumer {

    /**
     * 处理消息
     * @param topic 话题
     * @param tags  标签
     * @param key   消息关键词(查询消息使用)
     * @param msg   消息内容
     * @return  处理true成功或false失败 失败会进行重试
     */
    boolean doit(String topic, String tags, String key, String msg);
}
