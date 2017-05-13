package com.magic.commons.test;

import com.magic.api.commons.tools.UUIDUtil;

import java.util.HashSet;

/**
 * PromotionUtil
 *
 * @author zj
 * @date 2017/5/3
 */
public class PromotionUtil {

    public static void main(String[] args) {
        /*HashSet<String> set = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            String code = UUIDUtil.getCode();
            set.add(code);
            System.out.println(code);
        }
        System.out.println("size:" + set.size());*/
        System.out.println(UUIDUtil.getUUID().length());
    }

}
