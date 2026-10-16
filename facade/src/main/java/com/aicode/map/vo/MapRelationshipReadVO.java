/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关联关系**读投影** —— 出参用（`GET /project/relationship/list` 的元素，
 * 以及 {@link RelationshipFieldsVO#getMapRelationship()}）。
 *
 * <p><b>与 {@link MapRelationshipVO} 的区别（重要，SHALL NOT 混用）</b>：</p>
 * <table border="1">
 *   <caption>两个 VO 的职责分工</caption>
 *   <tr><th>VO</th><th>字段数</th><th>用途</th></tr>
 *   <tr><td>{@code MapRelationshipVO}</td><td>8</td>
 *       <td>{@code PUT /project/relationship/modify} 的 {@code @RequestBody} 入参 ——
 *           其 {@code id} 是该端点 {@code update(..., eq(getId, vo.getId()))}
 *           查询必需的。<b>不可裁</b>。</td></tr>
 *   <tr><td>{@code MapRelationshipReadVO}（本类）</td><td>7</td>
 *       <td>列表 / 关联配置展示用 —— 自增主键不出现在任何接口响应中。</td></tr>
 * </table>
 *
 * <p>本投影从 {@code MapRelationship} 实体（11 字段）裁掉 4 个：
 * 自增主键 {@code id}、嵌套 {@code associateClass}（整个 {@code MapClassTable}
 * 实体，列表场景必为 {@code null}）、派生 boolean {@code oneToOne} / {@code oneToMany}
 * （与 {@code isOneToOne} / {@code isOneToMany} 语义同源，前者是布尔字段、
 * 后者是 {@code YNEnum} 字符串化的结果）。</p>
 *
 * <p>详见 {@code openspec/changes/2026-10-18-relationship-config/design.md} - D3。</p>
 */
@Data
public class MapRelationshipReadVO implements java.io.Serializable {

    /**
     * 数据库字段:code  属性显示:
     */
    @Schema(description = "关联关系编码（雪花业务编码）")
    private String code;

    /**
     * 数据库字段:mapClassTableCode  属性显示:类表映射编码
     */
    @Schema(description = "主表的类表映射编码")
    private String mapClassTableCode;

    /**
     * 数据库字段:associateCode  属性显示:被关联编码
     */
    @Schema(description = "被关联表的类表映射编码")
    private String associateCode;

    /**
     * 数据库字段:isOneToOne  属性显示:是否一对一
     */
    @Schema(description = "是否一对一（Y / N）")
    private String isOneToOne;

    /**
     * 数据库字段:isOneToMany  属性显示:是否一对多
     */
    @Schema(description = "是否一对多（Y / N）")
    private String isOneToMany;

    /**
     * 数据库字段:mainField  属性显示:主表关联属性
     */
    @Schema(description = "主表关联属性名")
    private String mainField;

    /**
     * 数据库字段:joinField  属性显示:从表关联属性
     */
    @Schema(description = "从表关联属性名")
    private String joinField;
}