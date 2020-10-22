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
public class ProjectRepositoryAccountMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 版本管理编码
     */
    @ApiModelProperty(value = "版本管理编码")
    private java.lang.String code;
    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 帐户名
     */
    @ApiModelProperty(value = "帐户名")
    private java.lang.String account;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private java.lang.String password;
    /**
     * 仓库地址
     */
    @ApiModelProperty(value = "仓库地址")
    private java.lang.String home;
    /**
     * 仓库说明
     */
    @ApiModelProperty(value = "仓库说明")
    private java.lang.String description;
    /**
     * 状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;
    /**
     * 仓库类型:Git, Svn
     */
    @ApiModelProperty(value = "仓库类型:Git, Svn")
    private java.lang.String type;

}
