package com.magic.api.commons.mq.kafka;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.conf.ConsumerConf;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

public class ConsumerTask implements Runnable {

    /**
     * 消费者配置
     */
    private ConsumerConf<Properties> consumerConf;

    /**
     * 消费者对象 待回调
     */
    private Consumer consumers;

    /**
     * 初始化任务
     * @param consumerConf  消费者配置
     * @param consumers     消费者对象 待回调
     */
    public ConsumerTask(ConsumerConf<Properties> consumerConf, Consumer consumers) {
        this.consumerConf = consumerConf;
        this.consumers = consumers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                executeMsg();
            } catch (Exception e) {
                ApiLogger.error("consumer msg error", e);
            }
        }
    }

    /**
     * 处理具体单个消费者回调
     * @param consumerRecord    ConsumerRecord
     * @param consumers         Consumer
     * @param consumerConf      消费者配置
     * @return  是否成功消费
     */
    protected boolean consumerCallback(ConsumerRecord<String, String> consumerRecord, Consumer consumers, ConsumerConf<Properties> consumerConf) {
        String topic = consumerRecord.topic();
        String key = consumerRecord.key();
        String value = consumerRecord.value();
        StringBuilder builder = new StringBuilder();
        builder.append("consume msg");
        builder.append(" consumerName ").append(consumerConf.getConsumerName());
        builder.append(" topic ").append(topic);
        builder.append(" msg ").append(value);
        builder.append(" key ").append(key);
        ApiLogger.debug(String.valueOf(builder));
        try {
            if (!consumers.doit(topic, null, key, value)) {
                builder.append(" failed");
                ApiLogger.error(String.valueOf(builder));
                return true;
            }
            builder.append(" success");
            ApiLogger.info(String.valueOf(builder));
        } catch (Exception e) {
            builder.append(" error");
            ApiLogger.error(String.valueOf(builder), e);
            return true;
        }
        return false;
    }

    /**
     * 处理MQ消息
     */
    protected void executeMsg() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer(consumerConf.getData());
        consumer.subscribe(Arrays.asList(consumerConf.getTopic().getValue()));
        boolean booleanContinue = true;
        do {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);
            Set<TopicPartition> partitions = consumerRecords.partitions();
            for (TopicPartition partition : partitions) {
                List<ConsumerRecord<String, String>> records = consumerRecords.records(partition);
                long lastOffset = 0;
                boolean rollback = false;
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    lastOffset = consumerRecord.offset();
                    rollback = consumerCallback(consumerRecord, consumers, consumerConf);
                    if (rollback) {
                        break;
                    }
                }
                if (0 != lastOffset) {
                    long commitOffset;
                    if (rollback) {
                        booleanContinue = false;
                        commitOffset = lastOffset - 1;
                    } else {
                        commitOffset = lastOffset + 1;
                    }
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(commitOffset)));
                }
            }
        } while (booleanContinue);
    }
}
