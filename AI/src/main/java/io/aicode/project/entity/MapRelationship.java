/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import io.aicode.base.enums.YNEnum;
import lombok.Data;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class MapRelationship extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:关系编码
    private String mapClassTableCode;//数据库字段:tableCode  属性显示:表编码
    private String associateCode;//数据库字段:associateCode  属性显示:被关联编码
    private String isOneToOne;//数据库字段:isOneToOne  属性显示:是否一对一 Y N
    private String isOneToMany;//数据库字段:isOneToMany  属性显示:是否一对多Y N
    private String mainField;//数据库字段:mainField  属性显示:主表关联属性
    private String joinField;//数据库字段:joinField  属性显示:从表关联属性

    private MapClassTable associateClass;//被关联对象被查询

    private boolean oneToOne;
    private boolean oneToMany;

    public boolean getOneToOne() {
        return isOneToOne.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getOneToMany() {
        return isOneToMany.equals(YNEnum.Y.name()) ? true : false;
    }

}

