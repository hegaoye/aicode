/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.entity;

import io.aicode.core.base.BaseEntity;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectSql extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;
    private Long id;//数据库字段:id  属性显示:id
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String code;//数据库字段:code  属性显示:tsql编码
    private String tsql;//数据库字段:tsql  属性显示:sql脚本
    private String state;//数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]

    public ProjectSql() {
    }

    public ProjectSql(String projectCode) {
        this.projectCode = projectCode;
    }
}

