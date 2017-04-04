package com.magic.api.commons.uuid.utils;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.uuid.filter.CreditCodeRegexValidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InitUtil
 *
 * @author zj
 * @date 2016/8/9
 */
public class InitUtil{

    /**
     * 生成UUID 循环初始化List并打乱
     *
     * @param start
     * @param total
     * @param filterLever
     * @return
     */
    public static List<Long> generateUuids(long start, int total, int filterLever) {
        ApiLogger.info("generate uuid ! start:" + start + ",total:" + total + ",filterLever:" + filterLever);
        List<Long> ids = new ArrayList<Long>();
        switch (filterLever) {
            case 1://靓号过滤
                while (total-- > 0) {
                    long id = start++;
                    if (!RegexUtil.contains(id + "", CreditCodeRegexValidate.levitPatterns)) {//如果不是靓号
                        ids.add(id);
                    }
                }
                break;
            default:
                while (total-- > 0) {
                    ids.add(start++);
                }
                break;
        }
        Collections.shuffle(ids);
        return ids;
    }
}
