package com.magic.api.commons.tools;


import com.magic.api.commons.ApiLogger;

/**
 * 时间排查工具
 */
public class TimeCountingUtil {

    private String name;

    long init;

    long time;

    public TimeCountingUtil(String name) {
        init = System.currentTimeMillis();
        time = init;
        this.name = name;
    }

    public void print(String msg) {
        long currentTimeMillis = System.currentTimeMillis();
        long consumeTime = currentTimeMillis - time;
        ApiLogger.info(String.valueOf(new StringBuilder(name).append("\t").append(msg).append("\t耗时\t").append(consumeTime)));
        time = currentTimeMillis;
    }

    public void end() {
        long allTime = System.currentTimeMillis() - init;
        ApiLogger.info(name + "\t总耗时\t" + allTime);
    }
}
