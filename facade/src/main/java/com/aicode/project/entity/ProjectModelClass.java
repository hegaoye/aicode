/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:mapClassTableCode  属性显示:类编码
     */
    @ApiModelProperty(value = "类编码")
    @TableField("mapClassTableCode")
    private java.lang.String mapClassTableCode;
    /**
     * 数据库字段:projectModelCode  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    @TableField("projectModelCode")
    private java.lang.String projectModelCode;

}
