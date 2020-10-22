/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模块 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectModelVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String code;
    /**
     * 数据库字段:pre_code  属性显示:上级模块编码
     */
    @ApiModelProperty(value = "上级模块编码")
    private java.lang.String preCode;
    /**
     * 数据库字段:name  属性显示:模块显示名称
     */
    @ApiModelProperty(value = "模块显示名称")
    private java.lang.String name;
    /**
     * 数据库字段:route  属性显示:模块路由
     */
    @ApiModelProperty(value = "模块路由")
    private java.lang.String route;
    /**
     * 数据库字段:css  属性显示:模块css样式
     */
    @ApiModelProperty(value = "模块css样式")
    private java.lang.String css;
    /**
     * 数据库字段:is_menu  属性显示:是否是菜单 Y,N
     */
    @ApiModelProperty(value = "是否是菜单 Y,N")
    private java.lang.String isMenu;
    /**
     * 数据库字段:ico  属性显示:模块图标
     */
    @ApiModelProperty(value = "模块图标")
    private java.lang.String ico;

}
