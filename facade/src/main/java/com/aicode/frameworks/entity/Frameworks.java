/*
 * demo
 */
package com.aicode.frameworks.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 框架技术池 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Frameworks implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 技术编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:name 技术名称")
    private java.lang.String name;
    @Schema(description = "数据库字段:description 技术描述")
    private java.lang.String description;
    @Schema(description = "数据库字段:gitHome ")
    @TableField("gitHome")
    private java.lang.String gitHome;
    @Schema(description = "数据库字段:account ")
    private java.lang.String account;
    @Schema(description = "数据库字段:password ")
    private java.lang.String password;
    @Schema(description = "数据库字段:isPublic ")
    @TableField("isPublic")
    private java.lang.String isPublic;

}
