/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class BuildToolsPath extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String buildCode;//数据库字段:buildCode  属性显示:构建工具编码
    private String code;//数据库字段:code  属性显示:构建路径编码
    private String buildPath;//数据库字段:buildPath  属性显示:项目构建路径
    private String pathType;//数据库字段:pathType  属性显示:路径类型：Java,Resource,Webapp


}

