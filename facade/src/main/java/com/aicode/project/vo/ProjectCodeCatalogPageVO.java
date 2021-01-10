/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import com.aicode.core.base.BaseVO;
import lombok.Data;

/**
 * 生成源码资料 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectCodeCatalogPageVO extends BaseVO implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:编码
     */
    @ApiModelProperty(value = "编码")
    private java.lang.String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @ApiModelProperty(value = "项目基础包名")
    private java.lang.String basePackage;
    /**
     * 数据库字段:fileName  属性显示:文件名
     */
    @ApiModelProperty(value = "文件名")
    private java.lang.String fileName;
    /**
     * 数据库字段:fileSuffix  属性显示:文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    private java.lang.String fileSuffix;
    /**
     * 数据库字段:relativePath  属性显示:相对路径
     */
    @ApiModelProperty(value = "相对路径")
    private java.lang.String relativePath;
    /**
     * 数据库字段:absolutePath  属性显示:绝对路径
     */
    @ApiModelProperty(value = "绝对路径")
    private java.lang.String absolutePath;

}
