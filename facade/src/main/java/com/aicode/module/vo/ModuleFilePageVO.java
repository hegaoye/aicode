/*
 * aicode
 */
package com.aicode.module.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模块文件信息 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModuleFilePageVO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:moudleCode 模块编码")
    private String moudleCode;
    @Schema(description = "数据库字段:path 文件路径")
    private String path;
}
