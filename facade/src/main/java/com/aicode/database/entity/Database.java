package com.aicode.database.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 数据库映射表信息
 * SELECT * FROM information_schema.SCHEMATA where SCHEMA_NAME='ai_code';
 *
 * @author lixin hegaoye@qq.com
 */
@Data
public class Database implements java.io.Serializable {
    @TableField("catalogName")
    private String catalogName;//数据库字段:catalog_name
    @TableField("schemaName")
    private String schemaName;//数据库字段:schema_name  属性显示:数据库名
    @TableField("defaultCahracterSetName")
    private String defaultCahracterSetName;//数据库字段:default_cahracter_set_name  属性显示:字符集
    @TableField("defaultCollationName")
    private String defaultCollationName;//数据库字段:default_collation_name  属性显示:链接字符集

}

