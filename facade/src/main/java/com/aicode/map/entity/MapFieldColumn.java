/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

import com.aicode.core.enums.YNEnum;
import com.aicode.core.tools.core.StringHelper;
import com.aicode.core.tools.core.typemapping.DatabaseDataTypesUtils;
import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.vo.DisplayAttributeVO;
import com.aicode.map.vo.MapRelationshipVO;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 字段属性映射信息 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapFieldColumn implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:mapClassTableCode  属性显示:映射编码
     */
    @ApiModelProperty(value = "映射编码")
    @TableField("mapClassTableCode")
    private java.lang.String mapClassTableCode;
    /**
     * 数据库字段:code  属性显示:字段属性映射编码
     */
    @ApiModelProperty(value = "字段属性映射编码")
    private java.lang.String code;
    /**
     * 数据库字段:column  属性显示:字段
     */
    @ApiModelProperty(value = "字段")
    @TableField("`column`")
    private java.lang.String column;
    /**
     * 数据库字段:field  属性显示:属性
     */
    @ApiModelProperty(value = "属性")
    private java.lang.String field;
    /**
     * 数据库字段:sqlType  属性显示:字段类型
     */
    @ApiModelProperty(value = "字段类型")
    @TableField("sqlType")
    private java.lang.String sqlType;
    /**
     * 数据库字段:fieldType  属性显示:属性类型
     */
    @ApiModelProperty(value = "属性类型")
    @TableField("fieldType")
    private java.lang.String fieldType;
    /**
     * 数据库字段:notes  属性显示:注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;
    /**
     * 数据库字段:defaultValue  属性显示:字段默认值
     */
    @ApiModelProperty(value = "字段默认值")
    @TableField("defaultValue")
    private java.lang.String defaultValue;
    /**
     * 数据库字段:isPrimaryKey  属性显示:是否是主键
     */
    @ApiModelProperty(value = "是否是主键")
    @TableField("isPrimaryKey")
    private java.lang.String isPrimaryKey;
    /**
     * 数据库字段:isDate  属性显示:是否是时间类型
     */
    @ApiModelProperty(value = "是否是时间类型")
    @TableField("isDate")
    private java.lang.String isDate;
    /**
     * 数据库字段:isState  属性显示:是否是状态
     */
    @ApiModelProperty(value = "是否是状态")
    @TableField("isState")
    private java.lang.String isState;

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
        if (this.field.equals("id")) {
            this.fieldType = "java.lang.Long";
        } else {
            this.fieldType = DatabaseDataTypesUtils.getPreferredJavaType(this.sqlType);
        }
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
