/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import com.aicode.core.base.BaseVO;
import lombok.Data;

/**
 * 任务 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectJobPageVO extends BaseVO implements java.io.Serializable {
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
     * 数据库字段:code  属性显示:任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private java.lang.String code;
    /**
     * 数据库字段:number  属性显示:第多少次执行
     */
    @ApiModelProperty(value = "第多少次执行")
    private int number;
    /**
     * 数据库字段:state  属性显示:任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}
     */
    @ApiModelProperty(value = "任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}")
    private java.lang.String state;
    /**
     * 数据库字段:createTime  属性显示:执行任务时间
     */
    @ApiModelProperty(value = "执行任务时间")
    private java.util.Date createTime;

}
