/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * module 消息体针对 测试业务
 */
@Data
public class ModuleFileMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String moudleCode;
    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private java.lang.String path;

}
