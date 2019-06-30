/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import io.aicode.base.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */

@Data
public class Project extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;


    private Long id;//数据库字段:id  属性显示:id
    private String code;//数据库字段:code  属性显示:项目编码
    private String name;//数据库字段:name  属性显示:项目名
    private String description;//数据库字段:description  属性显示:项目描述
    private String englishName;//数据库字段:englishName  属性显示:项目英文名
    private String databaseType;//数据库字段:databaseType  属性显示:数据库类型:Mysql,Oracle
    private String language;//数据库字段:language  属性显示:项目语言:Java,Python,Js
    private String state;//数据库字段:state  属性显示:项目状态：停用[Disenable]，启用[Enable]，删除[Delete]
    private String copyright;//数据库字段:copyright  属性显示:项目版权文字信息
    private String author;//数据库字段:author  属性显示:作者
    private String phone;//数据库字段:phone  属性显示:联系方式
    private String basePackage;//数据库字段:basePackage  属性显示:项目基础包名
    private String sqlFile;//数据库字段:sql  属性显示:数据库脚本文件地址
    private String downloadUrl;//数据库字段:downloadUrl  属性显示:项目下载地址
    private String isRepository;//数据库字段:isRepository  属性显示:是否仓库管理
    private String isParseTable;//数据库字段:isParseTable  属性显示:是否已经解析表
    private String isParseClass;//数据库字段:isParseClass  属性显示:是否已经解析类
    private String isIncrement;//数据库字段:isIncrement  属性显示:是否增量生成
    private Integer buildNumber;//数据库字段:buildNumber  属性显示:生成次数
    private String accountCode;//数据库字段:accountCode  属性显示:账户编码
    private java.util.Date createTime;//数据库字段:createTime  属性显示:创建时间
    private java.util.Date updateTime;//数据库字段:updateTime  属性显示:更新时间

    private List<ProjectFramwork> projectFramworkList;
    private List<ProjectMap> projectMapList;
    private List<ProjectJob> projectJobList;
    private List<ProjectModule> projectModuleList;
    private List<ProjectRepositoryAccount> projectRepositoryAccountList;
    private List<ProjectSql> projectSqlList;
    private String relationshipAndDisplay;//表关系或字段显示属性，只要有一个设置是Y，两个都没设置是N
}

