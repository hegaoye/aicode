/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.entity;

/**
 * 设置 的实体类的状态
 *
 * @author hegaoye
 */
public enum SettingKey implements java.io.Serializable {
    DefaultDatabase,//默认数据库
    Gradle_Directory_Structure,//gradle目录结构
    Workspace,//工作目录
    Package_entity,//实体目录命名
    Template_Path,//模板默认路径
    Repository_Path,//zip仓库路径
    GitHome_Default,//默认系统仓库路径
    SandBox_Path,//沙箱环境目录
    ;

    @Override
    public String toString() {
        return this.name();
    }

}
