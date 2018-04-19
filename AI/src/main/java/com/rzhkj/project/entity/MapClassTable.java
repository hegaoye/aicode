/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.base.core.StringHelper;
import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class MapClassTable extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private String code;//数据库字段:code  属性显示:表编码
    private String tableName;//数据库字段:tableName  属性显示:表名
    private String className;//数据库字段:类名  属性显示:类名
    private String notes;//数据库字段:notes  属性显示:注释


    private String classModel;

    private List<MapFieldColumn> mapFieldColumnList;//one to many
    private List<MapRelationship> mapRelationshipList;//one to many

    public MapClassTable() {
    }

    public MapClassTable(String code, String name, String notes) {
        this.code = code;
        this.tableName = name;
        this.notes = notes;
    }

    public void toJava() {
        this.className = StringHelper.toJavaClassName(this.tableName);
    }

    public String getClassModel() {
        return this.classModel = tableName.contains("_") ? tableName.substring(0, tableName.indexOf("_")) : tableName;
    }
}

