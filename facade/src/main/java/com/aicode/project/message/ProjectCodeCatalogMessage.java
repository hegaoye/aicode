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
public class ProjectCodeCatalogMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private java.lang.String code;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 项目基础包名
     */
    @ApiModelProperty(value = "项目基础包名")
    private java.lang.String basePackage;
    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private java.lang.String fileName;
    /**
     * 文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    private java.lang.String fileSuffix;
    /**
     * 相对路径
     */
    @ApiModelProperty(value = "相对路径")
    private java.lang.String relativePath;
    /**
     * 绝对路径
     */
    @ApiModelProperty(value = "绝对路径")
    private java.lang.String absolutePath;

}
