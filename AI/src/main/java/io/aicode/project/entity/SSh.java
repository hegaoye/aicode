/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.aicode.core.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class SSh extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private String code;//数据库字段:code  属性显示:账户编码
    private String host;//数据库字段:host  属性显示:主机
    private int port;//数据库字段:port  属性显示:端口
    private String user;//数据库字段:user  属性显示:用户名
    @JSONField(serialize = false)
    private String password;//数据库字段:password  属性显示:密码

    public SSh() {
    }

    public SSh(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }
}

