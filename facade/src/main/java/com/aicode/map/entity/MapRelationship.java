/*
 * demo
 */
package com.aicode.map.entity;

import com.aicode.core.enums.YNEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 模型关系 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapRelationship implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 关系编码")
    private String code;
    @Schema(description = "数据库字段:mapClassTableCode 映射编码")
    @TableField("mapClassTableCode")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:associateCode 被关联编码")
    @TableField("associateCode")
    private String associateCode;
    @Schema(description = "数据库字段:isOneToOne 是否一对一 Y N")
    @TableField("isOneToOne")
    private String isOneToOne;
    @Schema(description = "数据库字段:isOneToMany 是否一对多Y N")
    @TableField("isOneToMany")
    private String isOneToMany;
    @Schema(description = "数据库字段:mainField 主表关联属性")
    @TableField("mainField")
    private String mainField;
    @Schema(description = "数据库字段:joinField 从表关联属性")
    @TableField("joinField")
    private String joinField;

    @TableField(exist = false)
    private MapClassTable associateClass;//被关联对象被查询

    @TableField(exist = false)
    private boolean oneToOne;
    @TableField(exist = false)
    private boolean oneToMany;

    public boolean getOneToOne() {
        return isOneToOne.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getOneToMany() {
        return isOneToMany.equals(YNEnum.Y.name()) ? true : false;
    }

}
