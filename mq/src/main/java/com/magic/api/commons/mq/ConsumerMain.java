package com.magic.api.commons.mq;


import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.conf.ConsumerConf;

import java.util.List;

/**
 * 消费者配置主入口
 */
public class ConsumerMain {

    /**
     * 消费者实现类列表
     */
    private List<Consumer> consumers;

    /**
     * 初始化消费者 nameServerAddr取自消费者注解
     * @param consumers     消费者实现类class
     */
    public ConsumerMain(List<Consumer> consumers) {
        this(consumers, null);
    }

    /**
     * 初始化消费者 nameServerAddr会被消费者注解覆盖
     * @param consumers         消费者实现类class
     * @param nameServerAddr    nameServerAddr地址
     */
    public ConsumerMain(List<Consumer> consumers, String nameServerAddr) {
        this.consumers = consumers;
        try {
            doSomething(consumers, nameServerAddr);
        } catch (Exception e) {
            throw new RuntimeException("初始化消息消费者发生异常", e);
        }
    }

    private void doSomething(List<Consumer> consumers, String nameServerAddr) throws Exception {
        for (Consumer consumer : consumers) {
            ConsumerConf consumerConf = getConsumerConf(nameServerAddr, consumer);
            executeMsg(consumerConf, consumer);
        }
    }

    /**
     * 获取客户端配置
     * @param nameServerAddr    nameServer地址
     * @param consumeClass      消费者Class
     * @return  客户端配置
     */
    private ConsumerConf getConsumerConf(String nameServerAddr, Consumer consumeClass) {
        ConsumerConfig consumerConfig = consumeClass.getClass().getAnnotation(ConsumerConfig.class);
        if (null == consumerConfig || !(consumerConfig instanceof ConsumerConfig)) {
            throw new RuntimeException(consumeClass.getClass().toString() + " 没有找到注解 " + ConsumerConfig.class.toString());
        }
        ConsumerConf consumerConf;
        if (null != consumerConfig.nameServer() && !"".equals(consumerConfig.nameServer())) {
            consumerConf = new ConsumerConf(consumerConfig.nameServer());
        } else {
            if (null == nameServerAddr || "".equals(nameServerAddr)) {
                throw new RuntimeException("nameServerAddr不能为空");
            }
            consumerConf = new ConsumerConf(nameServerAddr);
        }
        consumerConf.setConsumerName(consumerConfig.consumerName());
        consumerConf.setTopic(consumerConfig.topic());
        consumerConf.setTag(consumerConfig.tag());
        check(consumerConf, consumeClass.getClass());
        return consumerConf;
    }

    /**
     * 校验消费必填数据
     * @param consumerConf
     * @param consumeClass
     */
    private void check(ConsumerConf consumerConf, Class<?> consumeClass) {
        if (null == consumerConf.getNameServer() || "".equals(consumerConf.getNameServer())) {
            throw new RuntimeException(consumeClass.toString() + " NameServer不能为空");
        }
        if (null == consumerConf.getConsumerName() || "".equals(consumerConf.getConsumerName())) {
            throw new RuntimeException(consumeClass.toString() + " ConsumerName不能为空");
        }
        if (null == consumerConf.getTopic()) {
            throw new RuntimeException(consumeClass.toString() + " Topic不能为空");
        }
    }

    /**
     * 处理MQ消息
     * @param consumerConf  消费者配置
     * @param consumers     消费者
     * @throws MQClientException   MQClientException
     */
    private void executeMsg(final ConsumerConf consumerConf, final Consumer consumers) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerConf.getConsumerName());
        consumer.setNamesrvAddr(consumerConf.getNameServer());
        consumer.subscribe(consumerConf.getTopic().getValue(), null == consumerConf.getTag() || "".equals(consumerConf.getTag()) ? null : consumerConf.getTag());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.registerMessageListener(
                new MessageListenerConcurrently() {
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                        String topic = null;
                        String msg = null;
                        String tags = null;
                        String keys = null;
                        try {
                            Message message = list.get(0);
                            topic = message.getTopic();
                            msg = new String(message.getBody());
                            tags = message.getTags();
                            keys = message.getKeys();

                            StringBuilder builder = new StringBuilder();
                            builder.append("待处理消息");
                            builder.append(" consumerName ").append(consumerConf.getConsumerName());
                            builder.append(" topic ").append(topic);
                            builder.append(" msg ").append(msg);
                            builder.append(" tags ").append(tags);
                            builder.append(" keys ").append(keys);
                            ApiLogger.debug(String.valueOf(builder));
                            boolean flag = consumers.doit(topic, tags, keys, msg);
                            return flag ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        } catch (Exception e) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(consumerConf.toString()).append(" 处理消息发生异常 ");
                            builder.append(" topic ").append(topic);
                            builder.append(" msg ").append(msg);
                            builder.append(" tags ").append(tags);
                            builder.append(" keys ").append(keys);
                            ApiLogger.error(String.valueOf(builder), e);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                }
        );
        consumer.start();
    }
}