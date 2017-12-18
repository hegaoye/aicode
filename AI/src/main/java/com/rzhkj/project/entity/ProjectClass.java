/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectClass extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String classCode;//数据库字段:classCode  属性显示:类编码

    private ClassInfo classInfo;

    public ProjectClass() {
    }

    public ProjectClass(String projectCode, String classCode) {
        this.projectCode = projectCode;
        this.classCode = classCode;
    }
}

