//package com.magic.api.commons.kafka.producer;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.magic.api.commons.ApiLogger;
//import org.apache.kafka.clients.producer.*;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//
//import java.util.Properties;
//import java.util.concurrent.ExecutionException;
//
///**
// * KProducer
// * Kafkash
// * @author zj
// * @date 2017/8/8
// */
//public class KProducer {
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "202.153.207.179:8092,202.153.207.181:8092,202.153.207.182:8092");
//       // props.put("acks", "all");
//        props.put("retries", 0);
//        //props.put("batch.size", 0);
//        props.put("linger.ms", 1);
//        //props.put("buffer.memory", 33554432);
//        props.put("producer.type", "async");
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        Producer<String,String> producer = new KafkaProducer<String, String>(props);
//        ProducerRecord<String,String> record = new ProducerRecord<String,String>("topicTest", "msgId1", "{\"name\":\"test1111\"}");
//        try {
//            RecordMetadata recordMetadata = producer.send(record).get();
//            System.out.println(recordMetadata);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        /*producer.send(record, (metadata, e) -> {
//            if (e != null)
//                ApiLogger.error("the producer has a error:" + e.getMessage());
//            else {
//                System.out.println("The offset of the record we just sent is: " + metadata.offset());
//                System.out.println("The partition of the record we just sent is: " + metadata.partition());
//            }
//
//        });
//
//        *//*try {
//            Thread.sleep(1000);
//        }catch (Exception e){
//
//        }*/
//    }
//
//}
