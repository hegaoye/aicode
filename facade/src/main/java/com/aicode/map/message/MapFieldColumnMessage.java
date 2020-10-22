/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * map 消息体针对 测试业务
 */
@Data
public class MapFieldColumnMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String mapClassTableCode;
    /**
     * 字段属性映射编码
     */
    @ApiModelProperty(value = "字段属性映射编码")
    private java.lang.String code;
    /**
     * 字段
     */
    @ApiModelProperty(value = "字段")
    private java.lang.String column;
    /**
     * 属性
     */
    @ApiModelProperty(value = "属性")
    private java.lang.String field;
    /**
     * 字段类型
     */
    @ApiModelProperty(value = "字段类型")
    private java.lang.String sqlType;
    /**
     * 属性类型
     */
    @ApiModelProperty(value = "属性类型")
    private java.lang.String fieldType;
    /**
     * 注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;
    /**
     * 字段默认值
     */
    @ApiModelProperty(value = "字段默认值")
    private java.lang.String defaultValue;
    /**
     * 是否是主键
     */
    @ApiModelProperty(value = "是否是主键")
    private java.lang.String isPrimaryKey;
    /**
     * 是否是时间类型
     */
    @ApiModelProperty(value = "是否是时间类型")
    private java.lang.String isDate;
    /**
     * 是否是状态
     */
    @ApiModelProperty(value = "是否是状态")
    private java.lang.String isState;

}
