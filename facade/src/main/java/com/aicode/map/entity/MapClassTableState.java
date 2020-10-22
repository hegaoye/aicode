/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

/**
 * 类表映射信息 的实体类的状态
 *
 * @author hegaoye
 */
public enum MapClassTableState implements java.io.Serializable {
    ;

    public String val;

    MapClassTableState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static MapClassTableState getEnum(String stateName) {
        for (MapClassTableState mapClassTableState : MapClassTableState.values()) {
            if (mapClassTableState.name().equalsIgnoreCase(stateName)) {
                return mapClassTableState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
