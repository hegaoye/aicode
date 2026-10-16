/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 账户信息公开 VO —— profile 页面（`/account/load?code=`）消费集合。
 *
 * 详见 `openspec/changes/2026-10-17-account-load-and-menu/specs/aicode-web-account/spec.md`：
 * 3 字段（`id` / `code` / `account`）—— 故意**不含** `password` 字段，
 * 避免 hash 泄漏给前端 bundle。
 *
 * M3-D 改造：既有 `AccountVO`（4 字段含 `password`）继续被 build/list/modify/
 * delete 端点用（管理面），不与本 VO 共享字段集合。
 */
@Data
public class AccountPublicVO implements java.io.Serializable {

    /**
     * 数据库字段:id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 数据库字段:code 账户编码
     */
    @Schema(description = "账户编码")
    private String code;

    /**
     * 数据库字段:account 账户显示名（如 admin / aicode）
     */
    @Schema(description = "账户显示名")
    private String account;
}
