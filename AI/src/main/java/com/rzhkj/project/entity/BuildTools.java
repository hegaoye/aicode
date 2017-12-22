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
public class BuildTools extends BaseEntity implements java.io.Serializable{
	private static final long serialVersionUID = 5454155825314635342L;

	private Long id;//数据库字段:id  属性显示:id
	private String code;//数据库字段:code  属性显示:构建工具编码
	private String buildType;//数据库字段:buildType  属性显示:管理类型:Gradle,Maven
}

