/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */


package com.rzhkj.setting.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * 设置对象
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class Setting extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long id;//数据库字段:id  属性显示:id
    private String k;//数据库字段:k  属性显示:键
    private String v;//数据库字段:v  属性显示:值
    private String description;//数据库字段:description  属性显示:说明

    //设置键枚举
    public enum Key {
        DefaultDatabase,//默认数据库
        Gradle_Directory_Structure,//gradle目录结构
        Workspace,//工作目录
        Package_entity,//实体目录命名
        Template_Path,//模板默认路径
        ;

    }

}

