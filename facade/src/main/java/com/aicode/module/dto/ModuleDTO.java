/**
 * aicode
 */
package com.aicode.module.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 第三方模块池 VO
 *
 * @author aicode
 */
@Data
public class ModuleDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 模块编码")
    private String code;
    @Schema(description = "数据库字段:name 模块名")
    private String name;
    @Schema(description = "数据库字段:description 模块说明")
    private String description;
}
