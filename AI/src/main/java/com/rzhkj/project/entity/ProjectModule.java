/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectModule extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String code;//数据库字段:code  属性显示:模块编码
    private String name;//数据库字段:name  属性显示:模块名称
    private String englishName;//数据库字段:englishName  属性显示:模块英文名
    private String state;//数据库字段:state  属性显示:模块状态 停用[Disenable]，启用[Enable]

    private List<ProjectServiceModule> projectServiceModuleList;

}

