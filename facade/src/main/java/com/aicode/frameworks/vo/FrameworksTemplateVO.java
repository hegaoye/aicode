/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 框架配置文件模板 VO
 *
 * @author hegaoye
 */
@Data
public class FrameworksTemplateVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:模板编码
     */
    @ApiModelProperty(value = "模板编码")
    private java.lang.String code;
    /**
     * 数据库字段:frameworkCode  属性显示:框架编码
     */
    @ApiModelProperty(value = "框架编码")
    private java.lang.String frameworkCode;
    /**
     * 数据库字段:path  属性显示:模板路径
     */
    @ApiModelProperty(value = "模板路径")
    private java.lang.String path;

}
