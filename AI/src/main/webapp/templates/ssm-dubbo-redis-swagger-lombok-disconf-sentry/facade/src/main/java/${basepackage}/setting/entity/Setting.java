package ${basePackage}.setting.entity;

import ${basePackage}.core.base.BaseEntity;
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
    private String summary;//数据库字段:summary  属性显示:说明

    //设置键枚举
    public enum Key {
        ;

    }

}

