package com.magic.api.commons.model;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.constants.PhoneCodeConstants;
import com.magic.api.commons.utils.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;

/**
 * PhoneCode
 *
 * @author zj
 * @date 2016/7/20
 */
public class PhoneCode {

    public static final int CHINA_PHONE_CODE = 86;
    public static final int TEST_PHONE_CODE = 999;

    private String name;
    private int code;

    public PhoneCode(){}

    public PhoneCode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name + "(" + code + ")";
    }

    static PhoneCode[] regionCodes;

    static {
        try {
            Document doc = XmlUtil.parse(PhoneCodeConstants.PHONE_CODE);
            Element root = doc.getRootElement();
            List<Element> elements = root.elements();
            regionCodes = new PhoneCode[elements.size()];
            int index = 0;
            for (Element element : elements) {
                String name = element.elementTextTrim("name");
                int code = Integer.parseInt(element.elementTextTrim("code"));
                regionCodes[index] = new PhoneCode(name, code);
                index++;
            }
        } catch (Exception e) {
            ApiLogger.error("PhoneCode.static " + e.getMessage());
        }
    }

    public static PhoneCode[] getPhoneCodes() {
        return regionCodes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        PhoneCode other = (PhoneCode) obj;
        if (code != other.code)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
