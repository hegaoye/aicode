/**
 * aicode
 */
package com.aicode.frameworks.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改 框架配置文件模板 VO
 *
 * @author aicode
 */
@Data
public class FrameworksTemplateModifyVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private java.lang.Long id;
    @Schema(description = "数据库字段:code 模板编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:frameworkCode 框架编码")
    private java.lang.String frameworkCode;
    @Schema(description = "数据库字段:path 模板路径")
    private java.lang.String path;
}
