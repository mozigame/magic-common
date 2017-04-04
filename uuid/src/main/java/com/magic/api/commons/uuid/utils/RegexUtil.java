package com.magic.api.commons.uuid.utils;


import com.magic.api.commons.ApiLogger;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by fw on 2016/9/18.
 */
public class RegexUtil {

    private static Pattern pattern = null;

    /**
     * 根据正则过滤条件过滤
     *
     * @param input
     * @param patternString
     * @return
     */
    public static boolean contains(String input, String patternString) {
        try {
            pattern = Pattern.compile(patternString);
            return input !=null && pattern.matcher(input).matches();
        } catch (Exception e) {
            ApiLogger.error("compile error! input: + " + input + ",patternString:" + patternString, e);
            return false;
        }
    }

    /**
     * 根据批量正则过滤条件过滤
     *
     * @param input
     * @param patternStrings
     * @return
     * @throws
     */
    public static boolean contains(String input, Collection<String> patternStrings) {
        for (String pattern : patternStrings){
            if (pattern == null)continue;
            if (contains(input, pattern)) {
                return true;
            }
        }
        return false;
    }

}
