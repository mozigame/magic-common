package com.magic.api.commons.core.context;


import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.StringUtils;

import java.io.Serializable;

/**
 * 客户端版本
 * @author zz
 */
public class ClientVersion implements Serializable, Comparable<ClientVersion> {

    /**
     * 数据分割
     */
    private static final String SPLIT = ".";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6151930722084072143L;

    /**
     * 主版本号
     */
    public int major;

    /**
     * 子版本号
     */
    public int minor;

    /**
     * 修正版本号
     */
    public int revision;

    /**
     * 编译版本号
     */
    public int build;

    public ClientVersion() {

    }

    public ClientVersion(Integer major, Integer minor, Integer revision, Integer build) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.build = build;
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
                        throw ExceptionFactor.DEFAULT_EXCEPTION;
                }
            }
        } catch (Exception e) {
            ApiLogger.error("parse client version exception" + version, e);
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
        StringBuilder version = new StringBuilder().append(major).append(SPLIT).append(minor).append(SPLIT)
                .append(revision).append(SPLIT).append(build);
        return String.valueOf(version);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ClientVersion that = (ClientVersion) object;

        if (major != that.major) return false;
        if (minor != that.minor) return false;
        if (revision != that.revision) return false;
        return build == that.build;

    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + revision;
        result = 31 * result + build;
        return result;
    }

    @Override
    public int compareTo(ClientVersion o) {
        int compare = major - o.getMajor();
        if (0 != compare) {
            return compare;
        }
        compare = minor - o.getMinor();
        if (0 != compare) {
            return compare;
        }
        compare = revision - o.getRevision();
        if (0 != compare) {
            return compare;
        }
        return build - o.getBuild();
    }
}
