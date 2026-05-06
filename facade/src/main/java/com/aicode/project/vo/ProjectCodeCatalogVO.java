/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 生成源码资料 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectCodeCatalogVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:
     */
    @Schema(description = "")
    private Long id;
    /**
     * 数据库字段:code  属性显示:编码
     */
    @Schema(description = "编码")
    private String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @Schema(description = "项目基础包名")
    private String basePackage;
    /**
     * 数据库字段:fileName  属性显示:文件名
     */
    @Schema(description = "文件名")
    private String fileName;
    /**
     * 数据库字段:fileSuffix  属性显示:文件后缀
     */
    @Schema(description = "文件后缀")
    private String fileSuffix;
    /**
     * 数据库字段:relativePath  属性显示:相对路径
     */
    @Schema(description = "相对路径")
    private String relativePath;
    /**
     * 数据库字段:absolutePath  属性显示:绝对路径
     */
    @Schema(description = "绝对路径")
    private String absolutePath;

}
