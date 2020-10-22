/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * frameworks 消息体针对 测试业务
 */
@Data
public class FrameworksTemplateMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 模板编码
     */
    @ApiModelProperty(value = "模板编码")
    private java.lang.String code;
    /**
     * 框架编码
     */
    @ApiModelProperty(value = "框架编码")
    private java.lang.String frameworkCode;
    /**
     * 模板路径
     */
    @ApiModelProperty(value = "模板路径")
    private java.lang.String path;

}
