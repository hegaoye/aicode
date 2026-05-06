/*
 * demo
 */
package com.aicode.map.entity;

import com.aicode.core.tools.StringTools;
import com.aicode.core.tools.core.StringHelper;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 类表映射信息 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MapClassTable implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 映射编码")
    private String code;
    @Schema(description = "数据库字段:tableName 表名")
    @TableField("tableName")
    private String tableName;
    @Schema(description = "数据库字段:className 类名")
    @TableField("className")
    private String className;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;


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
