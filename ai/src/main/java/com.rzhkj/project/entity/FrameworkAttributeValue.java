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
public class FrameworkAttributeValue extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String frameworkCode;//数据库字段:frameworkCode  属性显示:技术编码
    private String code;//数据库字段:code  属性显示:属性值编码
    private String attribute;//数据库字段:attribute  属性显示:属性名
    private String attributeValue;//数据库字段:attributeValue  属性显示:属性值

}

