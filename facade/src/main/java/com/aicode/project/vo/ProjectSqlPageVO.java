/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import com.aicode.core.base.BaseVO;
import lombok.Data;

/**
 * 项目sql脚本 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectSqlPageVO extends BaseVO implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    private java.lang.String projectCode;
    /**
     * 数据库字段:code  属性显示:tsql编码
     */
    @ApiModelProperty(value = "tsql编码")
    private java.lang.String code;
    /**
     * 数据库字段:tsql  属性显示:sql脚本
     */
    @ApiModelProperty(value = "sql脚本")
    private java.lang.String tsql;
    /**
     * 数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]
     */
    @ApiModelProperty(value = "状态：停用[Disenable]，启用[Enable]")
    private java.lang.String state;

}
