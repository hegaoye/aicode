/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.entity;

/**
 * 模块文件信息 的实体类的状态
 *
 * @author hegaoye
 */
public enum ModuleFileState implements java.io.Serializable {
    ;

    public String val;

    ModuleFileState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ModuleFileState getEnum(String stateName) {
        for (ModuleFileState moduleFileState : ModuleFileState.values()) {
            if (moduleFileState.name().equalsIgnoreCase(stateName)) {
                return moduleFileState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
