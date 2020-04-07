package io.aicode.base.core;

import io.aicode.base.enums.YNEnum;
import io.aicode.base.tools.StringTools;
import io.aicode.display.entity.DisplayAttribute;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.entity.MapState;
import io.aicode.project.entity.Project;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    /**
     * 项目英文名
     */
    private String projectName;
    /**
     * 项目英文名
     */
    private String projectname;
    /**
     * 模块中的模型
     */
    private String model;
    /**
     * 模块 一个项目的模块化 不参与java的包定义只是项目管理分离办法
     */
    private String module;
    /**
     * 包名
     */
    private String basePackage;
    private String basepackage;
    /**
     * 表对象
     */
    private MapClassTable table;
    /**
     * 类对象
     */
    private MapClassTable clazz;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 类名
     */
    private String className;
    /**
     * 类名小写
     */
    private String classNameLower;
    /**
     * 类注释
     */
    private String notes;
    /**
     * 项目版权
     */
    private String copyright;
    /**
     * 作者
     */
    private String author;

    /**
     * 类信息对象  集合
     */
    private List<MapClassTable> classes = new ArrayList<>();
    /**
     * 列对象  集合
     */
    private List<MapFieldColumn> columns = new ArrayList<>();
    /**
     * 主键数据信息
     */
    private List<MapFieldColumn> pkColumns = new ArrayList<>();
    /**
     * 非主键数据信息
     */
    private List<MapFieldColumn> notPkColumns = new ArrayList<>();
    /**
     * 所有类下面的属性集合信息
     */
    private List<Field> fields = new ArrayList<>();
    /**
     * 主键数据信息
     */
    private List<MapFieldColumn> pkFields = new ArrayList<>();
    /**
     * 非主键主键数据信息
     */
    private List<MapFieldColumn> notPkFields = new ArrayList<>();
    /**
     * 各个模块下的所有类集合信息
     */
    private List<MapClassTable> modelClasses = new ArrayList<>();
    /**
     * 1对1集合
     */
    private List<TemplateData> oneToOneList = new ArrayList<>();
    /**
     * 1对多集合
     */
    private List<TemplateData> oneToManyList = new ArrayList<>();
    /**
     * 类的枚举
     */
    private List<MapState> states = new ArrayList<>();

    /**
     * 是否有关联关系
     */
    private boolean isRelation;
    /**
     * 主表关联属性
     */
    private String mainField;
    /**
     * 从表关联属性
     */
    private String joinField;

    //**********前端生成代码使用：start***********
    /**
     * 所有类下面的属性集合信息
     */
    private List<DisplayAttribute> displayAttributes = new ArrayList<>();
    /**
     * 前端页面显示
     */
    private List<MapFieldColumn> tableFields = new ArrayList<>();
    /**
     * 模型与实体类的关系
     */
    private List<ModelData> modelDatas = new ArrayList<>();
    /**
     * 破折号命名 或叫烤串命名 适用于 前端angular ,react, vue
     */
    private String dashedCaseName;

    //**********前端生成代码使用：end***********

    public TemplateData() {
    }

    //TODO {定义模板变量}
    public TemplateData(Project project, MapClassTable classTable, List<MapClassTable> classes, List<MapFieldColumn> columns,
                        List<MapFieldColumn> pkColumns, List<MapFieldColumn> notPkColumns, List<MapFieldColumn> tableColumns,
                        List<MapClassTable> modelClasses, List<ModelData> modelDatas, List<TemplateData> oneToOneList,
                        List<TemplateData> oneToManyList) {
        this.projectName = project.getEnglishName();
        this.projectname = project.getEnglishName();
        this.basePackage = project.getBasePackage();
        this.basepackage = project.getBasePackage();
        this.copyright = project.getCopyright();
        this.author = project.getAuthor();

        this.table = classTable;
        this.clazz = classTable;
        this.tableName = classTable.getTableName();
        this.className = classTable.getClassName();
        this.classNameLower = StringHelper.toJavaVariableName(classTable.getClassName());
        this.dashedCaseName = StringTools.humpToLine(classNameLower);

        this.fixColumns(pkColumns, classTable);
        this.fixColumns(notPkColumns, classTable);
        this.fixColumns(tableColumns, classTable);

        this.notes = classTable.getNotes();
        this.classes = classes;

        this.columns = columns;
        this.pkColumns = pkColumns;
        this.notPkColumns = notPkColumns;


        this.pkFields = pkColumns;
        this.notPkFields = notPkColumns;
        this.tableFields = tableColumns;
        if (classTable.getTableName().contains("_")) {
            this.model = classTable.getTableName().substring(0, classTable.getTableName().indexOf("_"));
        } else {
            this.model = classTable.getTableName();
        }
        //前端用于属性显示的转换
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

        this.fields(columns);
    }


    public TemplateData(Project project, MapClassTable classTable, String mainField, String joinField, List<MapFieldColumn> columns) {
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

        this.fields(columns);
    }

    /**
     * 纠正不合法的列命名进行自动转化
     *
     * @param fieldColumns 列集合
     * @param classTable   类模型映射
     */
    private void fixColumns(List<MapFieldColumn> fieldColumns, MapClassTable classTable) {
        for (MapFieldColumn mapFieldColumn : fieldColumns) {
            if (mapFieldColumn.getField().equalsIgnoreCase(classTable.getClassName())) {
                this.classNameLower = "_" + StringHelper.toJavaVariableName(classTable.getClassName());
                break;
            }
        }
    }

    /**
     * 封装显示属性到属性上
     *
     * @param columns
     */
    private void fields(List<MapFieldColumn> columns) {
        if (columns != null && !columns.isEmpty()) {
            if (this.fields == null) {
                this.fields = new ArrayList<>();
            }
            if (this.states == null) {
                this.states = new ArrayList<>();
            }
            for (MapFieldColumn mapFieldColumn : columns) {
                Field field = new Field();
                BeanUtils.copyProperties(mapFieldColumn, field);
                DisplayAttribute displayAttribute = mapFieldColumn.getDisplayAttribute();
                if (displayAttribute != null) {
                    if (displayAttribute.getIsRequired() != null) {
                        field.setQueryRequired(YNEnum.getYN(displayAttribute.getIsRequired()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getIsInsert() != null) {
                        field.setInsert(YNEnum.getYN(displayAttribute.getIsInsert()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getIsDeleteCondition() != null) {
                        field.setDeleteCondition(YNEnum.getYN(displayAttribute.getIsDeleteCondition()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getIsAllowUpdate() != null) {
                        field.setAllowUpdate(YNEnum.getYN(displayAttribute.getIsAllowUpdate()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getIsListPageDisplay() != null) {
                        field.setListPageDisplay(YNEnum.getYN(displayAttribute.getIsListPageDisplay()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getIsDetailPageDisplay() != null) {
                        field.setDetailPageDisplay(YNEnum.getYN(displayAttribute.getIsDetailPageDisplay()) == YNEnum.Y ? true : false);
                    }

                    if (displayAttribute.getIsLineNew() != null) {
                        field.setLineNew(YNEnum.getYN(displayAttribute.getIsLineNew()) == YNEnum.Y ? true : false);
                    }
                    if (displayAttribute.getMatchType() != null) {
                        field.setMatchType(displayAttribute.getMatchType());
                    }
                    if (displayAttribute.getDisplayType() != null) {
                        field.setDisplayType(displayAttribute.getDisplayType());
                    }
                    if (displayAttribute.getDisplayCss() != null) {
                        field.setDisplayCss(displayAttribute.getDisplayCss());
                    }
                    if (displayAttribute.getDisplayName() != null) {
                        field.setDisplayName(displayAttribute.getDisplayName());
                    }
                    if (displayAttribute.getDisplayNo() != null) {
                        field.setDisplayNo(displayAttribute.getDisplayNo());
                    }
                    if (displayAttribute.getFieldValidationMode() != null) {
                        field.setFieldValidationMode(displayAttribute.getFieldValidationMode());
                    }
                    if (displayAttribute.getValidateText() != null) {
                        field.setValidateText(displayAttribute.getValidateText());
                    }
                }
                this.fields.add(field);

                //转化状态
                if (YNEnum.Y == YNEnum.getYN(mapFieldColumn.getIsState())) {
                    if (StringUtils.isNotBlank(mapFieldColumn.getNotes())) {
                        Map<String, Object> map = StringTools.getStateOrType(mapFieldColumn.getNotes());
                        if (!map.isEmpty()) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                MapState mapState = new MapState();
                                mapState.setState(StringHelper.capitalize(entry.getKey()));
                                mapState.setValue(String.valueOf(entry.getValue()));
                                this.states.add(mapState);
                            }
                        }
                    }
                }
            }
        }

    }
}
