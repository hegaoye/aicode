/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

/**
 * 管理类型:Gradle,Maven
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum BuildToolsTypeEnum implements java.io.Serializable {
    Gradle,
    Maven;

    /**
     * 根据管理类型名称查询管理类型
     *
     * @param stateName
     * @return
     */
    public static BuildToolsTypeEnum getEnum(String stateName) {
        for (BuildToolsTypeEnum projectStateEnum : BuildToolsTypeEnum.values()) {
            if (projectStateEnum.name().equalsIgnoreCase(stateName)) {
                return projectStateEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

