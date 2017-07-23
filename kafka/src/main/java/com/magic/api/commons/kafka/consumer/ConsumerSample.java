//package com.magic.api.commons.kafka.consumer;
//
//import java.nio.ByteBuffer;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import kafka.consumer.Consumer;
//import kafka.consumer.ConsumerConfig;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.message.Message;
//import kafka.message.MessageAndMetadata;
//
///**
// * ConsumerSample
// *
// * @author zj
// * @date 2017/7/12
// */
//public class ConsumerSample {
//
//
//    public static void main(String[] args) {
//        Properties props = new Properties();
//        props.put("zk.connect", "localhost:2181");
//        props.put("groupid", "test_group");
//        props.put("zookeeper.session.timeout.ms", "4000");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("auto.offset.reset", "smallest");
//        //序列化类
//        //props.put("serializer.class", "kafka.serializer.StringEncoder");
//
//
//        // Create the connection to the cluster
//        ConsumerConfig consumerConfig = new ConsumerConfig(props);
//        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
//
//        // create 4 partitions of the stream for topic “test-topic”, to allow 4 threads to consume
//        HashMap<String, Integer> map = new HashMap<String, Integer>();
//        map.put("test-topic", 4);
//        Map<String, List<KafkaStream<Message>>> topicMessageStreams =
//                consumerConnector.createMessageStreams(map);
//        List<KafkaStream<Message>> streams = topicMessageStreams.get("test-topic");
//
//        // create list of 4 threads to consume from each of the partitions
//        ExecutorService executor = Executors.newFixedThreadPool(4);
//
//        // consume the messages in the threads
//        for (final KafkaStream<Message> stream : streams) {
//            executor.submit(new Runnable() {
//                public void run() {
//                    for (MessageAndMetadata msgAndMetadata : stream) {
//                        // process message (msgAndMetadata.message())
//                        System.out.println("topic: " + msgAndMetadata.topic());
//                        Message message = (Message) msgAndMetadata.message();
//                        ByteBuffer buffer = message.payload();
//                        byte[] bytes = new byte[message.payloadSize()];
//                        buffer.get(bytes);
//                        String tmp = new String(bytes);
//                        System.out.println("message content: " + tmp);
//                    }
//                }
//            });
//        }
//
//    }
//    }
//
//
//}
