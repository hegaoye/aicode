/**
 * aicode
 */
package com.aicode.map.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存 字段属性映射信息 VO
 *
 * @author aicode
 */
@Data
public class MapFieldColumnSaveDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:mapClassTableCode 映射编码")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:code 字段属性映射编码")
    private String code;
    @Schema(description = "数据库字段:column 字段")
    private String column;
    @Schema(description = "数据库字段:field 属性")
    private String field;
    @Schema(description = "数据库字段:sqlType 字段类型")
    private String sqlType;
    @Schema(description = "数据库字段:fieldType 属性类型")
    private String fieldType;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;
    @Schema(description = "数据库字段:defaultValue 字段默认值")
    private String defaultValue;
    @Schema(description = "数据库字段:isPrimaryKey 是否是主键")
    private String isPrimaryKey;
    @Schema(description = "数据库字段:isDate 是否是时间类型")
    private String isDate;
    @Schema(description = "数据库字段:isState 是否是状态")
    private String isState;
}
