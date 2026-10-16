/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 项目创建结果 VO —— 写侧端点（`POST /project/build`）返回集合。
 *
 * 详见 `openspec/changes/2026-10-18-project-create-and-sql-init/specs/api-data-object/spec.md`：
 * 8 字段（code / name / englishName / author / basePackage /
 * databaseType / state / createTime）。
 *
 * <p>字段选取依据是写侧返回体的用途：让前端跳详情页 + 显示一句确认。
 * 因此保留「跳转所需的业务编码」+「用户刚填的 6 个字段回显」+
 * 「派生的状态与创建时间」。</p>
 *
 * <p><b>刻意排除</b>（`Project` 实体共 29 字段）：</p>
 * <ul>
 *   <li>{@code id} —— 数据库自增主键，按既有约定不出现在任何响应中；</li>
 *   <li>{@code sqlFile} / {@code downloadUrl} / {@code buildNumber} —— 构建产物相关，
 *       与「创建」这一动作无关；</li>
 *   <li>{@code isParseTable} / {@code isParseClass} / {@code isIncrement} /
 *       {@code isRepository} —— 纯内部状态位与配置项；</li>
 *   <li>{@code relationshipAndDisplay} —— 前端不知其语义的派生串；</li>
 *   <li>{@code description} / {@code copyright} / {@code phone} —— 用户自己刚填的，
 *       跳详情页后会在 `/project/load/code/{code}` 再拉一次，避免重复传输；</li>
 *   <li>7 个关联 list（{@code projectFramworkList} 等）—— 创建时全为 null，
 *       放进契约只是噪音。</li>
 * </ul>
 */
@Data
public class ProjectCreateVO implements java.io.Serializable {

    /**
     * 数据库字段:code 项目编码
     */
    @Schema(description = "项目编码（雪花业务编码，前端据此跳详情页）")
    private String code;

    /**
     * 数据库字段:name 项目名
     */
    @Schema(description = "项目名")
    private String name;

    /**
     * 数据库字段:englishName 项目英文名
     */
    @Schema(description = "项目英文名（同时是数据库 schema 名，全局唯一）")
    private String englishName;

    /**
     * 数据库字段:author 作者
     */
    @Schema(description = "作者")
    private String author;

    /**
     * 数据库字段:basePackage 项目基础包名
     */
    @Schema(description = "项目基础包名")
    private String basePackage;

    /**
     * 数据库字段:databaseType 数据库类型
     */
    @Schema(description = "数据库类型：Mysql / Oracle 等（纯元数据，不影响后端建库方言）")
    private String databaseType;

    /**
     * 数据库字段:state 状态
     */
    @Schema(description = "项目状态：Enable / Disable")
    private String state;

    /**
     * 数据库字段:createTime 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
}