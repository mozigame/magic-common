package com.magic.api.commons.model;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.MobileUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PhoneNumber
 *
 * @author zj
 * @date 2016/7/20
 */
public class PhoneNumber {
    /**
     * 编码
     */
    private int code;
    /**
     * 手机号
     */
    private long number;
    /**
     * 手机号字符串
     */
    private transient String phoneString;

    //对空格和+号做encode编码，避免注册报错
    public static final char[] PHONE_CODE_PREFIX = new char[]{'+', ' '};

    public static final PhoneNumber DEFAULT_NO_EXIST_PHONE = new PhoneNumber(0, 0);

    public static PhoneNumber valueOf(String phoneNumStr) {
        if (StringUtils.isBlank(phoneNumStr)) {
            return null;
        }
        int phoneCode = 0;
        // 如果以 + 开头则匹配电话地区码，否则默认为中国大陆手机号
        if (ArrayUtils.contains(PHONE_CODE_PREFIX, phoneNumStr.charAt(0))) {
            phoneNumStr = phoneNumStr.substring(1);
            for (PhoneCode code : PhoneCode.getPhoneCodes()) {
                String codeStr = Integer.toString(code.getCode());
                if (phoneNumStr.startsWith(codeStr)) {
                    phoneCode = code.getCode();
                    phoneNumStr = phoneNumStr.substring(codeStr.length());
                    break;
                }
            }
        } else {
            phoneCode = PhoneCode.CHINA_PHONE_CODE;
        }
        if (phoneCode == 0) {
            ApiLogger.info("can not find phone_code by phone:" + phoneNumStr);
            return null;
        }
        try {
            long phone = Long.parseLong(phoneNumStr);
            PhoneNumber phoneNumber = new PhoneNumber(phoneCode, phone);
            if (!MobileUtil.isValidPhoneNum(phoneNumber)) {
                ApiLogger.debug("invlaid phone number:" + phoneNumStr);
                return null;
            }
            return phoneNumber;
        } catch (NumberFormatException e) {
            ApiLogger.info("invalid phone number:" + phoneNumStr);
            return null;
        }
    }

    public static PhoneNumber[] valueOf(String[] phoneNumStrs) {
        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(phoneNumStrs.length);
        for (String phoneNumStr : phoneNumStrs) {
            PhoneNumber phoneNumber = valueOf(phoneNumStr);
            if (phoneNumber != null) {
                phoneNumbers.add(phoneNumber);
            }
        }
        return phoneNumbers.toArray(new PhoneNumber[phoneNumbers.size()]);
    }

    public static PhoneNumber[] valueOf(long[] phones) {
        PhoneNumber[] phoneNums = new PhoneNumber[phones.length];
        for (int i = 0; i < phones.length; i++) {
            phoneNums[i] = new PhoneNumber(phones[i]);
        }
        return phoneNums;
    }

    public static PhoneNumber[] valueOf(Long[] phones) {
        PhoneNumber[] phoneNums = new PhoneNumber[phones.length];
        for (int i = 0; i < phones.length; i++) {
            phoneNums[i] = new PhoneNumber(phones[i]);
        }
        return phoneNums;
    }

    public static long[] toLong(PhoneNumber[] phoneNumbers) {
        long[] phones = new long[phoneNumbers.length];
        for (int i = 0; i < phoneNumbers.length; i++) {
            phones[i] = phoneNumbers[i].number;
        }
        return phones;
    }

    public PhoneNumber(){}

    public PhoneNumber(long phone) {
        this(PhoneCode.CHINA_PHONE_CODE, phone);
    }


    public PhoneNumber( int phoneCode,  long number) {
        super();
        if (phoneCode == 0) {
            phoneCode = PhoneCode.CHINA_PHONE_CODE;
        }
        this.code = phoneCode;
        this.number = number;
    }

    public int getCode() {
        return code;
    }

    public long getNumber() {
        return number;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (number ^ (number >>> 32));
        result = prime * result + code;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PhoneNumber other = (PhoneNumber) obj;
        if (number != other.number)
            return false;
        if (code != other.code)
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (this.phoneString == null) {
            this.phoneString = new StringBuilder().append(PHONE_CODE_PREFIX[0]).append(code).append(number).toString();
        }
        return this.phoneString;
    }

    public static PhoneNumber valueOf(JSONObject json) {
        return new PhoneNumber(json.getIntValue("code"), json.getLong("number"));
    }

    //@Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",this.code);
        jsonObject.put("number",this.number);
        return jsonObject;
    }
}
