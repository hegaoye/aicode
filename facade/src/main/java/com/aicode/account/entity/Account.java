/*
 * demo
 */
package com.aicode.account.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 账户 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Account implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private Long id;
    @Schema(description = "数据库字段:code 账户编码")
    private String code;
    @Schema(description = "数据库字段:account 账户")
    private String account;
    @Schema(description = "数据库字段:password 密码")
    private String password;

}
