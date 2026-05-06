/*
 * demo
 */
package com.aicode.module.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 第三方模块池 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Module implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 模块编码")
    private String code;
    @Schema(description = "数据库字段:name 模块名")
    private String name;
    @Schema(description = "数据库字段:description 模块说明")
    private String description;

}
