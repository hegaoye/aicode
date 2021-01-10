/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 项目sql脚本 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectSqlState implements java.io.Serializable {
    Enable("启用"),
    Disenable("停用"),
    Delete("删除");

    public String val;

    ProjectSqlState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectSqlState getEnum(String stateName) {
        for (ProjectSqlState projectSqlState : ProjectSqlState.values()) {
            if (projectSqlState.name().equalsIgnoreCase(stateName)) {
                return projectSqlState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
