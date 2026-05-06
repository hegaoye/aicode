/**
 * aicode
 */
package com.aicode.project.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改 生成源码资料 VO
 *
 * @author aicode
 */
@Data
public class ProjectCodeCatalogModifyVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:projectCode 项目编码")
    private java.lang.String projectCode;
    @Schema(description = "数据库字段:basePackage 项目基础包名")
    private java.lang.String basePackage;
    @Schema(description = "数据库字段:fileName 文件名")
    private java.lang.String fileName;
    @Schema(description = "数据库字段:fileSuffix 文件后缀")
    private java.lang.String fileSuffix;
    @Schema(description = "数据库字段:relativePath 相对路径")
    private java.lang.String relativePath;
    @Schema(description = "数据库字段:absolutePath 绝对路径")
    private java.lang.String absolutePath;
}
