/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.entity;

/**
 * 框架配置文件模板 的实体类的状态
 *
 * @author hegaoye
 */
public enum FrameworksTemplateState implements java.io.Serializable {
    ;

    public String val;

    FrameworksTemplateState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static FrameworksTemplateState getEnum(String stateName) {
        for (FrameworksTemplateState frameworksTemplateState : FrameworksTemplateState.values()) {
            if (frameworksTemplateState.name().equalsIgnoreCase(stateName)) {
                return frameworksTemplateState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
