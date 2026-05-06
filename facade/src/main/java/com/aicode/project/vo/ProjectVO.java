/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目信息 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:code  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String code;
    /**
     * 数据库字段:name  属性显示:项目名
     */
    @Schema(description = "项目名")
    private String name;
    /**
     * 数据库字段:description  属性显示:项目描述
     */
    @Schema(description = "项目描述")
    private String description;
    /**
     * 数据库字段:englishName  属性显示:项目英文名
     */
    @Schema(description = "项目英文名")
    private String englishName;
    /**
     * 数据库字段:databaseType  属性显示:数据库类型:Mysql,Oracle
     */
    @Schema(description = "数据库类型:Mysql,Oracle")
    private String databaseType;
    /**
     * 数据库字段:language  属性显示:项目语言:Java,Python,Js
     */
    @Schema(description = "项目语言:Java,Python,Js")
    private String language;
    /**
     * 数据库字段:state  属性显示:项目状态：停用[Disenable]，启用[Enable]
     */
    @Schema(description = "项目状态：停用[Disenable]，启用[Enable]")
    private String state;
    /**
     * 数据库字段:copyright  属性显示:项目版权文字信息
     */
    @Schema(description = "项目版权文字信息")
    private String copyright;
    /**
     * 数据库字段:author  属性显示:作者
     */
    @Schema(description = "作者")
    private String author;
    /**
     * 数据库字段:phone  属性显示:联系方式
     */
    @Schema(description = "联系方式")
    private String phone;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @Schema(description = "项目基础包名")
    private String basePackage;
    /**
     * 数据库字段:sqlFile  属性显示:数据库脚本文件地址
     */
    @Schema(description = "数据库脚本文件地址")
    private String sqlFile;
    /**
     * 数据库字段:downloadUrl  属性显示:项目下载地址
     */
    @Schema(description = "项目下载地址")
    private String downloadUrl;
    /**
     * 数据库字段:buildNumber  属性显示:生成次数
     */
    @Schema(description = "生成次数")
    private Integer buildNumber;
    /**
     * 数据库字段:isRepository  属性显示:是否仓库管理
     */
    @Schema(description = "是否仓库管理")
    private String isRepository;
    /**
     * 数据库字段:isParseTable  属性显示:是否已经解析表
     */
    @Schema(description = "是否已经解析表")
    private String isParseTable;
    /**
     * 数据库字段:isParseClass  属性显示:是否已经解析类
     */
    @Schema(description = "是否已经解析类")
    private String isParseClass;
    /**
     * 数据库字段:createTime  属性显示:创建时间
     */
    @Schema(description = "创建时间")
    private java.util.Date createTime;
    /**
     * 数据库字段:updateTime  属性显示:更新时间
     */
    @Schema(description = "更新时间")
    private java.util.Date updateTime;
    /**
     * 数据库字段:accountCode  属性显示:账户编码
     */
    @Schema(description = "账户编码")
    private String accountCode;
    /**
     * 数据库字段:isIncrement  属性显示:是否增量生成
     */
    @Schema(description = "是否增量生成")
    private String isIncrement;

}
