/*
 * aicode
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目应用技术 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectFramworkPageDTO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private java.lang.Long id;
    @Schema(description = "数据库字段:frameworkCode 技术编码")
    private java.lang.String frameworkCode;
    @Schema(description = "数据库字段:projectCode 项目编码")
    private java.lang.String projectCode;
}
