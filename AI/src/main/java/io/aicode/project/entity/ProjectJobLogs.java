/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.core.base.BaseEntity;
import io.aicode.core.tools.DateTools;
import lombok.Data;

import java.util.Date;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectJobLogs extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:任务编码
    private String log;//数据库字段:log  属性显示:日志

    public ProjectJobLogs() {
    }

    public ProjectJobLogs(String code, String log) {
        this.code = code;
        this.log = "> ✔ " + DateTools.yyyyMMddHHmmssSSS(new Date()) + "&nbsp;&nbsp;" + log;
    }

    public ProjectJobLogs(String projectCode) {
        this.code = projectCode;
    }
}

