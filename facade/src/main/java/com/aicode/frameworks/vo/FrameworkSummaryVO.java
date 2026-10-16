/*
 * aicode
 */
package com.aicode.frameworks.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 框架技术池 VO —— 列表页（{@code /framework/list}）与详情页（{@code /framework/load}）消费集合。
 *
 * <p>详见 {@code openspec/changes/archive/2026-10-18-framework-list-vo-contract/specs/api-data-object/spec.md}：</p>
 * <ul>
 *   <li><b>7 字段</b>：{@code id} / {@code code} / {@code name} / {@code description} /
 *       {@code gitHome} / {@code account} / {@code isPublic}；</li>
 *   <li><b>不含 {@code password}</b>：框架实体持有模板仓库的 Git 密码，该字段 SHALL NOT
 *       出现在任何响应中（同 {@code AccountPublicVO} 的处置）；</li>
 *   <li><b>不继承 {@code BaseVO}</b>：{@code BaseVO} 携带 4 个查询辅助字段
 *       （{@code createTimeBegin} / {@code createTimeEnd} / {@code updateTimeBegin} /
 *       {@code updateTimeEnd}），其语义是"入参：按时间范围过滤"，被序列化进响应即构成
 *       派生字段泄漏。</li>
 * </ul>
 *
 * <p>相比被取代的 {@code FrameworksPageVO}（已删除）：字段集合相同，唯一差别是本类
 * 不含 {@code password} 且不继承 {@code BaseVO}。</p>
 *
 * @author aicode
 */
@Data
public class FrameworkSummaryVO implements java.io.Serializable {

    /**
     * 数据库字段:id
     */
    @Schema(description = "id")
    private java.lang.Long id;

    /**
     * 数据库字段:code 技术编码
     */
    @Schema(description = "技术编码")
    private java.lang.String code;

    /**
     * 数据库字段:name 技术名称
     */
    @Schema(description = "技术名称")
    private java.lang.String name;

    /**
     * 数据库字段:description 技术描述
     */
    @Schema(description = "技术描述")
    private java.lang.String description;

    /**
     * 数据库字段:gitHome 模板仓库地址（公开信息，前端展示"模板来自哪个仓库"）
     */
    @Schema(description = "模板仓库地址")
    private java.lang.String gitHome;

    /**
     * 数据库字段:account 模板仓库账号（仅账号，无口令）
     */
    @Schema(description = "模板仓库账号")
    private java.lang.String account;

    /**
     * 数据库字段:isPublic 是否公开
     */
    @Schema(description = "是否公开")
    private java.lang.String isPublic;
}