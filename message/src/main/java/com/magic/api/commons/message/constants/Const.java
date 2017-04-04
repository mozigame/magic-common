package com.magic.api.commons.message.constants;

import com.magic.api.commons.tools.EnvUtil;

/**
 * Const
 * 常量
 * @author zj
 * @date 2016/8/22
 */
public class Const {

    public static final String SEND_NOTICE_URL; //网关直播间消息推送
    public static final String SEND_OFFLINE_URL; //网关离线消息

    static {
        EnvUtil.Env env = EnvUtil.getEnv();
        switch (env){
            case test:
                SEND_NOTICE_URL = "http://10.0.8.31/push/admin/sendnotice";
                SEND_OFFLINE_URL = "http://10.0.8.31/push/only";
                break;
            case dev:
                SEND_NOTICE_URL = "http://10.0.8.174/push/admin/sendnotice";
                SEND_OFFLINE_URL = "http://10.0.8.174/push/only";
                break;
            case prod:
                SEND_NOTICE_URL = "http://gw.51zb.cn/push/admin/sendnotice";
                SEND_OFFLINE_URL = "http://gw.51zb.cn/push/only";
                break;
            default:
                SEND_NOTICE_URL = "http://10.0.8.31/push/admin/sendnotice";
                SEND_OFFLINE_URL = "http://10.0.8.31/push/only";
                break;
        }
    }

}
