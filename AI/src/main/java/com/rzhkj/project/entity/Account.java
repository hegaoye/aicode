/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class Account extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private String code;//数据库字段:code  属性显示:账户编码
    private String account;//数据库字段:account  属性显示:账户

    @JSONField(serialize = false)
    private String password;//数据库字段:password  属性显示:密码

}

