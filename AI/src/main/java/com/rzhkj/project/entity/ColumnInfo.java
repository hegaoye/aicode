/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ColumnInfo extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String tableCode;//数据库字段:tableCode  属性显示:表编码
    private String code;//数据库字段:code  属性显示:字段编码
    private String name;//数据库字段:name  属性显示:字段名
    private String type;//数据库字段:type  属性显示:字段类型
    private String notes;//数据库字段:notes  属性显示:字段注释
    private String defaultValue;//数据库字段:defaultValue  属性显示:字段默认值
    private String isPrimaryKey;//数据库字段:isPrimaryKey  属性显示:是否是主键
    private String isDate;//数据库字段:isDate  属性显示:是否是时间类型
    private String isState;//数据库字段:isState  属性显示:是否是状态

    public ColumnInfo() {
    }

    public ColumnInfo(String tableCode, String code, String name, String type, String notes, String defaultValue, String isPrimaryKey, String isDate, String isState) {
        this.tableCode = tableCode;
        this.code = code;
        this.name = name;
        this.type = type;
        this.notes = notes;
        this.defaultValue = defaultValue;
        this.isPrimaryKey = isPrimaryKey;
        this.isDate = isDate;
        this.isState = isState;
    }
}

