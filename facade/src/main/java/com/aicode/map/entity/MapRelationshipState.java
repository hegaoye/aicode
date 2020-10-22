/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.entity;

/**
 * 模型关系 的实体类的状态
 *
 * @author hegaoye
 */
public enum MapRelationshipState implements java.io.Serializable {
    Y("是否一对一"),
    Y("是否一对多"),
    ;

    public String val;

    MapRelationshipState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static MapRelationshipState getEnum(String stateName) {
        for (MapRelationshipState mapRelationshipState : MapRelationshipState.values()) {
            if (mapRelationshipState.name().equalsIgnoreCase(stateName)) {
                return mapRelationshipState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
