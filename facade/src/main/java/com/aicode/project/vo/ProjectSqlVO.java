/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目sql脚本 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectSqlVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:
     */
    @Schema(description = "")
    private Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;
    /**
     * 数据库字段:code  属性显示:tsql编码
     */
    @Schema(description = "tsql编码")
    private String code;
    /**
     * 数据库字段:tsql  属性显示:sql脚本
     */
    @Schema(description = "sql脚本")
    private String tsql;
    /**
     * 数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]
     */
    @Schema(description = "状态：停用[Disenable]，启用[Enable]")
    private String state;

}
