/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 模块下的类 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectModelClass implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @Schema(description = "")
    private Long id;
    /**
     * 数据库字段:mapClassTableCode  属性显示:类编码
     */
    @Schema(description = "类编码")
    @TableField("mapClassTableCode")
    private String mapClassTableCode;
    /**
     * 数据库字段:projectModelCode  属性显示:模块编码
     */
    @Schema(description = "模块编码")
    @TableField("projectModelCode")
    private String projectModelCode;

}
