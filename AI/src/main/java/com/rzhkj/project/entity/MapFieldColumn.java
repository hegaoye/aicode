/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.base.core.StringHelper;
import com.rzhkj.base.core.typemapping.DatabaseDataTypesUtils;
import com.rzhkj.core.base.BaseEntity;
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
        this.fieldType = DatabaseDataTypesUtils.getPreferredJavaType(this.sqlType);
    }


}

