/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.annotations.ApiModelProperty;
import com.aicode.core.base.BaseVO;
import lombok.Data;

/**
 * 模块下的类 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectModelClassPageVO extends BaseVO implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:mapClassTableCode  属性显示:类编码
     */
    @ApiModelProperty(value = "类编码")
    private java.lang.String mapClassTableCode;
    /**
     * 数据库字段:projectModelCode  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    private java.lang.String projectModelCode;

}
