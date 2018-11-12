/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.core.StringHelper;
import io.aicode.base.core.typemapping.DatabaseDataTypesUtils;
import io.aicode.core.base.BaseEntity;
import io.aicode.core.enums.YNEnum;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class MapFieldColumn extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String mapClassTableCode;//数据库字段:mapClassTableCode  属性显示:映射编码
    private String code;//数据库字段:code  属性显示:字段编码
    private String column;//数据库字段:column  属性显示:字段
    private String field;//数据库字段:field  属性显示:属性
    private String sqlType;//数据库字段:sqlType  属性显示:字段类型
    private String fieldType;//数据库字段:fieldType  属性显示:属性类型
    private String notes;//数据库字段:notes  属性显示:注释
    private String defaultValue;//数据库字段:defaultValue  属性显示:字段默认值
    private String isPrimaryKey;//数据库字段:isPrimaryKey  属性显示:是否是主键
    private String isDate;//数据库字段:isDate  属性显示:是否是时间类型
    private String isState;//数据库字段:isState  属性显示:是否是状态

    private boolean checkDate;
    private boolean checkState;
    private boolean checkPk;
    private boolean checkDigit = false;

    public MapFieldColumn() {
    }

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

}

