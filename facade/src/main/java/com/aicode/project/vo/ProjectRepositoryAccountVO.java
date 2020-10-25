/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 版本控制管理 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectRepositoryAccountVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:版本管理编码
     */
    @ApiModelProperty(value = "版本管理编码")
    private java.lang.String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 数据库字段:account  属性显示:帐户名
     */
    @ApiModelProperty(value = "帐户名")
    private java.lang.String account;
    /**
     * 数据库字段:password  属性显示:密码
     */
    @ApiModelProperty(value = "密码")
    private java.lang.String password;
    /**
     * 数据库字段:home  属性显示:仓库地址
     */
    @ApiModelProperty(value = "仓库地址")
    private java.lang.String home;
    /**
     * 数据库字段:description  属性显示:仓库说明
     */
    @ApiModelProperty(value = "仓库说明")
    private java.lang.String description;
    /**
     * 数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;
    /**
     * 数据库字段:type  属性显示:仓库类型:Git, Svn
     */
    @ApiModelProperty(value = "仓库类型:Git, Svn")
    private java.lang.String type;

}
