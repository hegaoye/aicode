/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 模块下的类 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectModelClassState implements java.io.Serializable {
    ;

    public String val;

    ProjectModelClassState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectModelClassState getEnum(String stateName) {
        for (ProjectModelClassState projectModelClassState : ProjectModelClassState.values()) {
            if (projectModelClassState.name().equalsIgnoreCase(stateName)) {
                return projectModelClassState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
