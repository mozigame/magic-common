package com.magic.api.commons.core.tools;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.exception.CommonException;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.core.tools.encrypt.AESEncrypter;
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
     * 一天+五分钟
     */
    private static final long EXPIRES_TIME = 86700000;

    /**
     * 2小时内
     */
    private static final long NEW_TOKEN_TIME = 7200000;

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
    private static final String SPACES = "-";


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
     * @param deviceId 设备号
     * @return  用户ID
     */
    public static final AuthModel getUid(String stringMauth, String deviceId) {
        String[] ss = stringMauth.split(SPACES);
        switch (ss.length) {
            case NEW_MAUTH_LENTH :
                return getNewUid(ss[1], deviceId);
            default:
                ApiLogger.error("Authorization header error, stringMauth:" + stringMauth);
                throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
        }
    }

    /**
     * 获取用户ID
     * @param stringMauth StringMauth
     * @return  用户ID
     */
    public static final AuthModel getUid(String stringMauth) {
        String[] ss = stringMauth.split(SPACES);
        switch (ss.length) {
            case OLD_MAUTH_LENTH :
                return getOldUid(ss[1]);
            case NEW_MAUTH_LENTH :
                return getNewUid(ss[1]);
            default:
                ApiLogger.error("Authorization header error, stringMauth:" + stringMauth);
                throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
        }
    }

    public static void main(String[] args) {
        String auth = "MAuth-d96cec0100342a4d52699aff87eca8b32e9bae5d7696f1e6299ad272f4e996f1";
        AuthModel uid = getUid(auth);
        System.out.println(uid.getUid());
        String a = createOld(105094, System.currentTimeMillis() + 31536000000l);
        System.out.println(a);
    }

    /**
     * 生成Mauth
     * @param uid           用户ID
     * @param appId         用于标示平台 标识实惠为1
     * @param expiringDate  mauth过期的绝对值    毫秒
     * @return  Mauth
     */
    public static final String create(long uid, int appId, long expiringDate) {
        String tempMauth = expiringDate + CONNECT + uid + CONNECT + appId;
        return MAUTH_HEAD + SPACES + NEW_ENCRYPTER.encrypt(tempMauth) + SPACES + MAUTH_HEAD;
    }

    /**
     * 生成Mauth
     * @param uid
     * @param deviceId
     * @return
     */
    public static final String create(long uid, String deviceId){
        long expiringDate = System.currentTimeMillis();
        String tempMauth = expiringDate + CONNECT + uid + CONNECT + deviceId;
        return MAUTH_HEAD + SPACES + NEW_ENCRYPTER.encrypt(tempMauth) + SPACES + MAUTH_HEAD;
    }

    /**
     * 生成Mauth
     * @param uid           用户ID
     * @param expiringDate  mauth过期的绝对值    毫秒
     * @return  Mauth
     */
    public static final String createOld(long uid, long expiringDate) {
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
                throw ExceptionFactor.TOKEN_EXPIRES_EXCEPTION;
            }
            long uid = NumberUtils.toLong(timeAndUid[1], 0);
            if (uid <= 0) {
                throw ExceptionFactor.INVALID_UID_EXCEPTION;
            }
            if (now - time >= EXPIRES_TIME - NEW_TOKEN_TIME){
                authModel.setNewToken(createOld(uid, now));
            }
            authModel.setUid(uid);
            authModel.setExpiringDate(time);
            return authModel;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            ApiLogger.error("认证发生异常,header:" + stringMauth, e);
            throw ExceptionFactor.DEFAULT_EXCEPTION;
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
            String[] timeAndUidAndDeviceId = decryptedString.split(CONNECT);
            long time = NumberUtils.toLong(timeAndUidAndDeviceId[0], 0);
            long now = System.currentTimeMillis();
            if (now - time > EXPIRES_TIME) {
                throw ExceptionFactor.TOKEN_EXPIRES_EXCEPTION;
            }
            long uid = NumberUtils.toLong(timeAndUidAndDeviceId[1], 0);
            if (uid <= 0) {
                throw ExceptionFactor.INVALID_UID_EXCEPTION;
            }
            String deviceId = timeAndUidAndDeviceId[2];
            if (now - time >= EXPIRES_TIME - NEW_TOKEN_TIME){
                authModel.setNewToken(create(uid, deviceId));
            }
            authModel.setUid(uid);
            authModel.setDeviceId(deviceId);
            authModel.setExpiringDate(time);
            return authModel;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            ApiLogger.error("认证发生异常,header:" + stringMauth, e);
            throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
        }
    }

    /**
     * 获取用户ID
     * @param stringMauth StringMauth
     * @param deviceId 计算生成的deviceId，需与token中的deviceId进行校验
     * @return  用户ID
     */
    public static final AuthModel getNewUid(String stringMauth, String deviceId) {
        AuthModel authModel = new AuthModel();
        try {
            String decryptedString = NEW_ENCRYPTER.decryptAsString(stringMauth);
            String[] timeAndUidAndDeviceId = decryptedString.split(CONNECT);
            long time = NumberUtils.toLong(timeAndUidAndDeviceId[0], 0);
            long now = System.currentTimeMillis();
            if (now - time > EXPIRES_TIME) {
                throw ExceptionFactor.TOKEN_EXPIRES_EXCEPTION;
            }
            long uid = NumberUtils.toLong(timeAndUidAndDeviceId[1], 0);
            if (uid <= 0) {
                throw ExceptionFactor.INVALID_UID_EXCEPTION;
            }
            String nDeviceId = timeAndUidAndDeviceId[2];
            if (StringUtils.isEmpty(nDeviceId) || !nDeviceId.equals(deviceId)){
                throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
            }
            if (now - time >= EXPIRES_TIME - NEW_TOKEN_TIME){
                authModel.setNewToken(create(uid, deviceId));
            }
            authModel.setUid(uid);
            authModel.setDeviceId(nDeviceId);
            authModel.setExpiringDate(time);
            return authModel;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            ApiLogger.error("认证发生异常,header:" + stringMauth, e);
            throw ExceptionFactor.AUTH_FAILED_EXCEPTION;
        }
    }

    public static class AuthModel {

        /**
         * 用户ID
         */
        private long uid;

        /**
         * 设备号
         */
        private String deviceId;

        /**
         * mauth过期时间
         */
        private long expiringDate;

        /**
         * 新token，当当前token快过期时，重新下发新的token
         */
        private String newToken;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public long getExpiringDate() {
            return expiringDate;
        }

        public void setExpiringDate(long expiringDate) {
            this.expiringDate = expiringDate;
        }

        public String getNewToken() {
            return newToken;
        }

        public void setNewToken(String newToken) {
            this.newToken = newToken;
        }
    }

}
