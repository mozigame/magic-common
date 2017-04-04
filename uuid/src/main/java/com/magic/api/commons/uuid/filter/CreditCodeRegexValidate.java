package com.magic.api.commons.uuid.filter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fw on 2016/9/18.
 */
public class CreditCodeRegexValidate {

    public static Set<String> levitPatterns = new HashSet<>();

    static {
        // 手机号、生日号、跟公司业务相关的号码
        //levitPatterns.add("^(0|13|15|18|168|400|800)[0-9]*$");
        //levitPatterns.add("^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$");
        levitPatterns.add("^\\d*(1594184|1314|1314520|1314925|1392010|259695|259758|2925184|505|520|741|574839|3707|319421|359258|737421|745420|7418695|7474174)\\d*$");   //--OK

        //OOOOO 任意包含超过5个重复数字的号码
        //levitPatterns.add("^\\d(?!(\\d)\\1{4})\\d{5,}$");   //--NO
        levitPatterns.add("^\\d*(\\d)\\1{4}\\d*$"); //如:11111,111115,155555

        // 重复号码，镜子号码
        //levitPatterns.add("^([\\d])\\1{2,}$");
        //levitPatterns.add("^(<a>\\d)(\\d)(\\d)\\1\\2\\3$");
        // ABCCBA CBAABC
        levitPatterns.add("^\\d*(\\d)(\\d)(\\d)\\3\\2\\1\\d*$");   // --OK如:119911,1111119,1119911

        // AABB
        //levitPatterns.add("^\\d*(\\d)\\1(\\d)\\2\\d*$");
        // AAABBB
        //levitPatterns.add("^\\d*(\\d)\\1\\1(\\d)\\2\\2\\d*$");
        // ABABAB
        //levitPatterns.add("^(\\d)(\\d)\\1\\2\\1\\2\\1\\2$");
        // ABBABB
        //levitPatterns.add("^(\\d)(\\d)\\2\\1\\2\\2$");

        // AABAAB
        levitPatterns.add("^\\d*(\\d)\\1(\\d)\\1\\1\\2\\d*$");  //--OK如:119119,1191190,1119119
        // ABCABC CBACBA
        levitPatterns.add("^\\d*(\\d)(\\d)(\\d)\\1\\2\\3\\d*$");  //--OK如:119119,123123,190190,1119119,1191190,2011011

        // 4-8 位置重复
        //levitPatterns.add("^\\d*(\\d)\\1{2,}\\d*$");
        // 4位以上 位递增或者递减（7890也是递增）
        //levitPatterns.add("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)|9(?=0)){2,}|(?:0(?=9)|9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2,})\\d");

        // 不能以 518 、918 结尾
        //levitPatterns.add("^[0-9]*(518|918)$");
        // 1、2开头的号码保留，做生日号
        levitPatterns.add("^(1|2)\\d{0,}$");    //--OK
        // 以6、8、开头8位号段尽量不放
        levitPatterns.add("^(666|888)\\d{5}$"); //  --OK
    }
}
