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
public class TableRelationship extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:关系编码
    private String tableCode;//数据库字段:tableCode  属性显示:表编码
    private String relationship;//数据库字段:relationship  属性显示:关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany

}

