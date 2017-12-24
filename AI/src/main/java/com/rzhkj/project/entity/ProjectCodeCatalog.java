/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;


import com.rzhkj.core.base.BaseEntity;
import com.rzhkj.core.tools.HandleFuncs;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class ProjectCodeCatalog extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:目录编码
    private String projectCode;//数据库字段:projectCode  属性显示:项目编码
    private String moduleCode;//数据库字段:moduleCode  属性显示:项目模块编码
    private String serviceModuleCode;//数据库字段:serviceModuleCode  属性显示:业务编码
    private String codeModelCode;//数据库字段:codeModelCode  属性显示:源码结构编码
    private String classInfoCode;//数据库字段:classInfoCode  属性显示:类编码
    private String frameworksConfigureTemplateCode;//数据库字段:frameworksConfigureTemplateCode  属性显示:框架模板编码
    private String relativePath;//数据库字段:relativePath  属性显示:相对路径
    private String absolutePath;//数据库字段:absolutePath  属性显示:绝对路径
    private String fileName;//数据库字段:fileName  属性显示:文件名
    private String fileSuffix;//数据库字段:fileSuffix  属性显示:文件后缀
    private String fileType;//数据库字段:fileType  属性显示:文件类型：java,xml,property
    private String classPackage;//数据库字段:classPackage  属性显示:类包名
    private String basePackage;//数据库字段:basePackage  属性显示:项目基础包名

    private ClassInfo classInfo;
    private ProjectCodeModel projectCodeModel;

    public ProjectCodeCatalog() {
    }

    public ProjectCodeCatalog(String code, String projectCode, String moduleCode, String serviceModuleCode, String codeModelCode, String classInfoCode, String fileName) {
        this.code = code;
        this.projectCode = projectCode;
        this.moduleCode = moduleCode;
        this.serviceModuleCode = serviceModuleCode;
        this.codeModelCode = codeModelCode;
        this.classInfoCode = classInfoCode;
        this.fileName = fileName;
    }

    public String basePackage(String workspace) {
        return (new HandleFuncs().getCurrentClassPath() + "/"
                + workspace + "/"
                + absolutePath).replace("//", "/");
    }
}

