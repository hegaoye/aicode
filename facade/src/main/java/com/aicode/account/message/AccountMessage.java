/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * account 消息体针对 测试业务
 */
@Data
public class AccountMessage implements java.io.Serializable {

    /**
     * 
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 账户编码
     */
    @ApiModelProperty(value = "账户编码")
    private java.lang.String code;
    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private java.lang.String account;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private java.lang.String password;

}
