/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 项目应用技术 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectFramworkState implements java.io.Serializable {
    ;

    public String val;

    ProjectFramworkState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectFramworkState getEnum(String stateName) {
        for (ProjectFramworkState projectFramworkState : ProjectFramworkState.values()) {
            if (projectFramworkState.name().equalsIgnoreCase(stateName)) {
                return projectFramworkState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
