/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 模块 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectModelState implements java.io.Serializable {
    Css("模块"),
    Y("是否是菜单"),
    ;

    public String val;

    ProjectModelState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectModelState getEnum(String stateName) {
        for (ProjectModelState projectModelState : ProjectModelState.values()) {
            if (projectModelState.name().equalsIgnoreCase(stateName)) {
                return projectModelState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
