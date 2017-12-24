/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

/**
 * 模型：po,ctrl,vo,dao,facade,service
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum ProjectCodeModelEnum implements java.io.Serializable {
    po,
    ctrl,
    vo,
    dao,
    facade,
    service,
    framework,
    ;

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectCodeModelEnum getEnum(String stateName) {
        for (ProjectCodeModelEnum projectStateEnum : ProjectCodeModelEnum.values()) {
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

