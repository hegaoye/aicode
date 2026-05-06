/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.module.entity.Module;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目选择模块 的实体类
 *
 * @author hegaoye
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectModule implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    @TableField("projectCode")
    private String projectCode;
    /**
     * 数据库字段:moduleCode  属性显示:模块编码
     */
    @Schema(description = "模块编码")
    @TableField("moduleCode")
    private String moduleCode;

    @TableField(exist = false)
    private Module module;

}
