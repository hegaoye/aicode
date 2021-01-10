/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import com.aicode.core.base.BaseVO;
import lombok.Data;

/**
 * 项目信息 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectPageVO extends BaseVO implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String code;
    /**
     * 数据库字段:name  属性显示:项目名
     */
    @ApiModelProperty(value = "项目名")
    private java.lang.String name;
    /**
     * 数据库字段:description  属性显示:项目描述
     */
    @ApiModelProperty(value = "项目描述")
    private java.lang.String description;
    /**
     * 数据库字段:englishName  属性显示:项目英文名
     */
    @ApiModelProperty(value = "项目英文名")
    private java.lang.String englishName;
    /**
     * 数据库字段:databaseType  属性显示:数据库类型:Mysql,Oracle
     */
    @ApiModelProperty(value = "数据库类型:Mysql,Oracle")
    private java.lang.String databaseType;
    /**
     * 数据库字段:language  属性显示:项目语言:Java,Python,Js
     */
    @ApiModelProperty(value = "项目语言:Java,Python,Js")
    private java.lang.String language;
    /**
     * 数据库字段:state  属性显示:项目状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "项目状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;
    /**
     * 数据库字段:copyright  属性显示:项目版权文字信息
     */
    @ApiModelProperty(value = "项目版权文字信息")
    private java.lang.String copyright;
    /**
     * 数据库字段:author  属性显示:作者
     */
    @ApiModelProperty(value = "作者")
    private java.lang.String author;
    /**
     * 数据库字段:phone  属性显示:联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private java.lang.String phone;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @ApiModelProperty(value = "项目基础包名")
    private java.lang.String basePackage;
    /**
     * 数据库字段:sqlFile  属性显示:数据库脚本文件地址
     */
    @ApiModelProperty(value = "数据库脚本文件地址")
    private java.lang.String sqlFile;
    /**
     * 数据库字段:downloadUrl  属性显示:项目下载地址
     */
    @ApiModelProperty(value = "项目下载地址")
    private java.lang.String downloadUrl;
    /**
     * 数据库字段:buildNumber  属性显示:生成次数
     */
    @ApiModelProperty(value = "生成次数")
    private java.lang.Integer buildNumber;
    /**
     * 数据库字段:isRepository  属性显示:是否仓库管理
     */
    @ApiModelProperty(value = "是否仓库管理")
    private java.lang.String isRepository;
    /**
     * 数据库字段:isParseTable  属性显示:是否已经解析表
     */
    @ApiModelProperty(value = "是否已经解析表")
    private java.lang.String isParseTable;
    /**
     * 数据库字段:isParseClass  属性显示:是否已经解析类
     */
    @ApiModelProperty(value = "是否已经解析类")
    private java.lang.String isParseClass;
    /**
     * 数据库字段:createTime  属性显示:创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 数据库字段:updateTime  属性显示:更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**
     * 数据库字段:accountCode  属性显示:账户编码
     */
    @ApiModelProperty(value = "账户编码")
    private java.lang.String accountCode;
    /**
     * 数据库字段:isIncrement  属性显示:是否增量生成
     */
    @ApiModelProperty(value = "是否增量生成")
    private java.lang.String isIncrement;

}
