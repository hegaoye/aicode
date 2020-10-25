/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.entity;

/**
 * 第三方模块池 的实体类的状态
 *
 * @author hegaoye
 */
public enum ModuleState implements java.io.Serializable {
    ;

    public String val;

    ModuleState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ModuleState getEnum(String stateName) {
        for (ModuleState moduleState : ModuleState.values()) {
            if (moduleState.name().equalsIgnoreCase(stateName)) {
                return moduleState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
