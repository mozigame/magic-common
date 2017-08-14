package com.magic.api.commons.mq.kafka;


import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.conf.ConsumerConf;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费者配置主入口
 * @author zz
 */
public class ConsumerMain implements ApplicationContextAware {

    /**
     * 消费者实现类列表
     */
    protected Set<Consumer> consumers;

    /**
     * ApplicationContext
     */
    protected ApplicationContext applicationContext;

    /**
     * 线程池
     */
    protected ExecutorService executorService;

    /**
     * kafka配置
     */
    protected Properties props;

    /**
     * 初始化消费者 server会被消费者注解覆盖
     * @param props             kafka配置
     * @see org.apache.kafka.clients.consumer.ConsumerConfig
     */
    public ConsumerMain(Properties props) {
        this(null, props);
    }

    /**
     * 初始化消费者 server会被消费者注解覆盖
     * @param consumers         消费者实现类class
     * @param props             kafka配置
     * @see org.apache.kafka.clients.consumer.ConsumerConfig
     */
    public ConsumerMain(Set<Consumer> consumers, Properties props) {
        if (MapUtils.isEmpty(props)) {
            throw new RuntimeException("Properties con not be null");
        }
        this.consumers = consumers;
        this.props = props;
        //设置手动提交 用于消息重试
        this.props.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    }

    /**
     * 初始化消费者 server会被消费者注解覆盖
     *
     * 每个消费者独立线程不适消费者过多情况 如有需要可改为一个或几个线程处理所有topic
     * <a href="mailto:admin@hezhouzhou.com?subject%3d%e5%b7%a5%e5%85%b7%e8%b0%83%e6%95%b4%26body%3dKafka%e6%b6%88%e8%b4%b9%e8%80%85%e5%a4%9a%e7%ba%bf%e7%a8%8b%e6%94%b9%e5%8d%95%e7%ba%bf%e7%a8%8b">邮箱联系</a>
     * @throws Exception    Exception
     */
    public void start() {
        if (CollectionUtils.isEmpty(consumers)) {
            return;
        }
        executorService = Executors.newFixedThreadPool(consumers.size());
        Properties properties = copyProperties(props);
        StringBuilder stringBuilder = new StringBuilder("----------------init kafka consumer--------------\n");
        for (Consumer consumer : consumers) {
            stringBuilder.append(consumer.getClass()).append("\n");
            ConsumerConf consumerConf = getConsumerConf(properties, consumer);
            executorService.execute(new ConsumerTask(consumerConf, consumer));
        }
        stringBuilder.append("-------------successs. count ").append(consumers.size()).append(" -------------");
        ApiLogger.info(String.valueOf(stringBuilder));
    }

    /**
     * 防止多server冲突拷贝
     * @param props kafka配置
     * @return  kafka配置
     */
    protected Properties copyProperties(Properties props) {
        Properties properties = new Properties();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            properties.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        return properties;
    }

    /**
     * 获取客户端配置
     * @param props             kafka配置
     * @param consumeClass      消费者Class
     * @return  客户端配置
     */
    protected ConsumerConf getConsumerConf(Properties props, Consumer consumeClass) {
        ConsumerConfig consumerConfig = consumeClass.getClass().getAnnotation(ConsumerConfig.class);
        if (null == consumerConfig || !(consumerConfig instanceof ConsumerConfig)) {
            throw new RuntimeException(consumeClass.getClass().toString() + " 没有找到注解 " + ConsumerConfig.class.toString());
        }
        props.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, consumerConfig.consumerName());
        ConsumerConf<Properties> consumerConf = new ConsumerConf();
        if (null != consumerConfig.server() && !"".equals(consumerConfig.server())) {
            props.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerConfig.server());
            consumerConf.setData(props);
        } else {
            consumerConf.setData(props);
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
    protected void check(ConsumerConf consumerConf, Class<?> consumeClass) {
        if (null == consumerConf.getConsumerName() || "".equals(consumerConf.getConsumerName())) {
            throw new RuntimeException(consumeClass.toString() + " ConsumerName不能为空");
        }
        if (null == consumerConf.getTopic()) {
            throw new RuntimeException(consumeClass.toString() + " Topic不能为空");
        }
    }


    /**
     * 获取ApplicationContext
     * @param applicationContext    ApplicationContext
     * @throws BeansException       BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        if (null != consumers) {
            return;
        }
        synchronized (this) {
            if (null != consumers) {
                return;
            }
            Map<String, Consumer> beansOfType = applicationContext.getBeansOfType(Consumer.class);
            consumers = new HashSet<>(beansOfType.values());
        }
    }
}