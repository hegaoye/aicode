package com.aicode.core.enums;

/**
 * 语言枚举
 * Created by hegaoye on 2018/9/18.
 */
public enum LanguageEnum {
    zh_CN("简体中文"),
    en_GB("英式英语"),
   /* zh_CN("简体中文"),
    zh_TW("繁体中文"),
    en("英语"),
    en_GB("英式英语"),
    en_US("美式英语"),
    ja_JP("日语"),
    th_TH("泰语"),
    es_ES("西班牙语"),
    ru_RU("俄语"),*/
    ;
    public String val;

    LanguageEnum(String val) {
        this.val = val;
    }

    //通过值获得语言
    public static LanguageEnum getLanguage(String sex) {
        for (LanguageEnum sexEnum : LanguageEnum.values()) {
            if (sexEnum.name().equalsIgnoreCase(sex)) {
                return sexEnum;
            }
        }
        return null;
    }


}
