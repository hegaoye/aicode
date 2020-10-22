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
public class MapRelationshipMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 关系编码
     */
    @ApiModelProperty(value = "关系编码")
    private java.lang.String code;
    /**
     * 映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String mapClassTableCode;
    /**
     * 被关联编码
     */
    @ApiModelProperty(value = "被关联编码")
    private java.lang.String associateCode;
    /**
     * 是否一对一 Y N
     */
    @ApiModelProperty(value = "是否一对一 Y N")
    private java.lang.String isOneToOne;
    /**
     * 是否一对多Y N
     */
    @ApiModelProperty(value = "是否一对多Y N")
    private java.lang.String isOneToMany;
    /**
     * 主表关联属性
     */
    @ApiModelProperty(value = "主表关联属性")
    private java.lang.String mainField;
    /**
     * 从表关联属性
     */
    @ApiModelProperty(value = "从表关联属性")
    private java.lang.String joinField;

}
