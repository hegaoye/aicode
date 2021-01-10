/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目选择模块 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectModuleVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 数据库字段:moduleCode  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String moduleCode;

}
