package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * 数据库映射表信息
 * SELECT * FROM information_schema.SCHEMATA where SCHEMA_NAME='ai_code';
 *
 * @author lixin hegaoye@qq.com
 */
@Data
public class Database extends BaseEntity implements java.io.Serializable {
    private String catalogName;//数据库字段:catalog_name
    private String schemaName;//数据库字段:schema_name  属性显示:数据库名
    private String defaultCahracterSetName;//数据库字段:default_cahracter_set_name  属性显示:字符集
    private String defaultCollationName;//数据库字段:default_collation_name  属性显示:链接字符集

}

