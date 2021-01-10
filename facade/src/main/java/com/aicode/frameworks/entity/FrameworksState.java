/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.entity;

/**
 * 框架技术池 的实体类的状态
 *
 * @author hegaoye
 */
public enum FrameworksState implements java.io.Serializable {
    ;

    public String val;

    FrameworksState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static FrameworksState getEnum(String stateName) {
        for (FrameworksState frameworksState : FrameworksState.values()) {
            if (frameworksState.name().equalsIgnoreCase(stateName)) {
                return frameworksState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
