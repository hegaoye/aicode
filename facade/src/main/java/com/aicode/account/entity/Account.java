/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 账户 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Account implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:账户编码
     */
    @ApiModelProperty(value = "账户编码")
    private java.lang.String code;
    /**
     * 数据库字段:account  属性显示:账户
     */
    @ApiModelProperty(value = "账户")
    private java.lang.String account;
    /**
     * 数据库字段:password  属性显示:密码
     */
    @ApiModelProperty(value = "密码")
    private java.lang.String password;

}
