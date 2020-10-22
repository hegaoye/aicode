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
public class ProjectSqlMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * tsql编码
     */
    @ApiModelProperty(value = "tsql编码")
    private java.lang.String code;
    /**
     * sql脚本
     */
    @ApiModelProperty(value = "sql脚本")
    private java.lang.String tsql;
    /**
     * 状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;

}
