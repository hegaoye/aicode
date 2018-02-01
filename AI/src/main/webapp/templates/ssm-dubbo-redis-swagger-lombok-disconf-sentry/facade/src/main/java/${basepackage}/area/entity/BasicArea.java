
package ${basePackage}.area.entity;

import ${basePackage}.core.base.BaseEntity;
import lombok.Data;

import java.util.List;
@Data
public class BasicArea extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private String areaCode;//数据库字段:area_code  属性显示:行政区域代码
    private String areaName;//数据库字段:area_name  属性显示:行政区域名称
    private String fullName;//数据库字段:full_name  属性显示:区域全名称
    private String province;//数据库字段:province  属性显示:省级代码
    private String city;//数据库字段:city  属性显示:市级代码
    private int level;//数据库字段:level  属性显示:行政区域级别 (0 中国, 1 省、2 市、3 县区)
    private Boolean isNew;//数据库字段:is_new  属性显示:是否是新规划的行政区域代码 (0否， 1是)
    private String newAreaCode;//数据库字段:new_area_code  属性显示:新的行政区域代码，如果is_new=0时，对应area_code

    private List<BasicArea> children;
}

