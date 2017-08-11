package com.magic.api.commons.kafka.producer;

import com.magic.api.commons.ApiLogger;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * KafkaProducer
 * Kafka生产者
 * @author zj
 * @date 2017/8/9
 */
public class KafkaProducer implements Producer{

    /**
     * 生产者
     */
    private org.apache.kafka.clients.producer.Producer<String,String> producer;


    /**
     * 构造函数
     * @param serverAddress
     */
    public KafkaProducer(String serverAddress){
        Properties props = new Properties();
        props.put("bootstrap.servers", serverAddress);
        props.put("retries", 0);
        props.put("linger.ms", 1);
        props.put("producer.type", "async");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean send(String topic, String key, String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, msg);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append("send kafka topic ").append(topic).append(" key ").append(key).append(" msg ").append(msg);
            RecordMetadata recordMetadata = producer.send(record).get();
            stringBuilder.append(" success ").append(" offset ").append(recordMetadata.offset()).append(" partition ").append(recordMetadata.partition());
            ApiLogger.info(String.valueOf(stringBuilder));
            return true;
        } catch (Exception e) {
            ApiLogger.error(String.valueOf(stringBuilder.append(" fail")), e);
            return false;
        }
    }
}
