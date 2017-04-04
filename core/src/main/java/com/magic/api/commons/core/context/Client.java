package com.magic.api.commons.core.context;


import java.io.Serializable;
import java.util.HashMap;

/**
 * 客户端相关数据
 * @author zz
 */
public class Client implements Serializable {

    private static final long serialVersionUID = -9025060813745501948L;

    /**
     * Android or IOS
     */
    private ClientType clientType;

    /**
     * 客户端版本
     */
    private ClientVersion clientVersion;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 平台ID 5实惠 5实惠 100-10000SaaS渠道
     */
    private int appId;

    public enum ClientType {

        IOS(1, "ios"), ANDROID(2, "android");

        private int value;

        private String name;

        private static HashMap<String, ClientType> mapping = new HashMap<String, ClientType>();

        ClientType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static ClientType get(String name) {
            for (ClientType clientType : ClientType.values()) {
                if (clientType.name.equals(name)) {
                    return clientType;
                }
            }
            return null;
        }
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(ClientVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
