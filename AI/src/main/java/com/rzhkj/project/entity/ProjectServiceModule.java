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
public class ProjectServiceModule extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:业务编码
    private String name;//数据库字段:name  属性显示:业务名
    private String englishName;//数据库字段:englishName  属性显示:业务英文名
    private String state;//数据库字段:state  属性显示:状态停用[Disenable]，启用[Enable]


}

