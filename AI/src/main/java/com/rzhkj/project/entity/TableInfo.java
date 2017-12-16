/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class TableInfo extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:表编码
    private String name;//数据库字段:name  属性显示:表名
    private String notes;//数据库字段:notes  属性显示:表注释
    private Integer columnNumber;//数据库字段:columnNumber  属性显示:表字段数


    private List<ColumnInfo> columnInfos;//one to many

    public TableInfo() {
    }

    public TableInfo(String code, String name, String notes, Integer columnNumber) {
        this.code = code;
        this.name = name;
        this.notes = notes;
        this.columnNumber = columnNumber;
    }
}

