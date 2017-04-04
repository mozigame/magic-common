package com.magic.api.commons.utils;

import com.magic.api.commons.constants.MatrixConstants;
import com.magic.api.commons.mobile.MobileProvider;
import com.magic.api.commons.mobile.MobileProviderFactory;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.model.PhoneNumber;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MobileUtil
 *
 * @author zj
 * @date 2016/7/21
 */
public class MobileUtil {
    private static final long MIN_PHONE = 11000000000L;
    private static final long MAX_PHONE = 18999999999L;


    @Deprecated
    public static long parseRealPhone(String phoneNum) {
        phoneNum = trimPrefix(phoneNum);
        try {
            long phone = Long.parseLong(phoneNum);
            if (!isValidPhoneNum(phone)) {
                return MatrixConstants.DEFAULT_NOT_EXIST_MOBILE;
            }
            return phone;
        } catch (NumberFormatException e) {
            return MatrixConstants.DEFAULT_NOT_EXIST_MOBILE;
        }
    }

    /**
     * 不支持多国手机号 废弃
     *
     * @param phoneNums
     * @return
     */
    @Deprecated
    public static long[] parseRealPhonesLong(String[] phoneNums) {
        List<Long> phones = new ArrayList<Long>(phoneNums.length);
        for (String p : phoneNums) {
            long phone = parseRealPhone(p);
            if (phone > MatrixConstants.DEFAULT_NOT_EXIST_MOBILE) {
                phones.add(phone);
            }
        }
        Long[] phoneArr = new Long[phones.size()];
        phones.toArray(phoneArr);
        return ArrayUtils.toPrimitive(phoneArr);
    }

    /**
     * @param phone
     * @return
     */
    @Deprecated
    public static boolean isTestPhone(String phone) {
        if (phone == null) {
            return false;
        }
        PhoneNumber phoneNumber = PhoneNumber.valueOf(phone);
        if (phoneNumber == null) {
            return false;
        }
        return isTestPhone(phoneNumber);
    }

    @Deprecated
    public static boolean isTestPhone(long phone) {
        return isTestPhone(new PhoneNumber(phone));
    }

    // select * from user_idx where phone_num >12500000000 and phone_num <12500100000;


    public static boolean isUseTestUid(PhoneNumber phone) {
        for (MobileProvider provider : MobileProviderFactory.getInstance().getMobileProviders()) {
            if (provider.isTestUid()) {
                if (provider.isSupport(phone.getCode())) {
                    if (provider.isValidPhone(phone.getNumber())) {
                        if (!provider.isSpecialPhone(phone)){
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    public static boolean isUseTestAuthCode(PhoneNumber phone) {
        for (MobileProvider provider : MobileProviderFactory.getInstance().getMobileProviders()) {
            if (provider.isTestAuthCode()) {
                if (provider.isSupport(phone.getCode())) {
                    if (provider.isValidPhone(phone.getNumber())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isTestPhone(PhoneNumber phone) {
        for (MobileProvider provider : MobileProviderFactory.getInstance().getMobileProviders()) {
            if (provider.isTest()) {
                if (provider.isSupport(phone.getCode())) {
                    if (provider.isValidPhone(phone.getNumber())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 测试手机的验证码通过特殊方式生成
     *
     * @return
     */
    public static String getTestPhoneVerifyCode() {
        return DateFormatUtils.format(System.currentTimeMillis(), "MMdd");
    }

    private static String trimPrefix(String phoneNum) {
        phoneNum = phoneNum.trim();
        if (phoneNum.startsWith("+86")) {
            phoneNum = phoneNum.substring(3);
        } else if (phoneNum.startsWith("86")) {
            phoneNum = phoneNum.substring(2);
        } else if (phoneNum.startsWith("0")) {
            phoneNum = phoneNum.substring(1);
        }
        return phoneNum;
    }

    public static boolean isValidPhoneNum(String phoneNum) {
        return isValidPhoneNum(PhoneNumber.valueOf(phoneNum));
    }

    @Deprecated
    public static boolean isValidPhoneNum(long phone) {
        if (phone < MIN_PHONE || phone > MAX_PHONE) {
            return false;
        }
        for (MobileProvider provider : MobileProviderFactory.getInstance().getMobileProviders()) {
            if (provider.isSupport(PhoneCode.CHINA_PHONE_CODE)) {
                if (provider.isValidPhone(phone)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidPhoneNum(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        for (MobileProvider provider : MobileProviderFactory.getInstance().getMobileProviders()) {
            if (provider.isSupport(phoneNumber.getCode())) {
                if (provider.isValidPhone(phoneNumber.getNumber())) {
                    return true;
                }
            }
        }
        return false;
    }
}
