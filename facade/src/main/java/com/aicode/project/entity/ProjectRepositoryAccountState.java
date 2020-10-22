/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 版本控制管理 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectRepositoryAccountState implements java.io.Serializable {
    ;

    public String val;

    ProjectRepositoryAccountState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectRepositoryAccountState getEnum(String stateName) {
        for (ProjectRepositoryAccountState projectRepositoryAccountState : ProjectRepositoryAccountState.values()) {
            if (projectRepositoryAccountState.name().equalsIgnoreCase(stateName)) {
                return projectRepositoryAccountState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
