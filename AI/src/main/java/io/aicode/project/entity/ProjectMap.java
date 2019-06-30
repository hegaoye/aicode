/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectMap extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String mapClassTableCode;//数据库字段:mapClassTableCode  属性显示:字段属性映射编码

    private MapClassTable mapClassTable;

    public ProjectMap() {
    }

    public ProjectMap(String projectCode, String mapClassTableCode) {
        this.projectCode = projectCode;
        this.mapClassTableCode = mapClassTableCode;
    }
}

