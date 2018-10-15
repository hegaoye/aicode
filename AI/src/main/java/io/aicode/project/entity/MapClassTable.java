/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.core.StringHelper;
import io.aicode.core.base.BaseEntity;
import io.aicode.core.tools.StringTools;
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


    private String classModel;//类所在模块
    private String dashedCaseName;//破折号命名 或叫烤串命名 适用于 前端angular ,react, vue

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

    public String getDashedCaseName() {
        return StringTools.humpToLine(this.className);
    }
}

