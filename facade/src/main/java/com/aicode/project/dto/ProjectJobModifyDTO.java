/**
 * aicode
 */
package com.aicode.project.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改 任务 VO
 *
 * @author aicode
 */
@Data
public class ProjectJobModifyDTO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:projectCode 项目编码")
    private java.lang.String projectCode;
    @Schema(description = "数据库字段:code 任务编码")
    private java.lang.String code;
    @Schema(description = "数据库字段:number 第多少次执行")
    private java.lang.String number;
    @Schema(description = "数据库字段:state 任务状态: {创建:Create} , {执行中:Executing}, {完成:Completed} ,{失败:Error}, {警告:Waring}")
    private java.lang.String state;
    @Schema(description = "数据库字段:createTime 执行任务时间")
    private java.util.Date createTime;
}
