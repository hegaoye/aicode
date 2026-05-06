/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import com.aicode.core.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本控制管理 分页 对象 VO
 *
 * @author hegaoye
 */
@Data
public class ProjectRepositoryAccountPageVO extends BaseVO implements java.io.Serializable {
    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:code  属性显示:版本管理编码
     */
    @Schema(description = "版本管理编码")
    private String code;
    /**
     * 数据库字段:projectCode  属性显示:项目编码
     */
    @Schema(description = "项目编码")
    private String projectCode;
    /**
     * 数据库字段:account  属性显示:帐户名
     */
    @Schema(description = "帐户名")
    private String account;
    /**
     * 数据库字段:password  属性显示:密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 数据库字段:home  属性显示:仓库地址
     */
    @Schema(description = "仓库地址")
    private String home;
    /**
     * 数据库字段:description  属性显示:仓库说明
     */
    @Schema(description = "仓库说明")
    private String description;
    /**
     * 数据库字段:state  属性显示:状态：停用[Disenable]，启用[Enable]
     */
    @Schema(description = "状态：停用[Disenable]，启用[Enable]")
    private String state;
    /**
     * 数据库字段:type  属性显示:仓库类型:Git, Svn
     */
    @Schema(description = "仓库类型:Git, Svn")
    private String type;

}
