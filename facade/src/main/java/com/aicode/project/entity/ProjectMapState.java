/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 项目数据表 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectMapState implements java.io.Serializable {
    ;

    public String val;

    ProjectMapState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectMapState getEnum(String stateName) {
        for (ProjectMapState projectMapState : ProjectMapState.values()) {
            if (projectMapState.name().equalsIgnoreCase(stateName)) {
                return projectMapState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
