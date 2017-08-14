package com.magic.api.commons.mq.conf;

import com.magic.api.commons.mq.api.Topic;

/**
 * 消费者配置
 * @author zz
 */
public class ConsumerConf<Z> {

    /**
     * 一组消费者唯一标示
     */
    private String consumerName;

    /**
     * RocketMQ为nameServer地址 Kafka为properties
     */
    private Z data;

    /**
     * 话题
     */
    private Topic topic;

    /**
     * 标签
     */
    private String tag;

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Z getData() {
        return data;
    }

    public void setData(Z data) {
        this.data = data;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ConsumerConf{" +
                "consumerName='" + consumerName + '\'' +
                ", data=" + data +
                ", topic=" + topic +
                ", tag='" + tag + '\'' +
                '}';
    }
}
