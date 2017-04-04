package com.magic.api.commons.mobile;

import com.magic.api.commons.model.PhoneNumber;

/**
 * MobileProvider
 *
 * @author zj
 * @date 2016/7/21
 */
public interface MobileProvider {

    public static MobileProvider NULL = new MobileProvider() {

        @Override
        public String getName() {
            return "unknow_mobile";
        }

        @Override
        public boolean isSupport(int phoneCode) {
            return false;
        }

        @Override
        public boolean isValidPhone(long phone) {
            return false;
        }

        @Override
        public SMSServiceProvider[] getSmsServiceProviders(int type) {
            return new SMSServiceProvider[0];
        }

        @Override
        public boolean isTest() {
            return false;
        }

        @Override
        public boolean isTestUid() {
            return false;
        }

        @Override
        public boolean isTestAuthCode() {
            return false;
        }

        @Override
        public boolean isSpecialPhone(PhoneNumber phone) {
            return false;
        }
    };

    public String getName();

    /**
     * 是否支持该区域的手机
     *
     * @param phoneCode
     * @return
     */
    public boolean isSupport(int phoneCode);

    public boolean isValidPhone(long phone);

    public SMSServiceProvider[] getSmsServiceProviders(int type);

    @Deprecated
    public boolean isTest();

    /**
     * 生成新用户时是否使用测试用户uid
     *
     * @return
     */
    public boolean isTestUid();

    /**
     * 使用短信验证码时是否使用测试验证码
     *
     * @return
     */
    public boolean isTestAuthCode();

    boolean isSpecialPhone(PhoneNumber phone);
}
