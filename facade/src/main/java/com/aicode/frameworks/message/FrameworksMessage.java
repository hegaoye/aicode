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
public class FrameworksMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 技术编码
     */
    @ApiModelProperty(value = "技术编码")
    private java.lang.String code;
    /**
     * 技术名称
     */
    @ApiModelProperty(value = "技术名称")
    private java.lang.String name;
    /**
     * 技术描述
     */
    @ApiModelProperty(value = "技术描述")
    private java.lang.String description;
    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.String gitHome;
    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.String account;
    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.String password;
    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.String isPublic;

}
