package com.magic.commons.enginegw.bean;

/**
 * ThriftServerInfo
 *
 * @author zj
 * @date 2017/5/15
 */
public class ThriftServerInfo {

    private String ip;//服务器ip
    private int port;//服务器端口
    private int timeout;//连接超时时间

    /**
     * 构造函数
     * @param ip
     * @param port
     */
    public ThriftServerInfo(String ip, int port) {
        this(ip, port, 3000);
    }

    /**
     * 构造函数
     * @param ip
     * @param port
     * @param timeout
     */
    public ThriftServerInfo(String ip, int port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
