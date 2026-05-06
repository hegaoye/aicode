/**
 * aicode
 */
package com.aicode.project.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目数据表 VO
 *
 * @author aicode
 */
@Data
public class ProjectMapDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:projectCode 项目编码")
    private java.lang.String projectCode;
    @Schema(description = "数据库字段:mapClassTableCode 字段属性映射编码")
    private java.lang.String mapClassTableCode;
}
