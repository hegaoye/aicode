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
public class Templates extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String tempalteCategoryCode;//数据库字段:tempalteCategoryCode  属性显示:模板分类编码
    private String code;//数据库字段:code  属性显示:模板编码
    private String name;//数据库字段:name  属性显示:模板名称
    private String description;//数据库字段:description  属性显示:模板说明
    private String path;//数据库字段:path  属性显示:模板路径
    private String state;//数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]
    private String fileType;//数据库字段:fileType  属性显示:文件类型 Java, Xml, Property
    private String model;//数据库字段:model  属性显示:模型：po,ctrl,vo,dao,facade,service

}

