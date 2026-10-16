/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 「配关联关系」时供选字段用的组合响应 —— 替换
 * `GET /project/relationship/listMapFieldColumn` 原先的 {@code R<Object>} 出参。
 *
 * <p><b>为何必须具名</b>：原实现组装的是 {@code JSONObject}
 * （{@code jsonObject.put("mainFields", ...)} 等三处），Controller 签名只能写
 * {@code R<Object>} —— 于是契约里该端点的 {@code data} 是**无约束的 {@code {}}**，
 * 前端既拿不到内部字段名也无法做类型安全访问。参见
 * {@code openspec/specs/api-contract/spec.md} 的
 * 「R<Object> 出参 SHALL 被识别为契约缺陷而非合法写法」。</p>
 *
 * <p><b>JSON key 保持不变</b>：三个字段名与后端 {@code jsonObject.put} 的 key
 * 一一对应，SHALL NOT 改名（它们已进过任何已部署的前端）。</p>
 */
@Data
public class RelationshipFieldsVO implements java.io.Serializable {

    /**
     * 主表的字段候选列表
     */
    @Schema(description = "主表的字段候选（MapFieldColumnReadVO 列表）")
    private List<MapFieldColumnReadVO> mainFields;

    /**
     * 从表的字段候选列表
     */
    @Schema(description = "从表的字段候选（MapFieldColumnReadVO 列表）")
    private List<MapFieldColumnReadVO> associateFields;

    /**
     * 这两张表之间已有的关联关系（未配过时为 null）
     */
    @Schema(description = "两张表之间已有的关联关系；未配置过时为 null")
    private MapRelationshipReadVO mapRelationship;
}