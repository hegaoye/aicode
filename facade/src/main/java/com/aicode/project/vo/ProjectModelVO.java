/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "")
    private Long id;
    /**
     * 数据库字段:code  属性显示:模块编码
     */
    @Schema(description = "模块编码")
    private String code;
    /**
     * 数据库字段:pre_code  属性显示:上级模块编码
     */
    @Schema(description = "上级模块编码")
    private String preCode;
    /**
     * 数据库字段:name  属性显示:模块显示名称
     */
    @Schema(description = "模块显示名称")
    private String name;
    /**
     * 数据库字段:route  属性显示:模块路由
     */
    @Schema(description = "模块路由")
    private String route;
    /**
     * 数据库字段:css  属性显示:模块css样式
     */
    @Schema(description = "模块css样式")
    private String css;
    /**
     * 数据库字段:is_menu  属性显示:是否是菜单 Y,N
     */
    @Schema(description = "是否是菜单 Y,N")
    private String isMenu;
    /**
     * 数据库字段:ico  属性显示:模块图标
     */
    @Schema(description = "模块图标")
    private String ico;

}
