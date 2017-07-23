package com.magic.api.commons.utils;

import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jolestar
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{

   // private static JsonFactory jsonFactory = new JsonFactory();

    public static void trim(StringBuffer sb, char c) {
        if (sb == null) {
            return;
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    public static void trim(StringBuilder sb, char c) {
        if (sb == null) {
            return;
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    public static String[] safeSplitParam(String str, String reg) {
        if (str == null) {
            return null;
        }
        String[] strArr = str.split(reg);
        List<String> concreatedStrs = new ArrayList<String>(strArr.length);
        for (String s : strArr) {
            if (s.trim().length() > 0) {
                concreatedStrs.add(s.trim());
            }
        }
        return concreatedStrs.toArray(new String[concreatedStrs.size()]);
    }

    public static Long[] safeSplitAndParseLongParam(String str, String reg) {
        return safeSplitAndParseLongParam(str, reg, false);
    }

    public static Long[] safeSplitAndParseLongParam(String str, String reg, boolean onlyReservePostive) {
        if (str == null) {
            return null;
        }
        String[] strArr = str.split(reg);
        List<Long> concreatedStrs = new ArrayList<Long>(strArr.length);

        for (String s : strArr) {
            try {
                if (s.trim().length() > 0) {
                    long num = Long.parseLong(s.trim());
                    if (onlyReservePostive && num < 1) {
                        continue;
                    }
                    concreatedStrs.add(num);
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return concreatedStrs.toArray(new Long[concreatedStrs.size()]);
    }

    public static String join(long[] array, String separator) {
        return join(array, separator, 0, array.length);
    }

    public static String join(long[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0: Len = NofStrings *(len(firstString) +
        // len(separator))
        // (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize = 8 * array.length;

        StrBuilder buf = new StrBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static boolean checkJsonFormat(String content) {
//        if (content == null || content.trim().length() == 0) {
//            return true;
//        }
//        try {
//            new JsonTypeMapper().read(jsonFactory.createJsonParser(new StringReader(content))).getTextValue();
//            return true;
//        } catch (Exception e) {
//            ApiLogger.warn("found malformed json:" + content);
//            return false;
//        }
        return false;
    }

    public static String string2unicode(String str) {
        str = (str == null ? "" : str);
        StringBuffer sb = new StringBuffer(7 * str.length());
        sb.setLength(0);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            sb.append("\\u");
            int j = (c >>> 8);
            String tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF);
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    public static String unicode2string(String str) {
        str = (str == null ? "" : str);
        if (!str.startsWith("\\u"))
            return str;

        StringBuffer sb = new StringBuffer(1000);

        for (int i = 0; i <= str.length() - 6; ) {
            String strTemp = str.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a':
                        t = 10;
                        break;
                    case 'b':
                        t = 11;
                        break;
                    case 'c':
                        t = 12;
                        break;
                    case 'd':
                        t = 13;
                        break;
                    case 'e':
                        t = 14;
                        break;
                    case 'f':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }

    /**
     * @param strings
     * @return
     * @Doc String[] 转成String,eg：new String[]{"aaa","bbb","ccc"} 转成aaa,bbb,ccc
     */
    public static String arrayToStrSplit(String[] strings) {
        if (strings == null || strings.length <= 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String s : strings) {
            sb.append(s).append(",");
        }
        String str = "";
        if (sb.length() > 0) {
            str = sb.substring(0, sb.length() - 1);
        }
        return str;
    }

//    public static boolean hasText(String str) {
//        return org.springframework.util.StringUtils.hasText(str);
//    }
//
//    public static boolean hasText(CharSequence chars) {
//        return org.springframework.util.StringUtils.hasText(chars);
//    }
//
//    public static String[] commaDelimitedListToStringArray(String str) {
//        return org.springframework.util.StringUtils.commaDelimitedListToStringArray(str);
//    }
//
//    public static Set<String> commaDelimitedListToSet(String str) {
//        return org.springframework.util.StringUtils.commaDelimitedListToSet(str);
//    }
//
//    public static String collectionToDelimitedString(Collection<?> coll) {
//        return org.springframework.util.StringUtils.collectionToCommaDelimitedString(coll);
//    }

}

