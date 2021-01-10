/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.entity;

/**
 * 生成源码资料 的实体类的状态
 *
 * @author hegaoye
 */
public enum ProjectCodeCatalogState implements java.io.Serializable {
    ;

    public String val;

    ProjectCodeCatalogState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectCodeCatalogState getEnum(String stateName) {
        for (ProjectCodeCatalogState projectCodeCatalogState : ProjectCodeCatalogState.values()) {
            if (projectCodeCatalogState.name().equalsIgnoreCase(stateName)) {
                return projectCodeCatalogState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
