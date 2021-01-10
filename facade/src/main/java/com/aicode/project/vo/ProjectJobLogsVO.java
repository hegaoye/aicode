/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private java.lang.String code;
    /**
     * 数据库字段:log  属性显示:日志
     */
    @ApiModelProperty(value = "日志")
    private java.lang.String log;

}
