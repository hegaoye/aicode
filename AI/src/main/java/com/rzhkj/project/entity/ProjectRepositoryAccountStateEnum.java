/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

/**
 * 状态：停用[Disenable]，启用[Enable]
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum ProjectRepositoryAccountStateEnum implements java.io.Serializable {
    Enable("Enable"),
    Disenable("停用"),
    Delete("Delete");
    public String val;

    ProjectRepositoryAccountStateEnum(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectRepositoryAccountStateEnum getEnum(String stateName) {
        for (ProjectRepositoryAccountStateEnum projectRepositoryAccountStateEnum : ProjectRepositoryAccountStateEnum.values()) {
            if (projectRepositoryAccountStateEnum.name().equalsIgnoreCase(stateName)) {
                return projectRepositoryAccountStateEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

