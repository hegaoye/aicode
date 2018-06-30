/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.entity;

/**
 * 仓库类型:Git, Svn
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum ProjectRepositoryTypeEnum implements java.io.Serializable {
    GIT("GIT"),
    SVN("SVN"),;
    public String val;

    ProjectRepositoryTypeEnum(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static ProjectRepositoryTypeEnum getEnum(String stateName) {
        for (ProjectRepositoryTypeEnum projectRepositoryAccountStateEnum : ProjectRepositoryTypeEnum.values()) {
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

