/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;


import io.aicode.core.base.BaseEntity;
import lombok.Data;

/**
 * 框架配置文件模板
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class FrameworksTemplate extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:模板编码
    private String frameworkCode;//数据库字段:frameworkCode  属性显示:技术编码
    private String path;//数据库字段:path  属性显示:模板路径
}

