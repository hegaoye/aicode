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
public class ClassAttributes extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String columnCode;//数据库字段:columnCode  属性显示:字段编码
    private String classInfoCode;//数据库字段:classInfoCode  属性显示:类编码
    private String code;//数据库字段:code  属性显示:属性编码
    private String name;//数据库字段:name  属性显示:属性名
    private String type;//数据库字段:type  属性显示:属性类型:String,int,boolean,float,double,Date,byte,short,long
    private String notes;//数据库字段:notes  属性显示:属性注释
    private String isPrimaryKey;//数据库字段:isPrimaryKey  属性显示:是否是主键
    private String isDate;//数据库字段:isDate  属性显示:是否是时间类型
    private String isState;//数据库字段:isState  属性显示:是否是状态


    private ColumnInfo columnInfo;//列信息

    public ClassAttributes() {
    }

    public ClassAttributes(String columnCode, String classInfoCode, String code, String name, String type, String notes, String isPrimaryKey) {
        this.columnCode = columnCode;
        this.classInfoCode = classInfoCode;
        this.code = code;
        this.name = name;
        this.type = type;
        this.notes = notes;
        this.isPrimaryKey = isPrimaryKey;
    }
}

