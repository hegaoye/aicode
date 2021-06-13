/*
 * Powered By [lixin]
 *
 */

package com.aicode.database.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;


/**
 * 数据库映射表信息
 * select COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,COLUMN_KEY,EXTRA,COLUMN_COMMENT
 * from information_schema.columns
 * where table_schema='ai-code' and table_name='project';
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class Column implements java.io.Serializable {

    /**
     * 数据库字段:COLUMN_NAME  属性显示:字段名
     */
    @TableField("columnName")
    private String columnName;

    /**
     * 数据库字段:COLUMN_DEFAULT  属性显示:字段默认值
     */
    @TableField("columnDefault")
    private String columnDefault;

    /**
     * 数据库字段:IS_NULLABLE  属性显示:表类型 NO,YES
     */
    @TableField("isNullable")
    private String isNullable;
    /**
     * 数据库字段:TYPE_NAME  属性显示:字段类型
     */
    @TableField("typeName")
    private String typeName;

    /**
     * 数据库字段:remarks  属性显示:字段注释
     */
    @TableField("remarks")
    private String remarks;

}

