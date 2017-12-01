/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectJob extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:任务编码
    private String name;//数据库字段:name  属性显示:任务名
    private String description;//数据库字段:description  属性显示:任务描述
    private Integer number;//数据库字段:number  属性显示:任务执行次数
    private String state;//数据库字段:state  属性显示:任务状态: 创建[Create] , 执行中[Executing], 完成[C ompleted] ,失败[Error] 警告 [Waring]
}

