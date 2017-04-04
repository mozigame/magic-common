package com.magic.api.commons.core.context;


import com.magic.api.commons.exception.CommonException;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.StringUtils;

import java.io.Serializable;

/**
 * 客户端版本
 * @author zz
 */
public class ClientVersion implements Serializable {

    /**
     * 数据分割
     */
    private static final String SPLIT = ".";

    private static final long serialVersionUID = 6151930722084072143L;

    public int major;
    public int minor;
    public int revision;
    public int build;

    public ClientVersion() {

    }

    public ClientVersion(String version) {
        try {
            if (StringUtils.isNotEmpty(version)) {
                String[] strings = version.split("[.]");
                switch (strings.length) {
                    case 1:
                        this.major = Integer.valueOf(strings[0]);
                        break;
                    case 2:
                        this.major = Integer.valueOf(strings[0]);
                        this.minor = Integer.valueOf(strings[1]);
                        break;
                    case 3:
                        this.major = Integer.valueOf(strings[0]);
                        this.minor = Integer.valueOf(strings[1]);
                        this.revision = Integer.valueOf(strings[2]);
                        break;
                    case 4:
                        this.major = Integer.valueOf(strings[0]);
                        this.minor = Integer.valueOf(strings[1]);
                        this.revision = Integer.valueOf(strings[2]);
                        this.build = Integer.valueOf(strings[3]);
                        break;
                    default:
                        throw new CommonException("非法请求");
                }
            }
        } catch (Exception e) {
            ApiLogger.error("解析客户端版本法发生异 version " + version, e);
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    @Override
    public String toString() {
        return major + SPLIT + minor + SPLIT + revision + SPLIT + build;
    }
}
