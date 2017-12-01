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
public class ProjectFiles extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:源码文件编码
    private String templateCode;//数据库字段:templateCode  属性显示:模板编码
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String path;//数据库字段:path  属性显示:源文件路径
    private String description;//数据库字段:description  属性显示:源文件说明
    private java.util.Date createTime;//数据库字段:createTime  属性显示:创建时间

}

