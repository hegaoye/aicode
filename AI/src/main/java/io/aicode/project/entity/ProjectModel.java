/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import lombok.Data;

/**
 * 模块
 *
 * @author lixin
 */
@Data
public class ProjectModel extends BaseEntity implements java.io.Serializable {

    /**
     * 数据库字段:code  属性显示:模块编码
     */
    private String code;
    /**
     * 数据库字段:pre_code  属性显示:上级模块编码
     */
    private String preCode;
    /**
     * 数据库字段:name  属性显示:模块显示名称
     */
    private String name;
    /**
     * 数据库字段:route  属性显示:模块路由
     */
    private String route;
    /**
     * 数据库字段:css  属性显示:模块css样式
     */
    private String css;
    /**
     * 数据库字段:is_menu  属性显示:是否是菜单 Y,N
     */
    private String isMenu;
    /**
     * 数据库字段:ico  属性显示:模块图标
     */
    private String ico;
}
