/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 模块文件信息 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModuleFile implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @ApiModelProperty(value = "id")
    private java.lang.Long id;
    /**
     * 数据库字段:moudleCode  属性显示:模块编码
     */
    @ApiModelProperty(value = "模块编码")
    @TableField("moudleCode")
    private java.lang.String moudleCode;
    /**
     * 数据库字段:path  属性显示:文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private java.lang.String path;

}
