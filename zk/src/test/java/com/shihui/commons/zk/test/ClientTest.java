package com.magic.api.commons.zk.test;

import com.magic.api.commons.zk.Client;
import com.magic.api.commons.zk.Listener;
import com.magic.api.commons.zk.ZKClient;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import java.util.List;

/**
 * ZKClient单元测试
 * @author zz
 */
public class ClientTest {

    private Client client = new ZKClient("192.168.36.125:2181");

    private static final String PATH = "/zkTest";
    private static final String DATA = "DemoData";

    /**
     * 监听节点变化
     */
    @Test
    public void listenerTest() {
        assert client.create(PATH, DATA);
        client.listener(PATH, new Listener() {
            @Override
            public boolean callback(WatchedEvent event) {
                //do something
                return true;
            }
        });
        client.set(PATH, DATA);
        client.delete(PATH);
    }

    /**
     * 创建节点
     */
    @Test
    public void create() {
        assert client.create(PATH, DATA);
        client.delete(PATH);
    }


    /**
     * 获取节点下数据
     */
    @Test
    public void get() {
        assert client.create(PATH, DATA);
        assert DATA.equals(client.get(PATH));
        client.delete(PATH);
    }

    /**
     * 获取父节点节点下所有子节点
     */
    @Test
    public void getChildren() {
        assert client.create(PATH + PATH, DATA);
        List<String> children = client.getChildren(PATH);
        assert 1 == children.size();
        client.delete(PATH);
    }

    /**
     * 节点插入数据
     */
    @Test
    public void set() {
        assert client.create(PATH, DATA);
        assert client.set(PATH, PATH);
        client.delete(PATH);
    }

    /**
     * 删除节点以及子节点
     */
    @Test
    public void delete() {
        assert client.create(PATH, DATA);
        client.delete(PATH);
    }

}
