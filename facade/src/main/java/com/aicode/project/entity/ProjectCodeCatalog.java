/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

import com.aicode.core.tools.HandleFuncs;
import com.aicode.map.entity.MapClassTable;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "")
    private java.lang.Long id;
    /**
     * 数据库字段:code  属性显示:编码
     */
    @ApiModelProperty(value = "编码")
    private java.lang.String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @ApiModelProperty(value = "项目编码")
    @TableField("projectCode")
    private java.lang.String projectCode;
    /**
     * 数据库字段:basePackage  属性显示:项目基础包名
     */
    @ApiModelProperty(value = "项目基础包名")
    @TableField("basePackage")
    private java.lang.String basePackage;
    /**
     * 数据库字段:fileName  属性显示:文件名
     */
    @ApiModelProperty(value = "文件名")
    @TableField("fileName")
    private java.lang.String fileName;
    /**
     * 数据库字段:fileSuffix  属性显示:文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    @TableField("fileSuffix")
    private java.lang.String fileSuffix;
    /**
     * 数据库字段:relativePath  属性显示:相对路径
     */
    @ApiModelProperty(value = "相对路径")
    @TableField("relativePath")
    private java.lang.String relativePath;
    /**
     * 数据库字段:absolutePath  属性显示:绝对路径
     */
    @ApiModelProperty(value = "绝对路径")
    @TableField("absolutePath")
    private java.lang.String absolutePath;


    @TableField(exist = false)
    private MapClassTable mapClassTable;

    public String basePackage(String workspace) {
        return (new HandleFuncs().getCurrentClassPath() + "/"
                + workspace + "/"
                + absolutePath).replace("//", "/");
    }
}
