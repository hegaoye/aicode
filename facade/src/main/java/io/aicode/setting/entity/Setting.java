/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */


package io.aicode.setting.entity;

import io.aicode.core.base.BaseEntity;
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
        Repository_Path,//zip仓库路径
        GitHome_Default,//默认系统仓库路径
        SandBox_Path,//沙箱环境目录
        ;

    }

}

