/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import lombok.Data;

/**
 * 模块下的类
 *
 * @author lixin
 */
@Data
public class ProjectModelClass extends BaseEntity implements java.io.Serializable {

    /**
     * 数据库字段:mapClassTableCode  属性显示:类编码
     */
    private String mapClassTableCode;
    /**
     * 数据库字段:projectModelCode  属性显示:模块编码
     */
    private String projectModelCode;
}
