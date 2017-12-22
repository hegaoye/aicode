/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

/**
 * 路径类型：Java,Resource,Webapp
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum BuildToolsPathTypeEnum implements java.io.Serializable {
    Java,
    Resource,
    Webapp;

    /**
     * 根据管理类型名称查询管理类型
     *
     * @param stateName
     * @return
     */
    public static BuildToolsPathTypeEnum getEnum(String stateName) {
        for (BuildToolsPathTypeEnum projectStateEnum : BuildToolsPathTypeEnum.values()) {
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

