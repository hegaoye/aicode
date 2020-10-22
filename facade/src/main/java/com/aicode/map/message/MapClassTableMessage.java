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
public class MapClassTableMessage implements java.io.Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String code;
    /**
     * 表名
     */
    @ApiModelProperty(value = "表名")
    private java.lang.String tableName;
    /**
     * 类名
     */
    @ApiModelProperty(value = "类名")
    private java.lang.String className;
    /**
     * 注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;

}
