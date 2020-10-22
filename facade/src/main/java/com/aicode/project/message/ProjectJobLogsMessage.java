/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * project 消息体针对 测试业务
 */
@Data
public class ProjectJobLogsMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private java.lang.String code;
    /**
     * 日志
     */
    @ApiModelProperty(value = "日志")
    private java.lang.String log;

}
