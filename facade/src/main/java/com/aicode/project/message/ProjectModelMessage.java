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
public class ProjectModelMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String code;
    /**
     * 上级模块编码
     */
    @ApiModelProperty(value = "上级模块编码")
    private java.lang.String preCode;
    /**
     * 模块显示名称
     */
    @ApiModelProperty(value = "模块显示名称")
    private java.lang.String name;
    /**
     * 模块路由
     */
    @ApiModelProperty(value = "模块路由")
    private java.lang.String route;
    /**
     * 模块css样式
     */
    @ApiModelProperty(value = "模块css样式")
    private java.lang.String css;
    /**
     * 是否是菜单 Y,N
     */
    @ApiModelProperty(value = "是否是菜单 Y,N")
    private java.lang.String isMenu;
    /**
     * 模块图标
     */
    @ApiModelProperty(value = "模块图标")
    private java.lang.String ico;

}
