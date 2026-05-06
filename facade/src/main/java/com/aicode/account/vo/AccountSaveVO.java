/**
 * aicode
 */
package com.aicode.account.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存 账户 VO
 *
 * @author aicode
 */
@Data
public class AccountSaveVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private Long id;
    @Schema(description = "数据库字段:code 账户编码")
    private String code;
    @Schema(description = "数据库字段:account 账户")
    private String account;
    @Schema(description = "数据库字段:password 密码")
    private String password;
}
