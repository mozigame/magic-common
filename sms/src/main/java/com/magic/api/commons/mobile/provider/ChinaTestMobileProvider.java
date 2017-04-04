/**
 *
 */
package com.magic.api.commons.mobile.provider;

import com.magic.api.commons.mobile.LogSMSServiceProvider;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.model.PhoneNumber;

/**
 * @author jolestar
 */
public class ChinaTestMobileProvider extends AbstractChinaMobileProvider {

    public static final long MIN_SEPECIAL_PHONE = 12500000001L;
    public static final long MAX_SEPECIAL_PHONE = 12500099999L;

    /**
     * 测试手机号段
     */
    private static PhoneRange[] TEST_PHONE = new PhoneRange[]{
            new PhoneRange("120"),
            new PhoneRange("121"),
            new PhoneRange("122"),
            new PhoneRange("123"),
            new PhoneRange("124"),
            new PhoneRange("125"),
            new PhoneRange("126"),
            new PhoneRange("127"),
            new PhoneRange("128"),
            new PhoneRange("129")
    };

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "TestMobile";
    }

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getPhoneNumRange()
     */
    @Override
    public PhoneRange[] getPhoneNumRange() {
        return TEST_PHONE;
    }

    @Override
    public SMSServiceProvider[] getSmsServiceProviders(int type) {

        return new SMSServiceProvider[]{LogSMSServiceProvider.NULL_SMS_SERVICE_PROVIDER};
    }

    @Override
    public boolean isSupport(int phoneCode) {
        return phoneCode == PhoneCode.CHINA_PHONE_CODE;
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public boolean isTestUid() {
        return true;
    }

    @Override
    public boolean isTestAuthCode() {
        return true;
    }

    @Override
    public boolean isSpecialPhone(PhoneNumber phone) {
        return checkIsSpecialPhone(phone);
    }

    public static boolean checkIsSpecialPhone(PhoneNumber phone) {
        long phoneNum = phone.getNumber();
        if (phoneNum >= MIN_SEPECIAL_PHONE && phoneNum <= MAX_SEPECIAL_PHONE) {
            return true;
        }
        return false;
    }
}
