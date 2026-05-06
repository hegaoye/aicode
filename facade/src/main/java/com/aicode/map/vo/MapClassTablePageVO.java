/*
 * aicode
 */
package com.aicode.map.vo;

import com.aicode.core.BaseVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类表映射信息 分页 对象 VO
 *
 * @author aicode
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MapClassTablePageVO extends BaseVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:code 映射编码")
    private String code;
    @Schema(description = "数据库字段:tableName 表名")
    private String tableName;
    @Schema(description = "数据库字段:className 类名")
    private String className;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;
}
