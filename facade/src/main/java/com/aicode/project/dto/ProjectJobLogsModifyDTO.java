/**
 * aicode
 */
package com.aicode.project.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改 任务日志 VO
 *
 * @author aicode
 */
@Data
public class ProjectJobLogsModifyDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 任务编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:log 日志")
    private java.lang.String log;
}
