/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

import com.aicode.core.tools.StringTools;
import com.aicode.core.tools.core.StringHelper;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 类表映射信息 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapClassTable implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:映射编码
     */
    @ApiModelProperty(value = "映射编码")
    private java.lang.String code;
    /**
     * 数据库字段:tableName  属性显示:表名
     */
    @ApiModelProperty(value = "表名")
    private java.lang.String tableName;
    /**
     * 数据库字段:className  属性显示:类名
     */
    @ApiModelProperty(value = "类名")
    private java.lang.String className;
    /**
     * 数据库字段:notes  属性显示:注释
     */
    @ApiModelProperty(value = "注释")
    private java.lang.String notes;

    @TableField(exist = false)
    private String classModel;//类所在模块
    @TableField(exist = false)
    private String dashedCaseName;//破折号命名 或叫烤串命名 适用于 前端angular ,react, vue

    @TableField(exist = false)
    private List<MapFieldColumn> mapFieldColumnList;
    @TableField(exist = false)
    private List<MapRelationship> mapRelationshipList;//类模型的关联关系

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
