/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 项目信息详情 VO —— 详情页（`/project/load/code/{code}`）消费集合。
 *
 * 详见 `openspec/changes/2026-10-17-project-vo-trim/specs/api-data-object/spec.md`：
 * 12 字段（code / name / englishName / description / copyright / author /
 * phone / basePackage / buildNumber / createTime / updateTime /
 * accountCode）—— 与列表页 `ProjectSummaryVO`（7 字段）不同语义。
 *
 * M3-C 改造：列表与详情共用不同 VO。配置 / 元数据类字段
 * （`databaseType` / `language` / `state` / `isRepository` /
 * `isParseTable` / `isParseClass` / `isIncrement` 等）从详情页也
 * 移除，与 `ProjectSummaryVO` 共同精简信息架构。
 */
@Data
public class ProjectDetailVO implements java.io.Serializable {

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
     * 数据库字段:description 项目描述
     */
    @Schema(description = "项目描述")
    private String description;

    /**
     * 数据库字段:copyright 项目版权文字信息
     */
    @Schema(description = "项目版权文字信息")
    private String copyright;

    /**
     * 数据库字段:author 作者
     */
    @Schema(description = "作者")
    private String author;

    /**
     * 数据库字段:phone 联系方式
     */
    @Schema(description = "联系方式")
    private String phone;

    /**
     * 数据库字段:basePackage 项目基础包名
     */
    @Schema(description = "项目基础包名")
    private String basePackage;

    /**
     * 数据库字段:buildNumber 生成次数
     */
    @Schema(description = "生成次数")
    private Integer buildNumber;

    /**
     * 数据库字段:createTime 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 数据库字段:updateTime 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 数据库字段:accountCode 账户编码
     */
    @Schema(description = "账户编码")
    private String accountCode;
}
