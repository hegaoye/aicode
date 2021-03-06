/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.map.entity.MapClassTable;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    @TableField("projectCode")
    private java.lang.String projectCode;
    /**
     * 数据库字段:mapClassTableCode  属性显示:字段属性映射编码
     */
    @ApiModelProperty(value = "字段属性映射编码")
    @TableField("mapClassTableCode")
    private java.lang.String mapClassTableCode;

    @TableField(exist = false)
    private MapClassTable mapClassTable;

}
