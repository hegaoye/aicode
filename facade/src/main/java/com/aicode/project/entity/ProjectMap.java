/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.map.entity.MapClassTable;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目数据表 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectMap implements java.io.Serializable {
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
     * 数据库字段:mapClassTableCode  属性显示:字段属性映射编码
     */
    @Schema(description = "字段属性映射编码")
    @TableField("mapClassTableCode")
    private String mapClassTableCode;

    @TableField(exist = false)
    private MapClassTable mapClassTable;

}
