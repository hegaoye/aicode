/**
 * aicode
 */
package com.aicode.module.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模块文件信息 VO
 *
 * @author aicode
 */
@Data
public class ModuleFileDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:moudleCode 模块编码")
    private String moudleCode;
    @Schema(description = "数据库字段:path 文件路径")
    private String path;
}
