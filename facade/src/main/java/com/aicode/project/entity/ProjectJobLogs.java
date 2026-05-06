/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import cn.hutool.core.date.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.text.DateFormat;
import java.util.Date;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_PATTERN;

/**
 * 任务日志 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectJobLogs implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:code  属性显示:任务编码
     */
    @Schema(description = "任务编码")
    private String code;
    /**
     * 数据库字段:log  属性显示:日志
     */
    @Schema(description = "日志")
    private String log;


    public ProjectJobLogs(String code, String log) {
        this.code = code;
        this.log = "> ✔ " + DateUtil.format(new Date(), NORM_DATETIME_MS_PATTERN) + "&nbsp;&nbsp;" + log;
    }
}
