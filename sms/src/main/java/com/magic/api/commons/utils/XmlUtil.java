package com.magic.api.commons.utils;

import com.magic.api.commons.ApiLogger;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * XmlUtil
 *
 * @author zj
 * @date 2016/7/21
 */
public class XmlUtil {

    public XmlUtil() {
    }

    public static Document parse(String xml) {
        try {
            SAXReader e = new SAXReader();
            Document document = e.read(new InputSource(new StringReader(xml)));
            return document;
        } catch (DocumentException e) {
            ApiLogger.info(e.getMessage());
            return null;
        }
    }

    public static String getValueAsText(Node node, String xpath) {
        Node target = node.selectSingleNode(xpath);
        return target == null?null:target.getStringValue();
    }

    public static int getValueAsInt(Node node, String xpath, int defaultValue) {
        String value = getValueAsText(node, xpath);
        if(value == null) {
            return defaultValue;
        } else {
            try {
                return NumberUtils.createNumber(value).intValue();
            } catch (NumberFormatException var5) {
                return defaultValue;
            }
        }
    }
}
