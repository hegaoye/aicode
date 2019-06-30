/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ModuleFile extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String moudleCode;//数据库字段:moudleCode  属性显示:模块编码
    private String path;//数据库字段:path  属性显示:文件路径

}

