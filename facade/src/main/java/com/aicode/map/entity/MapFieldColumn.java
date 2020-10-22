/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 字段属性映射信息 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapFieldColumn implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:mapClassTableCode  属性显示:映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String mapClassTableCode;
    /**
     * 数据库字段:code  属性显示:字段属性映射编码
     */
    @ApiModelProperty(value = "字段属性映射编码")
    private java.lang.String code;
    /**
     * 数据库字段:column  属性显示:字段
     */
    @ApiModelProperty(value = "字段")
    private java.lang.String column;
    /**
     * 数据库字段:field  属性显示:属性
     */
    @ApiModelProperty(value = "属性")
    private java.lang.String field;
    /**
     * 数据库字段:sqlType  属性显示:字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private java.lang.String sqlType;
    /**
     * 数据库字段:fieldType  属性显示:属性类型
     */
    @ApiModelProperty(value = "属性类型")
    private java.lang.String fieldType;
    /**
     * 数据库字段:notes  属性显示:注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;
    /**
     * 数据库字段:defaultValue  属性显示:字段默认值
     */
    @ApiModelProperty(value = "字段默认值")
    private java.lang.String defaultValue;
    /**
     * 数据库字段:isPrimaryKey  属性显示:是否是主键
     */
    @ApiModelProperty(value = "是否是主键")
    private java.lang.String isPrimaryKey;
    /**
     * 数据库字段:isDate  属性显示:是否是时间类型
     */
    @ApiModelProperty(value = "是否是时间类型")
    private java.lang.String isDate;
    /**
     * 数据库字段:isState  属性显示:是否是状态
     */
    @ApiModelProperty(value = "是否是状态")
    private java.lang.String isState;

}
