/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.frameworks.entity.Frameworks;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目应用技术 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectFramwork implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:frameworkCode  属性显示:技术编码
     */
    @ApiModelProperty(value = "技术编码")
    private java.lang.String frameworkCode;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;

    @TableField(exist = false)
    private Frameworks frameworks;

}
