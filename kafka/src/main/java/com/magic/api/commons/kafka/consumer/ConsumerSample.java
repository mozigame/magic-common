//package com.magic.api.commons.kafka.consumer;
//
//import java.nio.ByteBuffer;
//import java.util.*;
//
//import kafka.consumer.ConsumerConfig;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//
///**
// * ConsumerSample
// *
// * @author zj
// * @date 2017/7/12
// */
//public class ConsumerSample {
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
////brokerServer(kafka)ip地址,不需要把所有集群中的地址都写上，可是一个或一部分
//        props.put("bootstrap.servers", "202.153.207.179:8092,202.153.207.181:8092,202.153.207.182:8092");
////设置consumer group name,必须设置
//        props.put("group.id", "testtset222");
////设置自动提交偏移量(offset),由auto.commit.interval.ms控制提交频率
//        props.put("enable.auto.commit", "true");
////偏移量(offset)提交频率
//        props.put("auto.commit.interval.ms", "1000");
////设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
////如果采用latest，消费者只能得道其启动后，生产者生产的消息
//        props.put("auto.offset.reset", "earliest");
////设置心跳时间
//        props.put("session.timeout.ms", "30000");
////设置key以及value的解析（反序列）类
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
////订阅topic
//        consumer.subscribe(Arrays.asList("topicTest"));
//        while (true) {
//            //每次取100条信息
//            ConsumerRecords<String, String> records = consumer.poll(100);
//            for (ConsumerRecord<String, String> record : records)
//                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
//        }
//    }
//}
