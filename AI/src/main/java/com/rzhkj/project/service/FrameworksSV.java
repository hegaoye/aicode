/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.Frameworks;

public interface FrameworksSV extends BaseMybatisSV<Frameworks, Long> {

    /**
     * 删除
     *
     * @param code 技术编码
     */
    void delete(String code);
}
