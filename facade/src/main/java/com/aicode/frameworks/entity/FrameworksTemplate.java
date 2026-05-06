/*
 * demo
 */
package com.aicode.frameworks.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 框架配置文件模板 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FrameworksTemplate implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 模板编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:frameworkCode 框架编码")
    @TableField("frameworkCode")
    private java.lang.String frameworkCode;
    @Schema(description = "数据库字段:path 模板路径")
    private java.lang.String path;

}
