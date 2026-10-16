/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目 SQL 脚本 VO —— 写侧端点
 * （`POST /project/sql/build` 与 `POST /project/sql/modify`）返回集合。
 *
 * 详见 `openspec/changes/2026-10-18-project-create-and-sql-init/specs/api-data-object/spec.md`：
 * 4 字段（code / projectCode / tsql / state）。
 *
 * <p><b>2026-10-18 裁切</b>：此前该 VO 的字段与 {@code ProjectSql} 实体完全相同
 * （含 {@code id}），等于只改了类名没做裁切 —— 违反
 * `api-data-object` 的「Controller SHALL 返回 VO 而非持久化实体」。本次移除
 * {@code id}：数据库自增主键 SHALL NOT 出现在任何接口响应中，
 * 对外标识是雪花业务编码 {@code code}。</p>
 *
 * @author hegaoye
 */
@Data
public class ProjectSqlVO implements java.io.Serializable {

    /**
     * 数据库字段:code 属性显示:tsql编码
     */
    @Schema(description = "tsql编码（雪花业务编码，对外标识）")
    private String code;

    /**
     * 数据库字段:projectCode 属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;

    /**
     * 数据库字段:tsql 属性显示:sql脚本
     */
    @Schema(description = "sql脚本")
    private String tsql;

    /**
     * 数据库字段:state 属性显示:状态：停用[Disenable]，启用[Enable]
     */
    @Schema(description = "状态：停用[Disenable]，启用[Enable]")
    private String state;

}