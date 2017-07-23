package com.magic.commons.enginegw.service;

import com.magic.api.commons.ApiLogger;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * ConnectionFactory
 * 连接池管理的对象Transport的工厂类，GenericObjectPool会使用此类的create方法来创建Transport对象并放进pool里进行管理等操作。
 *
 * @author zj
 * @date 2017/5/15
 */
public class ConnectionFactory extends BasePooledObjectFactory<TProtocol>{

    private String ip;//服务器ip
    private int port;//服务器端口
    private boolean keepAlive = true;

    /**
     * 构造函数
     * @param ip 服务器IP
     * @param port 服务器PORT
     */
    public ConnectionFactory(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 创建TTransport类型对象方法
     * @return
     */
    @Override
    public TProtocol create() throws Exception{
        TSocket socket = new TSocket(ip, port);
        TFramedTransport transport = new TFramedTransport(socket);
        if (!transport.isOpen()){
            transport.open();
        }
        return new TBinaryProtocol(transport);
    }

    /**
     * 把TProtocol对象打包成池管理的对象PooledObject<TProtocol>
     * @param protocol
     * @return
     */
    @Override
    public PooledObject<TProtocol> wrap(TProtocol protocol) {
        return new DefaultPooledObject<>(protocol);
    }

    /**
     * 对象钝化(即：从激活状态转入非激活状态，returnObject时触发）
     *
     * @param pooledObject ignored
     */
    @Override
    public void passivateObject(PooledObject<TProtocol> pooledObject)
            throws Exception {
        if (!keepAlive){
            TTransport transport = pooledObject.getObject().getTransport();
            transport.flush();
            transport.close();
        }
    }

    /**
     *  对象激活(borrowObject时触发）
     *
     *  @param pooledObject ignored
     */
    @Override
    public void activateObject(PooledObject<TProtocol> pooledObject) throws Exception {
        TTransport transport = pooledObject.getObject().getTransport();
        if (!transport.isOpen()){
            transport.open();
        }
    }

    /**
     *  对象销毁(clear时会触发）
     *
     *  @param pooledObject ignored
     */
    @Override
    public void destroyObject(PooledObject<TProtocol> pooledObject)
            throws Exception  {
        passivateObject(pooledObject);
        pooledObject.markAbandoned();
    }

    /**
     * This implementation always returns {@code true}.
     *
     * @param pooledObject ignored
     *
     * @return {@code true}
     */
    @Override
    public boolean validateObject(PooledObject<TProtocol> pooledObject) {
        TTransport transport = pooledObject.getObject().getTransport();
        if (transport != null) {
            if (transport.isOpen()) {
                return true;
            }
            try {
                transport.open();
                return true;
            } catch (TTransportException e) {
                ApiLogger.error("validate object error.", e);
            }
        }
        return false;
    }
}
