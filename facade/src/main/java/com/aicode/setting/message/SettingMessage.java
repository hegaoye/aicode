/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * setting 消息体针对 测试业务
 */
@Data
public class SettingMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 键
     */
    @ApiModelProperty(value = "键")
    private java.lang.String k;
    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    private java.lang.String v;
    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    private java.lang.String description;

}
