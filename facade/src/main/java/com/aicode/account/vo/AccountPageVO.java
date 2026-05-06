/*
 * aicode
 */
package com.aicode.account.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccountPageVO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private Long id;
    @Schema(description = "数据库字段:code 账户编码")
    private String code;
    @Schema(description = "数据库字段:account 账户")
    private String account;
    @Schema(description = "数据库字段:password 密码")
    private String password;
}
