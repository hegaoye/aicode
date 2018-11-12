/*
 * Powered By [lixin]
 *
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
public class ProjectFramwork extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String frameworkCode;//数据库字段:frameworkCode  属性显示:技术编码
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码

    private Frameworks frameworks;

    public ProjectFramwork() {
    }

    public ProjectFramwork(String projectCode) {
        this.projectCode = projectCode;
    }
}

