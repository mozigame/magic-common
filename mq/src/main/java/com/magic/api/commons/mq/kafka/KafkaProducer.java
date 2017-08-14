package com.magic.api.commons.mq.kafka;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.api.Producer;
import com.magic.api.commons.mq.api.Topic;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;


/**
 * Kafka消息生产者
 * @author zz
 */
public class KafkaProducer implements Producer {

    /**
     * Kafka
     */
    private org.apache.kafka.clients.producer.KafkaProducer kafkaProducer;

    /**
     * 初始化MQ
     * @param props kafka配置
     * @see org.apache.kafka.clients.producer.ProducerConfig
     */
    public KafkaProducer(Properties props) {
        kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer(props);
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
        return send(topic, key, null, value);
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
        final String finalTag = tag;
        kafkaProducer.send(new ProducerRecord<String, String>(topic.getValue(), key, value), (recordMetadata, e) -> {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("send msg topic ").append(topic.getValue()).append(" key ").append(key).append(" tag ").append(finalTag).append(" value ").append(value);
            if (null != recordMetadata && null == e) {
                stringBuilder.append(" success ").append("offset ").append(recordMetadata.offset()).append(" partition ").append(recordMetadata.partition());
                ApiLogger.debug(String.valueOf(stringBuilder));
            } else {
                stringBuilder.append(" failed ");
                ApiLogger.error(String.valueOf(stringBuilder), e);
            }
        });
        return true;
    }
}
