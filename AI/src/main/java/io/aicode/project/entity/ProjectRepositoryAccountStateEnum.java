/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

/**
 * 状态：停用[Disenable]，启用[Enable]
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum ProjectRepositoryAccountStateEnum implements java.io.Serializable {
    Enable("启用"),
    Disenable("停用"),
    Delete("删除");
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

