/*
 * demo
 */
package com.aicode.map.entity;

import com.aicode.core.enums.YNEnum;
import com.aicode.core.tools.core.StringHelper;
import com.aicode.core.tools.core.typemapping.DatabaseDataTypesUtils;
import com.aicode.display.entity.DisplayAttribute;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 字段属性映射信息 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapFieldColumn implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:mapClassTableCode 映射编码")
    @TableField("mapClassTableCode")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:code 字段属性映射编码")
    private String code;
    @Schema(description = "数据库字段:column 字段")
    private String column;
    @Schema(description = "数据库字段:field 属性")
    private String field;
    @Schema(description = "数据库字段:sqlType 字段类型")
    @TableField("sqlType")
    private String sqlType;
    @Schema(description = "数据库字段:fieldType 属性类型")
    @TableField("fieldType")
    private String fieldType;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;
    @Schema(description = "数据库字段:defaultValue 字段默认值")
    @TableField("defaultValue")
    private String defaultValue;
    @Schema(description = "数据库字段:isPrimaryKey 是否是主键")
    @TableField("isPrimaryKey")
    private String isPrimaryKey;
    @Schema(description = "数据库字段:isDate 是否是时间类型")
    @TableField("isDate")
    private String isDate;
    @Schema(description = "数据库字段:isState 是否是状态")
    @TableField("isState")
    private String isState;


    @TableField(exist = false)
    private DisplayAttribute displayAttribute;

    @TableField(exist = false)
    private MapRelationship mapRelationship;

    @TableField(exist = false)
    private boolean checkDate;
    @TableField(exist = false)
    private boolean checkState;
    @TableField(exist = false)
    private boolean checkPk;
    @TableField(exist = false)
    private boolean checkDigit = false;
    @TableField(exist = false)
    private String upper;


    public MapFieldColumn(String mapClassTableCode, String code, String column, String sqlType, String notes, String defaultValue, String isPrimaryKey) {
        this.mapClassTableCode = mapClassTableCode;
        this.code = code;
        this.column = column;
        this.sqlType = sqlType;
        this.notes = notes;
        this.defaultValue = defaultValue;
        this.isPrimaryKey = isPrimaryKey;
    }

    public void toJava() {
        this.field = StringHelper.toJavaVariableName(this.column);
        //        if (this.field.equals("id")) {
        //            this.fieldType = "java.lang.Long";
        //        } else {
        this.fieldType = DatabaseDataTypesUtils.getPreferredJavaType(this.sqlType);
        //        }
    }


    public boolean getCheckDate() {
        return isDate.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckState() {
        return isState.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckPk() {
        return isPrimaryKey.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckDigit() {
        this.checkDigit = DatabaseDataTypesUtils.isIntegerNumber(this.fieldType);
        if (!this.checkDigit) {
            this.checkDigit = DatabaseDataTypesUtils.isFloatNumber(this.fieldType);
        }
        return checkDigit;
    }

    public String getUpper() {
        this.upper = StringHelper.capitalize(field);
        return this.upper;
    }

}
