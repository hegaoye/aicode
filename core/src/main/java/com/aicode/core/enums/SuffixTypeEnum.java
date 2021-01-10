/*
 *      http://www.aicode.io
 *      本代码仅用于AI-Code.
 *
 */

package com.aicode.core.enums;

/**
 * 后缀枚举
 * Created by lixin on 2017/6/7.
 */
public enum SuffixTypeEnum {
    Log(".log"), Txt(".txt");
    public String val;

    SuffixTypeEnum(String val) {
        this.val = val;
    }

    //通过值获得性别
    public static SuffixTypeEnum getYN(String yn) {
        for (SuffixTypeEnum suffixEnum : SuffixTypeEnum.values()) {
            if (suffixEnum.name().equalsIgnoreCase(yn)) {
                return suffixEnum;
            }
        }
        return null;
    }


}
