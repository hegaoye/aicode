/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.ToolsCategory;

public interface ToolsCategorySV extends BaseMybatisSV<ToolsCategory, Long> {

    /**
     * 删除
     *
     * @param code 类型编码
     */
    void delete(String code);
}
