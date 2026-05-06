/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.core.tools.HandleFuncs;
import com.aicode.map.entity.MapClassTable;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 生成源码资料 的实体类
 *
 * @author hegaoye
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectCodeCatalog implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:
     */
    @Schema(description = "")
    private Long id;
    /**
     * 数据库字段:code  属性显示:编码
     */
    @Schema(description = "编码")
    private String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    @TableField("projectCode")
    private String projectCode;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @Schema(description = "项目基础包名")
    @TableField("basePackage")
    private String basePackage;
    /**
     * 数据库字段:fileName  属性显示:文件名
     */
    @Schema(description = "文件名")
    @TableField("fileName")
    private String fileName;
    /**
     * 数据库字段:fileSuffix  属性显示:文件后缀
     */
    @Schema(description = "文件后缀")
    @TableField("fileSuffix")
    private String fileSuffix;
    /**
     * 数据库字段:relativePath  属性显示:相对路径
     */
    @Schema(description = "相对路径")
    @TableField("relativePath")
    private String relativePath;
    /**
     * 数据库字段:absolutePath  属性显示:绝对路径
     */
    @Schema(description = "绝对路径")
    @TableField("absolutePath")
    private String absolutePath;


    @TableField(exist = false)
    private MapClassTable mapClassTable;

    public String basePackage(String workspace) {
        return (new HandleFuncs().getCurrentClassPath() + "/"
                + workspace + "/"
                + absolutePath).replace("//", "/");
    }
}
