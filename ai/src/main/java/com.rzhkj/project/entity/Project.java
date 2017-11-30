/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class Project extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:项目编码
    private String name;//数据库字段:name  属性显示:项目名
    private String description;//数据库字段:description  属性显示:项目描述
    private String englishName;//数据库字段:englishName  属性显示:项目英文名
    private String databaseType;//数据库字段:databaseType  属性显示:数据库类型:Mysql,Oracle
    private String language;//数据库字段:language  属性显示:项目语言:Java,Python,Js
    private String state;//数据库字段:state  属性显示:项目状态：停用[Disenable]，启用[Enable]
    private String copyright;//数据库字段:copyright  属性显示:项目版权文字信息
    private String author;//数据库字段:author  属性显示:作者
    private String phone;//数据库字段:phone  属性显示:联系方式
    private String basePackage;//数据库字段:basePackage  属性显示:项目基础包名
    private String sql;//数据库字段:sql  属性显示:数据库脚本文件地址
    private String downloadUrl;//数据库字段:downloadUrl  属性显示:项目下载地址
    private String isRepository;//数据库字段:isRepository  属性显示:是否仓库管理
    private java.util.Date createTime;//数据库字段:createTime  属性显示:创建时间
    private java.util.Date updateTime;//数据库字段:updateTime  属性显示:更新时间

}

