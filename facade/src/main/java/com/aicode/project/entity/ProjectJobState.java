/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 任务 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectJobState implements java.io.Serializable {
    Create("创建"),
    Executing("执行中"),
    Completed("完成"),
    Error("失败"),
    Waring("警告");

    public String val;

    ProjectJobState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectJobState getEnum(String stateName) {
        for (ProjectJobState projectJobState : ProjectJobState.values()) {
            if (projectJobState.name().equalsIgnoreCase(stateName)) {
                return projectJobState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
