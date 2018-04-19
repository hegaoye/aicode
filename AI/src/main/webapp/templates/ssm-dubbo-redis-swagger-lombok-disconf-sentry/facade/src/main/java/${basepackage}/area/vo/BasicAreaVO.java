package ${basePackage}.area.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BasicAreaVO implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;


    /**
     * 行政区域代码
     */
    private String areaCode;
    /**
     * 行政区域名称
     */
    private String areaName;
    /**
     * 区域全名称
     */
    private String fullName;
    /**
     * 省级代码
     */
    private String province;
    /**
     * 市级代码
     */
    private String city;
    /**
     * 行政区域级别 (0 中国, 1 省、2 市、3 县区)
     */
    private Boolean level;
    /**
     * 是否是新规划的行政区域代码 (0否， 1是)
     */
    private Boolean isNew;
    /**
     * 新的行政区域代码，如果is_new=0时，对应area_code
     */
    private String newAreaCode;

}

