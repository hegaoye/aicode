/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package com.rzhkj.core.enums;

/**
 * true/false 枚举
 * Created by lixin on 2017/6/7.
 */
public enum PonddyInfoEnum {
    OptCode("sys"), OptName("sys"),OptEmail("#"),OptTimezone("America/New_York");
    public String val;

    PonddyInfoEnum(String val) {
        this.val = val;
    }

    //通过值获得性别
    public static PonddyInfoEnum getPonddyInfo(String yn) {
        for (PonddyInfoEnum ynEnum : PonddyInfoEnum.values()) {
            if (ynEnum.name().equalsIgnoreCase(yn)) {
                return ynEnum;
            }
        }
        return null;
    }


}
