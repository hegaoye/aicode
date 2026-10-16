/**
* aicode
 */
package com.aicode.map.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类表映射信息 VO —— `GET /project/relationship/listMapClassTable` 的出参元素。
 *
 * <p><b>2026-10-18 改造（5 → 6 字段）</b>：</p>
 * <ul>
 *   <li>移除自增主键 {@code id} —— 按 {@code api-data-object} 的约定，数据库自增主键
 *       SHALL NOT 出现在任何接口响应中，对外标识是雪花业务编码 {@code code}；</li>
 *   <li>补 {@code classModel} 与 {@code dashedCaseName} —— 前者决定生成的是 POJO 还是
 *       ActiveRecord 风格，后者是前端侧的 kebab-case 名（代码生成要产出对应目录）；
 *       {@code ProjectSqlPanel} 的解析结果表格已在消费 {@code dashedCaseName}。</li>
 * </ul>
 *
 * <p>刻意不含两个关联 list（{@code mapFieldColumnList} / {@code mapRelationshipList}）
 * —— 列表场景必为 {@code null}，进契约只是噪音。字段另有分场景的读投影：
 * {@code MapFieldColumnReadVO} / {@code MapRelationshipReadVO}。</p>
 *
 * <p>详见 {@code openspec/changes/2026-10-18-relationship-config/design.md}。</p>
 *
 * @author aicode
 */
@Data
public class MapClassTableVO implements java.io.Serializable {
    @Schema(description = "映射编码（雪花业务编码，对外标识）")
    private String code;
    @Schema(description = "数据库字段:tableName 表名")
    private String tableName;
    @Schema(description = "数据库字段:className 类名")
    private String className;
    @Schema(description = "类模型风格（POJO / ActiveRecord 等）")
    private String classModel;
    @Schema(description = "前端侧的 kebab-case 名（生成代码时的目录名）")
    private String dashedCaseName;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;
}