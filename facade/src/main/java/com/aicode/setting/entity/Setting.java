/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 设置 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Setting implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:k  属性显示:键
     */
    @ApiModelProperty(value = "键")
    private java.lang.String k;
    /**
     * 数据库字段:v  属性显示:值
     */
    @ApiModelProperty(value = "值")
    private java.lang.String v;
    /**
     * 数据库字段:description  属性显示:说明
     */
    @ApiModelProperty(value = "说明")
    private java.lang.String description;

}
