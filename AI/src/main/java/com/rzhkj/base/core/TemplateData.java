package com.rzhkj.base.core;

import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.entity.MapClassTable;
import com.rzhkj.project.entity.MapFieldColumn;
import com.rzhkj.project.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 映射模板的输出数据定义
 * 需要新参数在此类中定义即可
 * 模板采用freemarker
 * 模板可以拿到如下数据：
 * ${projectName} //项目英文名
 * ${model}   //模块中的模型 -> ${basePackage}.${model}.service
 * ${module} //模块 一个项目的模块化 不参与java的包定义只是项目管理分离办法
 * <p>
 * ${basePackage}  //包名
 * <p>
 * ${table}  //表对象
 * ${tableName}  //表名
 * <p>
 * ${classes}  //类信息对象  集合
 * ${class}  //类对象
 * ${className}  //类名
 * ${classNameLower}  //类名小写
 * <p>
 * ${columns}  //列对象  集合
 * ${pkColumns}  //主键数据信息
 * ${notPkColumns}  //非主键数据信息
 * <p>
 * <p>
 * ${fields}  //类属性  集合
 * ${pkFields}  //主键数据信息
 * ${notPkFields}  //非主键主键数据信息
 * <p>
 * ${notes}  //类注释
 * ${copyright}  //项目版权
 * ${author}  //作者
 * <p>
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
    private List<MapClassTable> modelClasses;//各个模块下的所有类集合信息
    //**********前端生成代码使用：start***********
    private List<MapFieldColumn> tableFields;  //前端页面显示
    private List<ModelData> modelDatas;//模型与实体类的关系
    private String dashedCaseName;//破折号命名 或叫烤串命名 适用于 前端angular ,react, vue

    //**********前端生成代码使用：end***********


    //TODO {定义模板变量}
    public TemplateData(Project project, MapClassTable classTable, List<MapClassTable> classes, List<MapFieldColumn> columns, List<MapFieldColumn> pkColumns, List<MapFieldColumn> notPkColumns, List<MapFieldColumn> tableColumns, List<MapClassTable> modelClasses, List<ModelData> modelDatas) {
        this.projectName = project.getEnglishName();
        this.basePackage = project.getBasePackage();
        this.copyright = project.getCopyright();
        this.author = project.getAuthor();

        this.table = classTable;
        this.clazz = classTable;
        this.tableName = classTable.getTableName();
        this.className = classTable.getClassName();
        this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName());
        this.dashedCaseName = StringTools.humpToLine(classNameLower);
        for (MapFieldColumn mapFieldColumn : pkColumns) {
            if (mapFieldColumn.getField().equalsIgnoreCase(classTable.getClassName())) {
                this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName()) + "Obj";
                break;
            }
        }
        for (MapFieldColumn mapFieldColumn : notPkColumns) {
            if (mapFieldColumn.getField().equalsIgnoreCase(classTable.getClassName())) {
                this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName()) + "Obj";
                break;
            }
        }

        for (MapFieldColumn mapFieldColumn : tableColumns) {
            if (mapFieldColumn.getField().equalsIgnoreCase(classTable.getClassName())) {
                this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName()) + "Obj";
                break;
            }
        }
        this.notes = classTable.getNotes();
        this.classes = classes;

        this.columns = columns;
        this.pkColumns = pkColumns;
        this.notPkColumns = notPkColumns;
        this.fields = columns;
        this.pkFields = pkColumns;
        this.notPkFields = notPkColumns;
        this.tableFields = tableColumns;
        if (classTable.getTableName().contains("_")) {
            this.model = classTable.getTableName().substring(0, classTable.getTableName().indexOf("_"));
        } else {
            this.model = classTable.getTableName();
        }
        this.modelClasses = modelClasses;
        this.modelDatas = modelDatas;
    }
}
