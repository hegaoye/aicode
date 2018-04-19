/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.MapClassTable;

public interface MapClassTableSV extends BaseMybatisSV<MapClassTable, Long> {

    /**
     * 解析表信息
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    boolean parseTableInfo(String projectCode);

}
