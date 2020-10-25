/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.entity;

/**
 * 设置 的实体类的状态
 *
 * @author hegaoye
 */
public enum SettingState implements java.io.Serializable {
    ;

    public String val;

    SettingState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static SettingState getEnum(String stateName) {
        for (SettingState settingState : SettingState.values()) {
            if (settingState.name().equalsIgnoreCase(stateName)) {
                return settingState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
