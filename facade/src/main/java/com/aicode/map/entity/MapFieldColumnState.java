/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

/**
 * 字段属性映射信息 的实体类的状态
 *
 * @author hegaoye
 */
public enum MapFieldColumnState implements java.io.Serializable {
    ;

    public String val;

    MapFieldColumnState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static MapFieldColumnState getEnum(String stateName) {
        for (MapFieldColumnState mapFieldColumnState : MapFieldColumnState.values()) {
            if (mapFieldColumnState.name().equalsIgnoreCase(stateName)) {
                return mapFieldColumnState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
