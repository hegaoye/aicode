/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务日志 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectJobLogsVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:code  属性显示:任务编码
     */
    @Schema(description = "任务编码")
    private String code;
    /**
     * 数据库字段:log  属性显示:日志
     */
    @Schema(description = "日志")
    private String log;

}
