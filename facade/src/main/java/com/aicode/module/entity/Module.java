/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 第三方模块池 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Module implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String code;
    /**
     * 数据库字段:name  属性显示:模块名
     */
    @ApiModelProperty(value = "模块名")
    private java.lang.String name;
    /**
     * 数据库字段:description  属性显示:模块说明
     */
    @ApiModelProperty(value = "模块说明")
    private java.lang.String description;

}
