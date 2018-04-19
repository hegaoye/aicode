/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ProjectRepositoryAccount;

public interface ProjectRepositoryAccountSV extends BaseMybatisSV<ProjectRepositoryAccount, Long> {

    /**
     * 删除
     *
     * @param code 版本管理编码
     */
    void delete(String code);
}
