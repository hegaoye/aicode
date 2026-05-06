/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 项目数据表 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectMapVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;
    /**
     * 数据库字段:mapClassTableCode  属性显示:字段属性映射编码
     */
    @Schema(description = "字段属性映射编码")
    private String mapClassTableCode;

}
