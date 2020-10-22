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
public class ProjectJobMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private java.lang.String code;
    /**
     * 第多少次执行
     */
    @ApiModelProperty(value = "第多少次执行")
    private java.lang.String number;
    /**
     * 任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}
     */
    @ApiModelProperty(value = "任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}")
    private java.lang.String state;
    /**
     * 执行任务时间
     */
    @ApiModelProperty(value = "执行任务时间")
    private java.util.Date createTime;

}
