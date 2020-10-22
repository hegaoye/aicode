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
public class ProjectFramworkMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 技术编码
     */
    @ApiModelProperty(value = "技术编码")
    private java.lang.String frameworkCode;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;

}
