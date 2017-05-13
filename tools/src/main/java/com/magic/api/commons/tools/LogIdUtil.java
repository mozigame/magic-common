package com.magic.api.commons.tools;

import java.time.LocalTime;

/**
 * LogIdUtil
 *
 * @author zj
 * @date 2017/4/24
 */
public class LogIdUtil {

    private static final String localIp;

    //本地IP
    static {
        localIp = "";
    }


    public static String getLogId(int appId){
        StringBuilder logId = new StringBuilder(localIp);
        logId.append("-").append(appId)
                .append(LocalTime.now());
        return null;
    }

}
