/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * project 消息体针对 测试业务
 */
@Data
public class ProjectMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String code;
    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名")
    private java.lang.String name;
    /**
     * 项目描述
     */
    @ApiModelProperty(value = "项目描述")
    private java.lang.String description;
    /**
     * 项目英文名
     */
    @ApiModelProperty(value = "项目英文名")
    private java.lang.String englishName;
    /**
     * 数据库类型:Mysql,Oracle
     */
    @ApiModelProperty(value = "数据库类型:Mysql,Oracle")
    private java.lang.String databaseType;
    /**
     * 项目语言:Java,Python,Js
     */
    @ApiModelProperty(value = "项目语言:Java,Python,Js")
    private java.lang.String language;
    /**
     * 项目状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "项目状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;
    /**
     * 项目版权文字信息
     */
    @ApiModelProperty(value = "项目版权文字信息")
    private java.lang.String copyright;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private java.lang.String author;
    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private java.lang.String phone;
    /**
     * 项目基础包名
     */
    @ApiModelProperty(value = "项目基础包名")
    private java.lang.String basePackage;
    /**
     * 数据库脚本文件地址
     */
    @ApiModelProperty(value = "数据库脚本文件地址")
    private java.lang.String sqlFile;
    /**
     * 项目下载地址
     */
    @ApiModelProperty(value = "项目下载地址")
    private java.lang.String downloadUrl;
    /**
     * 生成次数
     */
    @ApiModelProperty(value = "生成次数")
    private java.lang.Integer buildNumber;
    /**
     * 是否仓库管理
     */
    @ApiModelProperty(value = "是否仓库管理")
    private java.lang.String isRepository;
    /**
     * 是否已经解析表
     */
    @ApiModelProperty(value = "是否已经解析表")
    private java.lang.String isParseTable;
    /**
     * 是否已经解析类
     */
    @ApiModelProperty(value = "是否已经解析类")
    private java.lang.String isParseClass;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**
     * 账户编码
     */
    @ApiModelProperty(value = "账户编码")
    private java.lang.String accountCode;
    /**
     * 是否增量生成
     */
    @ApiModelProperty(value = "是否增量生成")
    private java.lang.String isIncrement;

}
