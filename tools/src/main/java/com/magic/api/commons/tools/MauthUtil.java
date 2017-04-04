package com.magic.api.commons.tools;

import com.magic.api.commons.exception.CommonException;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.encrypt.AESEncrypter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Mauth工具类
 * @author zz
 */
public class MauthUtil {

    private static final AESEncrypter ENCRYPTER = AESEncrypter.getInstance();

    private static final AESEncrypter NEW_ENCRYPTER = AESEncrypter.getInstance(AESEncrypter.NEW_AES_KEY_STR);

    /**
     * 三天+五分钟
     */
    private static final long EXPIRES_TIME = 1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 5;

    /**
     * 老mauth长度
     */
    private static final int OLD_MAUTH_LENTH = 2;

    /**
     * 新mauth长度
     */
    private static final int NEW_MAUTH_LENTH = 3;

    /**
     * mauth拼接分割符
     */
    private static final String CONNECT = ":";

    /**
     * mauth头尾拼接符
     */
    private static final String MAUTH_HEAD = "MAuth";

    /**
     * mauth拼接符
     */
    private static final String SPACES = " ";


    /**
     * 是否可以验证
     * @param mauth mauth
     * @return  true可以使用mauth验证 false不可使用
     */
    public static final boolean canAuth(String mauth) {
        return !StringUtils.isBlank(mauth) && mauth.toLowerCase().startsWith(MAUTH_HEAD.toLowerCase() + SPACES);
    }


    /**
     * 获取用户ID
     * @param stringMauth StringMauth
     * @return  用户ID
     */
    public static final AuthModel getUid(String stringMauth) {
        String[] ss = stringMauth.split(" ");
        switch (ss.length) {
            case OLD_MAUTH_LENTH :
                return getOldUid(ss[1]);
            case NEW_MAUTH_LENTH :
                return getNewUid(ss[1]);
            default:
                ApiLogger.error("Authorization header error, stringMauth:" + stringMauth);
                throw new CommonException("认证失败");
        }
        //TODO 校验之后快过期 是否重新下发Mauth
    }

    /**
     * 生成Mauth
     * @param uid           用户ID
     * @param appId         用于标示平台 标识实惠为1
     * @param expiringDate  mauth过期的绝对值    毫秒
     * @return  Mauth
     */
    public static final String create(int uid, int appId, long expiringDate) {
        String tempMauth = expiringDate + CONNECT + uid + CONNECT + appId;
        return MAUTH_HEAD + SPACES + NEW_ENCRYPTER.encrypt(tempMauth) + SPACES + MAUTH_HEAD;
    }

    /**
     * 生成Mauth
     * @param uid           用户ID
     * @param expiringDate  mauth过期的绝对值    毫秒
     * @return  Mauth
     */
    public static final String createOld(int uid, long expiringDate) {
        String tempMauth = expiringDate + CONNECT + uid;
        return MAUTH_HEAD + SPACES + ENCRYPTER.encrypt(tempMauth);
    }

    /**
     * 获取用户ID
     * @param stringMauth StringMauth
     * @return  用户ID
     */
    public static final AuthModel getOldUid(String stringMauth) {
        AuthModel authModel = new AuthModel();
        try {
            String decryptedString = ENCRYPTER.decryptAsString(stringMauth);
            String[] timeAndUid = decryptedString.split(CONNECT);
            long time = NumberUtils.toLong(timeAndUid[0], 0);
            long now = System.currentTimeMillis();
            if (now - time > EXPIRES_TIME) {
                throw new CommonException("token expires.");
            }
            int uid = NumberUtils.toInt(timeAndUid[1], 0);
            if (uid <= 0) {
                throw new CommonException("invalid uid.");
            }
            authModel.setUid(uid);
            authModel.setAppId(1);
            authModel.setExpiringDate(time);
            return authModel;
        } catch (Exception e) {
            ApiLogger.error("认证发生异常,header:" + stringMauth, e);
            throw new CommonException("认证失败");
        }
    }

    /**
     * 获取用户ID
     * @param stringMauth StringMauth
     * @return  用户ID
     */
    public static final AuthModel getNewUid(String stringMauth) {
        AuthModel authModel = new AuthModel();
        try {
            String decryptedString = NEW_ENCRYPTER.decryptAsString(stringMauth);
            String[] timeAndUidAndAppId = decryptedString.split(CONNECT);
            long time = NumberUtils.toLong(timeAndUidAndAppId[0], 0);
            long now = System.currentTimeMillis();
            if (now - time > EXPIRES_TIME) {
                throw new CommonException("token expires.");
            }
            int uid = NumberUtils.toInt(timeAndUidAndAppId[1], 0);
            if (uid <= 0) {
                throw new CommonException("invalid uid.");
            }
            int appId = NumberUtils.toInt(timeAndUidAndAppId[2], 0);
            authModel.setUid(uid);
            authModel.setAppId(appId);
            authModel.setExpiringDate(time);
            return authModel;
        } catch (Exception e) {
            ApiLogger.error("认证发生异常,header:" + stringMauth, e);
            throw new CommonException("认证失败");
        }
    }

    public static class AuthModel {

        /**
         * 用户ID
         */
        private int uid;

        /**
         * 平台标示 1实惠
         */
        private int appId;

        /**
         * mauth过期时间
         */
        private long expiringDate;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public long getExpiringDate() {
            return expiringDate;
        }

        public void setExpiringDate(long expiringDate) {
            this.expiringDate = expiringDate;
        }
    }

}
