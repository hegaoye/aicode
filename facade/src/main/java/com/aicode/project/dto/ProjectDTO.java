/**
 * aicode
 */
package com.aicode.project.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目信息 VO
 *
 * @author aicode
 */
@Data
public class ProjectDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 项目编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:name 项目名")
    private java.lang.String name;
    @Schema(description = "数据库字段:description 项目描述")
    private java.lang.String description;
    @Schema(description = "数据库字段:englishName 项目英文名")
    private java.lang.String englishName;
    @Schema(description = "数据库字段:databaseType 数据库类型:Mysql,Oracle")
    private java.lang.String databaseType;
    @Schema(description = "数据库字段:language 项目语言:Java,Python,Js")
    private java.lang.String language;
    @Schema(description = "数据库字段:state 项目状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;
    @Schema(description = "数据库字段:copyright 项目版权文字信息")
    private java.lang.String copyright;
    @Schema(description = "数据库字段:author 作者")
    private java.lang.String author;
    @Schema(description = "数据库字段:phone 联系方式")
    private java.lang.String phone;
    @Schema(description = "数据库字段:basePackage 项目基础包名")
    private java.lang.String basePackage;
    @Schema(description = "数据库字段:sqlFile 数据库脚本文件地址")
    private java.lang.String sqlFile;
    @Schema(description = "数据库字段:downloadUrl 项目下载地址")
    private java.lang.String downloadUrl;
    @Schema(description = "数据库字段:buildNumber 生成次数")
    private java.lang.Integer buildNumber;
    @Schema(description = "数据库字段:isRepository 是否仓库管理")
    private java.lang.String isRepository;
    @Schema(description = "数据库字段:isParseTable 是否已经解析表")
    private java.lang.String isParseTable;
    @Schema(description = "数据库字段:isParseClass 是否已经解析类")
    private java.lang.String isParseClass;
    @Schema(description = "数据库字段:createTime 创建时间")
    private java.util.Date createTime;
    @Schema(description = "数据库字段:updateTime 更新时间")
    private java.util.Date updateTime;
    @Schema(description = "数据库字段:accountCode 账户编码")
    private java.lang.String accountCode;
    @Schema(description = "数据库字段:isIncrement 是否增量生成")
    private java.lang.String isIncrement;
}
