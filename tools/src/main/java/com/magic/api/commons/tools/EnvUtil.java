package com.magic.api.commons.tools;



import com.magic.api.commons.ApiLogger;

import java.io.InputStream;
import java.util.Properties;

/**
 * 环境区分
 */
public class EnvUtil {

    private EnvUtil() {}

    private static Env env;

    static {
        try (InputStream inputStream = EnvUtil.class.getClassLoader().getResourceAsStream("env.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String stringEnv = properties.getProperty("env");
            env = Env.valueOf(stringEnv);
            if (null == env) {
                throw new RuntimeException("未知环境");
            }
        } catch (Exception e) {
            env = Env.local;
            ApiLogger.error("读取环境配置出错", e);
        }
    }

    public static Env getEnv() {
        return env;
    }

    public static enum Env {
        local, dev, test, prod, gray;

    }
}
