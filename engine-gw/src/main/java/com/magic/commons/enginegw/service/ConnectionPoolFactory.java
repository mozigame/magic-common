package com.magic.commons.enginegw.service;

import com.magic.api.commons.core.exception.ExceptionFactor;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;

/**
 * ConnectionPoolFactory2
 *
 * @author zj
 * @date 2017/5/15
 */
public class ConnectionPoolFactory {

    /**
     * 连接池
     */
    private GenericObjectPool<TProtocol> pool;

    /**
     * 构造函数
     * @param config
     * @param factory
     */
    public ConnectionPoolFactory(GenericObjectPoolConfig config, ConnectionFactory factory) {
        try {
            config.setMaxIdle(0);//必须设置为0
            pool = new GenericObjectPool<>(factory, config);
        }catch (Exception e){
            throw ExceptionFactor.INIT_ENGINEGW_CLIENT_EXCEPTION;
        }
    }

    /**
     * 从连接池中获取连接对象
     * @return
     * @throws Exception
     */
    public TProtocol getConnection() throws Exception {
        return pool.borrowObject();
    }

    /**
     * 将连接对象归还到连接池
     * @param protocol
     */
    public void releaseConnection(TProtocol protocol){
        pool.returnObject(protocol);
    }
}
