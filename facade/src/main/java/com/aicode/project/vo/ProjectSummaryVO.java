/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 项目信息列表 VO —— 列表页（`/project/list`）消费集合。
 *
 * 详见 `openspec/changes/2026-10-17-project-vo-trim/specs/api-data-object/spec.md`：
 * 5 字段（code / name / englishName / state / createTime）—— 与详情页
 * `ProjectDetailVO`（12 字段）不同语义。
 *
 * M3-C 改造：与原 `ProjectVO`（22 字段全拷贝实体）相比，列表场景只
 * 关心"哪一行、什么状态、谁拥有、何时建"，配置类字段
 * （`databaseType` / `language` / `description` 等）放在详情页消费；
 * `updateTime` / `accountCode` 列表页未消费，亦不下沉。
 */
@Data
public class ProjectSummaryVO implements java.io.Serializable {

    /**
     * 数据库字段:code 项目编码
     */
    @Schema(description = "项目编码")
    private String code;

    /**
     * 数据库字段:name 项目名
     */
    @Schema(description = "项目名")
    private String name;

    /**
     * 数据库字段:englishName 项目英文名
     */
    @Schema(description = "项目英文名")
    private String englishName;

    /**
     * 数据库字段:state 项目状态：停用[Disenable]，启用[Enable]
     */
    @Schema(description = "项目状态：停用[Disenable]，启用[Enable]")
    private String state;

    /**
     * 数据库字段:createTime 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
}
