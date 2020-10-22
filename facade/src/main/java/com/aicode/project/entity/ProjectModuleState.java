/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 项目选择模块 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectModuleState implements java.io.Serializable {
    ;

    public String val;

    ProjectModuleState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectModuleState getEnum(String stateName) {
        for (ProjectModuleState projectModuleState : ProjectModuleState.values()) {
            if (projectModuleState.name().equalsIgnoreCase(stateName)) {
                return projectModuleState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
