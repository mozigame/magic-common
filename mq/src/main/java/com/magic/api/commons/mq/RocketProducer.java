package com.magic.api.commons.mq;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.api.Topic;

/**
 * RocketMQ消息生产者
 * @author zz
 */
public class RocketProducer implements Producer {

    /**
     * RocketMQ
     */
    private DefaultMQProducer producer;

    /**
     * 初始化MQ
     * @param producerName      一组生产者唯一标识
     * @param nameServerAddr    NameServer地址
     */
    public RocketProducer(String producerName, String nameServerAddr) {
        producer = new DefaultMQProducer(producerName);
        producer.setNamesrvAddr(nameServerAddr);
        try {
            producer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("启动DefaultMQProducer发生异常", e);
        }
    }

    /**
     * 发送消息
     * @param topic 话题
     * @param key   标签
     * @param value 消息内容
     * @return  发送true成/false功失败
     */
    @Override
    public boolean send(Topic topic, String key, String value) {
        return send(topic, key, "", value);
    }

    /**
     * 发送消息
     * @param topic 话题
     * @param key   消息关键词(查询消息使用)
     * @param tag   标签
     * @param value 消息内容
     * @return  发送true成/false功失败
     */
    @Override
    public boolean send(Topic topic, String key, String tag, String value) {
        if (null == tag || "".equals(tag)) {
            tag = null;
        }
        Message message = new Message(topic.getValue(), tag, key, value.getBytes());
        SendResult result = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append("发送消息 topic ").append(topic.getValue()).append(" key ").append(key).append(" tag ").append(tag).append(" value ").append(value);
            result = producer.send(message);
            if (SendStatus.SEND_OK == result.getSendStatus()) {
                stringBuilder.append(" 成功 ").append(result);
                ApiLogger.debug(String.valueOf(stringBuilder));
                return true;
            } else {
                stringBuilder.append(" 失败 ").append(result);
                ApiLogger.debug(String.valueOf(stringBuilder));
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(String.valueOf(stringBuilder.append(" 失败")), e);
        }
    }
}
