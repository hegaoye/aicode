/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 框架技术池 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Frameworks implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:技术编码
     */
    @ApiModelProperty(value = "技术编码")
    private java.lang.String code;
    /**
     * 数据库字段:name  属性显示:技术名称
     */
    @ApiModelProperty(value = "技术名称")
    private java.lang.String name;
    /**
     * 数据库字段:description  属性显示:技术描述
     */
    @ApiModelProperty(value = "技术描述")
    private java.lang.String description;
    /**
     * 数据库字段:gitHome  属性显示:
     */
    @ApiModelProperty(value = "")
    @TableField("gitHome")
    private java.lang.String gitHome;
    /**
     * 数据库字段:account  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.String account;
    /**
     * 数据库字段:password  属性显示:
     */
    @ApiModelProperty(value = "")
    @JSONField(serialize = false)
    private java.lang.String password;
    /**
     * 数据库字段:isPublic  属性显示:
     */
    @ApiModelProperty(value = "")
    @TableField("isPublic")
    private java.lang.String isPublic;

    @TableField(exist = false)
    private List<FrameworksTemplate> frameworksTemplateList;


}
