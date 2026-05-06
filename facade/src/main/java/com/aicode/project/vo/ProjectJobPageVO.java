/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;
    /**
     * 数据库字段:code  属性显示:任务编码
     */
    @Schema(description = "任务编码")
    private String code;
    /**
     * 数据库字段:number  属性显示:第多少次执行
     */
    @Schema(description = "第多少次执行")
    private int number;
    /**
     * 数据库字段:state  属性显示:任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}
     */
    @Schema(description = "任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}")
    private String state;
    /**
     * 数据库字段:createTime  属性显示:执行任务时间
     */
    @Schema(description = "执行任务时间")
    private java.util.Date createTime;

}
