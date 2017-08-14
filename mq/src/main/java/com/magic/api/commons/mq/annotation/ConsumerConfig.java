package com.magic.api.commons.mq.annotation;


import com.magic.api.commons.mq.api.Topic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MQ消费者基本信息配置注解
 * @author zz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConsumerConfig {

    /**
     * 连接的server RocketMQ为NameServer Kafka为broker bootstrap.servers
     * @return 连接的server
     */
    String server() default "";

    /**
     * 一组消费者唯一标示
     * @return  一组消费者唯一标示
     */
    String consumerName();

    /**
     * topic
     * @return  topic
     */
    Topic topic();

    /**
     * 标签
     * @return 标签
     */
    String tag() default "";

}
