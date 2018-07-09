/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rzhkj.core.base.BaseEntity;
import com.rzhkj.core.tools.JSON;
import lombok.Data;

import java.util.List;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class Frameworks extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;
    private String code;//数据库字段:code  属性显示:技术编码
    private String name;//数据库字段:code  属性显示:技术名称
    private String gitHome;//数据库字段:gitHome  属性显示:模板仓库地址
    private String account;//数据库字段:account  属性显示:模板仓库账户
    @JSONField(serialize = false)
    private String password;//数据库字段:password  属性显示:模板仓库密码
    private String isPublic;//数据库字段:isPublic  属性显示:是否是公开库 Y N
    private String description;//数据库字段:description  属性显示:技术描述

    private List<FrameworksTemplate> frameworksTemplateList;
}

