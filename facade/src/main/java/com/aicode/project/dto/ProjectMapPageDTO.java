/*
 * aicode
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目数据表 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectMapPageDTO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:projectCode 项目编码")
    private java.lang.String projectCode;
    @Schema(description = "数据库字段:mapClassTableCode 字段属性映射编码")
    private java.lang.String mapClassTableCode;
}
