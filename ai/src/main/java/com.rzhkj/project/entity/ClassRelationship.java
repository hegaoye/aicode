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
public class ClassRelationship extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:关系编码
    private String classCode;//数据库字段:classCode  属性显示:类编码
    private String classRelationshipCode;//数据库字段:classRelationshipCode  属性显示:关系类编码
    private String relationship;//数据库字段:relationship  属性显示:关系为：1对1  OneOnOne，1对多 OneOnMany，多对多 ManyOnMany


}

