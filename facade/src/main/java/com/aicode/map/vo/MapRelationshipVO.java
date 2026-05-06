/**
 * aicode
 */
package com.aicode.map.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型关系 VO
 *
 * @author aicode
 */
@Data
public class MapRelationshipVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 关系编码")
    private String code;
    @Schema(description = "数据库字段:mapClassTableCode 映射编码")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:associateCode 被关联编码")
    private String associateCode;
    @Schema(description = "数据库字段:isOneToOne 是否一对一 Y N")
    private String isOneToOne;
    @Schema(description = "数据库字段:isOneToMany 是否一对多Y N")
    private String isOneToMany;
    @Schema(description = "数据库字段:mainField 主表关联属性")
    private String mainField;
    @Schema(description = "数据库字段:joinField 从表关联属性")
    private String joinField;
}
