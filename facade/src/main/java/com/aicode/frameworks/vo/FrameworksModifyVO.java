/**
 * aicode
 */
package com.aicode.frameworks.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改 框架技术池 VO
 *
 * @author aicode
 */
@Data
public class FrameworksModifyVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 技术编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:name 技术名称")
    private java.lang.String name;
    @Schema(description = "数据库字段:description 技术描述")
    private java.lang.String description;
    @Schema(description = "数据库字段:gitHome ")
    private java.lang.String gitHome;
    @Schema(description = "数据库字段:account ")
    private java.lang.String account;
    @Schema(description = "数据库字段:password ")
    private java.lang.String password;
    @Schema(description = "数据库字段:isPublic ")
    private java.lang.String isPublic;
}
