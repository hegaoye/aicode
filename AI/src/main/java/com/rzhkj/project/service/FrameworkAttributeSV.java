/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.FrameworkAttribute;

public interface FrameworkAttributeSV extends BaseMybatisSV<FrameworkAttribute, Long> {

    /**
     * 删除
     *
     * @param code 属性编码
     */
    void delete(String code);
}
