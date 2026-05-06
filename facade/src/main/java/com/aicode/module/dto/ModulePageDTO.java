/*
 * aicode
 */
package com.aicode.module.dto;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方模块池 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModulePageDTO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 模块编码")
    private String code;
    @Schema(description = "数据库字段:name 模块名")
    private String name;
    @Schema(description = "数据库字段:description 模块说明")
    private String description;
}
