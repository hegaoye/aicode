/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 项目信息 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectState implements java.io.Serializable {
    ;

    public String val;

    ProjectState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectState getEnum(String stateName) {
        for (ProjectState projectState : ProjectState.values()) {
            if (projectState.name().equalsIgnoreCase(stateName)) {
                return projectState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
