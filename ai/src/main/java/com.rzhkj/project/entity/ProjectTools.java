/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
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
public class ProjectTools extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String toolsCode;//数据库字段:toolsCode  属性显示:工具编码


}

