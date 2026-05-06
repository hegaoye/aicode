/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目应用技术 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectFramworkVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:frameworkCode  属性显示:技术编码
     */
    @Schema(description = "技术编码")
    private String frameworkCode;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;

}
