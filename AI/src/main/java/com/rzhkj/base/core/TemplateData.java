package com.rzhkj.base.core;

import com.rzhkj.project.entity.MapClassTable;
import com.rzhkj.project.entity.MapFieldColumn;
import com.rzhkj.project.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 映射模板的输出数据定义
 * Created by lixin on 2018/1/25.
 */
@Data
public class TemplateData implements Serializable {
    private String projectName; //项目英文名
    private String model;   //模块中的模型 -> private String basePackage;.private String model;.service
    private String module; //模块 一个项目的模块化 不参与java的包定义只是项目管理分离办法
    private String basePackage;  //包名
    private MapClassTable table;  //表对象
    private MapClassTable clazz;  //类对象
    private String tableName;  //表名
    private String className;  //类名
    private String classNameLower;  //类名小写
    private String notes;  //类注释
    private String copyright;  //项目版权
    private String author;  //作者

    private List<MapClassTable> classes;//类信息对象  集合
    private List<MapFieldColumn> columns;  //列对象  集合
    private List<MapFieldColumn> pkColumns;//主键数据信息
    private List<MapFieldColumn> notPkColumns;  //非主键数据信息
    private List<MapFieldColumn> fields;  //类属性  集合
    private List<MapFieldColumn> pkFields;  //主键数据信息
    private List<MapFieldColumn> notPkFields;  //非主键主键数据信息

    public TemplateData() {
    }

    public TemplateData(Project project, MapClassTable classTable, List<MapClassTable> classes, List<MapFieldColumn> columns, List<MapFieldColumn> pkColumns, List<MapFieldColumn> notPkColumns) {
        this.projectName = project.getEnglishName();
        this.basePackage = project.getBasePackage();
        this.copyright = project.getCopyright();
        this.author = project.getAuthor();

        this.table = classTable;
        this.clazz = classTable;
        this.tableName = classTable.getTableName();
        this.className = classTable.getClassName();
        this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName());
        this.notes = classTable.getNotes();
        this.classes = classes;

        this.columns = columns;
        this.pkColumns = pkColumns;
        this.notPkColumns = notPkColumns;
        this.fields = columns;
        this.pkFields = pkColumns;
        this.notPkFields = notPkColumns;
        if (classTable.getTableName().contains("_")) {
            this.model = classTable.getTableName().substring(0, classTable.getTableName().indexOf("_"));
        } else {
            this.model = classTable.getTableName();
        }
    }
}
