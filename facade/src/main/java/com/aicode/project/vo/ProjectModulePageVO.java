/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目选择模块 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectModulePageVO extends BaseVO implements java.io.Serializable {
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
     * 数据库字段:moduleCode  属性显示:模块编码
     */
    @Schema(description = "模块编码")
    private String moduleCode;

}
