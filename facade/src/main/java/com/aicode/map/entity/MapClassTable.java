/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 类表映射信息 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapClassTable implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String code;
    /**
     * 数据库字段:tableName  属性显示:表名
     */
    @ApiModelProperty(value = "表名")
    private java.lang.String tableName;
    /**
     * 数据库字段:className  属性显示:类名
     */
    @ApiModelProperty(value = "类名")
    private java.lang.String className;
    /**
     * 数据库字段:notes  属性显示:注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;

}
