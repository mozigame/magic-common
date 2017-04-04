package com.magic.api.commons.mq.conf;

/**
 * 消费者配置
 * @author zz
 */
public class MQBaseConfig {

    /**
     * nameServer地址
     */
    private String nameServerAddr;


    public String getNameServerAddr() {
        return nameServerAddr;
    }

    public void setNameServerAddr(String nameServerAddr) {
        this.nameServerAddr = nameServerAddr;
    }
}
