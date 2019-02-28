package io.aicode.base.core;

import com.alibaba.fastjson.JSON;
import io.aicode.core.tools.StringTools;
import io.aicode.display.entity.DisplayAttribute;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 映射模板的输出数据定义
 * 需要新参数在此类中定义即可
 * 模板采用freemarker
 * 模板可以拿到如下数据：
 * ${projectName} 项目英文名
 * ${model}   模块中的模型 -> ${basePackage}.${model}.service
 * ${module} 模块 一个项目的模块化 不参与java的包定义只是项目管理分离办法
 * ${basePackage}  包名
 * ${table}  表对象
 * ${clazz}  类对象
 * ${tableName}  表名
 * ${className}  类名
 * ${classNameLower}  类名小写
 * ${notes}  类注释
 * ${copyright}  项目版权
 * ${author}  作者
 * <p>
 * ${classes}  类信息对象  集合
 * ${columns}  列对象  集合
 * ${pkColumns}  主键数据信息
 * ${notPkColumns}  非主键数据信息
 * ${fields}  类属性  集合
 * ${pkFields}  主键数据信息
 * ${notPkFields}  非主键主键数据信息
 * ${modelClasses} 各个模块下的所有类集合信息
 * ${relationships} 类与类之间的关联关系模型集合信息
 * ${displayAttributes} 所有类下面的属性集合信息
 * ${tableFields} 前端页面显示
 * ${modelDatas} 模型与实体类的关系
 * ${dashedCaseName} 破折号命名 或叫烤串命名 适用于 前端angular ,react, vue
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

    private List<MapClassTable> classes = new ArrayList<>();//类信息对象  集合
    private List<MapFieldColumn> columns = new ArrayList<>();  //列对象  集合
    private List<MapFieldColumn> pkColumns = new ArrayList<>();//主键数据信息
    private List<MapFieldColumn> notPkColumns = new ArrayList<>();  //非主键数据信息
    //    private List<MapFieldColumn> fields = new ArrayList<>();  //类属性  集合
    private List<Field> fields = new ArrayList<>();//所有类下面的属性集合信息
    private List<MapFieldColumn> pkFields = new ArrayList<>();  //主键数据信息
    private List<MapFieldColumn> notPkFields = new ArrayList<>();  //非主键主键数据信息
    private List<MapClassTable> modelClasses = new ArrayList<>();//各个模块下的所有类集合信息
    private List<TemplateData> oneToOneList = new ArrayList<>();//1对1集合
    private List<TemplateData> oneToManyList = new ArrayList<>();//1对多集合
    private boolean isRelation;//是否有关联关系
    private String mainField;//主表关联属性
    private String joinField;//从表关联属性

    //**********前端生成代码使用：start***********
    private List<DisplayAttribute> displayAttributes = new ArrayList<>();//所有类下面的属性集合信息
    private List<MapFieldColumn> tableFields = new ArrayList<>();  //前端页面显示
    private List<ModelData> modelDatas = new ArrayList<>();//模型与实体类的关系
    private String dashedCaseName;//破折号命名 或叫烤串命名 适用于 前端angular ,react, vue

    //**********前端生成代码使用：end***********


    //TODO {定义模板变量}
    public TemplateData(Project project, MapClassTable classTable, List<MapClassTable> classes, List<MapFieldColumn> columns,
                        List<MapFieldColumn> pkColumns, List<MapFieldColumn> notPkColumns, List<MapFieldColumn> tableColumns,
                        List<MapClassTable> modelClasses, List<ModelData> modelDatas, List<TemplateData> oneToOneList,
                        List<TemplateData> oneToManyList) {
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

//        this.fields = columns;
        if (columns != null && !columns.isEmpty()) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            for (MapFieldColumn mapFieldColumn : columns) {
                String json = JSON.toJSONString(mapFieldColumn);
                Field field = JSON.parseObject(json, Field.class);
                field.setIsRequired(mapFieldColumn.getDisplayAttribute().getIsRequired());
                field.setIsInsert(mapFieldColumn.getDisplayAttribute().getIsInsert());
                field.setIsDeleteCondition(mapFieldColumn.getDisplayAttribute().getIsDeleteCondition());
                field.setIsAllowUpdate(mapFieldColumn.getDisplayAttribute().getIsAllowUpdate());
                field.setIsListPageDisplay(mapFieldColumn.getDisplayAttribute().getIsListPageDisplay());
                field.setIsDetailPageDisplay(mapFieldColumn.getDisplayAttribute().getIsDetailPageDisplay());
                field.setIsQueryRequired(mapFieldColumn.getDisplayAttribute().getIsQueryRequired());
                field.setIsLineNew(mapFieldColumn.getDisplayAttribute().getIsLineNew());
                field.setMatchType(mapFieldColumn.getDisplayAttribute().getMatchType());
                field.setDisplayType(mapFieldColumn.getDisplayAttribute().getDisplayType());
                field.setDisplayCss(mapFieldColumn.getDisplayAttribute().getDisplayCss());
                field.setDisplayName(mapFieldColumn.getDisplayAttribute().getDisplayName());
                field.setDisplayNo(mapFieldColumn.getDisplayAttribute().getDisplayNo());
                field.setFieldValidationMode(mapFieldColumn.getDisplayAttribute().getFieldValidationMode());
                field.setValidateText(mapFieldColumn.getDisplayAttribute().getValidateText());
                this.fields.add(field);
            }
        }
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
        this.oneToOneList = oneToOneList;
        this.oneToManyList = oneToManyList;
        this.isRelation = false;
        if (oneToManyList != null && oneToOneList != null) {
            if (!oneToManyList.isEmpty() || !oneToOneList.isEmpty()) {
                this.isRelation = true;
            }
        }
    }


    public TemplateData(Project project, MapClassTable classTable, String mainField, String joinField) {
        this.projectName = project.getEnglishName();
        this.basePackage = project.getBasePackage();
        this.copyright = project.getCopyright();
        this.author = project.getAuthor();
        this.table = classTable;
        this.clazz = classTable;
        this.tableName = classTable.getTableName();
        this.className = classTable.getClassName();
        this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName());
        this.mainField = mainField;
        this.joinField = joinField;
        this.notes = classTable.getNotes();
        if (classTable.getTableName().contains("_")) {
            this.model = classTable.getTableName().substring(0, classTable.getTableName().indexOf("_"));
        } else {
            this.model = classTable.getTableName();
        }
    }
}
