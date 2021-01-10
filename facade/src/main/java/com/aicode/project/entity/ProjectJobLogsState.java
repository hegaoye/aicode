/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 任务日志 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectJobLogsState implements java.io.Serializable {
    ;

    public String val;

    ProjectJobLogsState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectJobLogsState getEnum(String stateName) {
        for (ProjectJobLogsState projectJobLogsState : ProjectJobLogsState.values()) {
            if (projectJobLogsState.name().equalsIgnoreCase(stateName)) {
                return projectJobLogsState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
