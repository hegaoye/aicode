package com.aicode.database.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 数据库映射表信息
 * select table_name,table_comment
 * from information_schema.tables
 * where table_schema='ai-code' and table_type='base table';
 *
 * @author lixin hegaoye@qq.com
 */
@Data
public class Table implements java.io.Serializable {

    @TableField("tableName")
    private String tableName;//数据库字段:table_name  属性显示:表名
    @TableField("tableComment")
    private String tableComment;//数据库字段:table_comment  属性显示:数据库注释

}

