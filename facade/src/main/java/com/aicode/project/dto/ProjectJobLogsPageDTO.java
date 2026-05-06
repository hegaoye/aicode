/*
 * aicode
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务日志 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectJobLogsPageDTO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 任务编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:log 日志")
    private java.lang.String log;
}
