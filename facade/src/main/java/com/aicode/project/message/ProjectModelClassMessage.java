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
public class ProjectModelClassMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 类编码
     */
    @ApiModelProperty(value = "类编码")
    private java.lang.String mapClassTableCode;
    /**
     * 模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String projectModelCode;

}
