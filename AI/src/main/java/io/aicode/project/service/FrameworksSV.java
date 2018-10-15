/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.Frameworks;

public interface FrameworksSV extends BaseMybatisSV<Frameworks, Long> {

    /**
     * 删除
     *
     * @param code 技术编码
     */
    void delete(String code);
}
