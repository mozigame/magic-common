package com.magic.api.commons.mq.conf;

import com.magic.api.commons.mq.api.Topic;

/**
 * 消费者配置
 * @author zz
 */
public class ConsumerConf {

    public ConsumerConf() {
    }

    public ConsumerConf(String nameServer) {
        this.nameServer = nameServer;
    }

    /**
     * 一组消费者唯一标示
     */
    private String consumerName;

    /**
     * nameServer地址
     */
    private String nameServer;

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

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
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
                ", nameServer='" + nameServer + '\'' +
                ", topic=" + topic +
                ", tag='" + tag + '\'' +
                '}';
    }
}
