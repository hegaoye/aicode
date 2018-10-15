/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.ProjectRepositoryAccount;

public interface ProjectRepositoryAccountSV extends BaseMybatisSV<ProjectRepositoryAccount, Long> {

    /**
     * 删除
     *
     * @param code 版本管理编码
     */
    void delete(String code);
}
