/*
 * demo
 */
package com.aicode.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 模块文件信息 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModuleFile implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:moudleCode 模块编码")
    @TableField("moudleCode")
    private String moudleCode;
    @Schema(description = "数据库字段:path 文件路径")
    private String path;

}
