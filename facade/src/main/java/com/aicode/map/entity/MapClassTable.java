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
        //表名含下划线取首段作为模块名；与 generator() 的 model 分组保持一致
        this.classModel = this.tableName != null && this.tableName.contains("_")
                ? this.tableName.substring(0, this.tableName.indexOf("_"))
                : this.tableName;
    }


    public String getClassModel() {
        // 自愈：DB 脏数据场景（classModel 列 NULL 或直接 SQL 写入的记录）
        // 按 tableName 懒计算并缓存（与 toJava() 同样的推导规则）。
        if (this.classModel == null && this.tableName != null) {
            this.classModel = this.tableName.contains("_")
                    ? this.tableName.substring(0, this.tableName.indexOf("_"))
                    : this.tableName;
        }
        return this.classModel;
    }

    public String getDashedCaseName() {
        return StringTools.humpToLine(this.className);
    }
}
