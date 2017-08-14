package com.magic.api.commons.kafka.producer;

/**
 * Producer
 *
 * @author zj
 * @date 2017/8/9
 */
public interface Producer {

    /**
     * 消息发送
     * @param topic 话题
     * @param key   消息关键字
     * @param msg   消息内容
     * @return
     */
    boolean send(String topic, String key, String msg);
}
