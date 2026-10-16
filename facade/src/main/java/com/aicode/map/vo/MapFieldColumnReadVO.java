/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段映射**读投影** —— 供"配置表关联关系"场景消费
 * （`GET /project/relationship/listMapFieldColumn` 返回的
 * {@code mainFields} / {@code associateFields} 元素）。
 *
 * <p><b>与 {@link MapFieldColumnVO} 的区别（重要，SHALL NOT 混用）</b>：</p>
 * <table border="1">
 *   <caption>两个 VO 的职责分工</caption>
 *   <tr><th>VO</th><th>字段数</th><th>用途</th></tr>
 *   <tr><td>{@code MapFieldColumnVO}</td><td>13</td>
 *       <td>{@code /displayAttribute/list} 的出参 ——
 *           含嵌套 {@code displayAttribute}（该实体本身就是"前端表单的 UI 契约"，
 *           由 {@code DisplayAttributesPage} 消费）。<b>不可裁</b>。</td></tr>
 *   <tr><td>{@code MapFieldColumnReadVO}（本类）</td><td>8</td>
 *       <td>配关联关系时选字段用 —— 只需字段名 / 类型 / 主键·日期·状态标记。</td></tr>
 * </table>
 *
 * <p>本投影从 {@code MapFieldColumn} 实体（19 字段）裁掉 11 个：
 * 自增主键 {@code id}、嵌套 {@code displayAttribute} 与 {@code mapRelationship}
 * （两者在组合响应里另行给出）、{@code notes} / {@code defaultValue} /
 * {@code sqlType} / {@code upper}、以及 4 个派生 boolean
 * （{@code checkDate} / {@code checkState} / {@code checkPk}）。</p>
 *
 * <p>详见 {@code openspec/changes/2026-10-18-relationship-config/design.md} - D2。</p>
 */
@Data
public class MapFieldColumnReadVO implements java.io.Serializable {

    /**
     * 数据库字段:code  属性显示:
     */
    @Schema(description = "字段映射编码（雪花业务编码，对外标识）")
    private String code;

    /**
     * 数据库字段:mapClassTableCode  属性显示:类表映射编码
     */
    @Schema(description = "所属的类表映射编码")
    private String mapClassTableCode;

    /**
     * 数据库字段:column  属性显示:列名
     */
    @Schema(description = "数据库列名")
    private String column;

    /**
     * 数据库字段:field  属性显示:属性名
     */
    @Schema(description = "Java 属性名（配关联时展示给用户选的就是它）")
    private String field;

    /**
     * 数据库字段:fieldType  属性显示:字段类型
     */
    @Schema(description = "Java 字段类型，如 java.lang.String")
    private String fieldType;

    /**
     * 数据库字段:isPrimaryKey  属性显示:是否主键
     */
    @Schema(description = "是否主键")
    private String isPrimaryKey;

    /**
     * 数据库字段:isDate  属性显示:是否日期
     */
    @Schema(description = "是否日期字段")
    private String isDate;

    /**
     * 数据库字段:isState  属性显示:是否状态
     */
    @Schema(description = "是否状态字段")
    private String isState;
}