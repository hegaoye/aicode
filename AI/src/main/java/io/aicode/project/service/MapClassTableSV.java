/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.base.BaseMybatisSV;
import io.aicode.project.entity.MapClassTable;

import java.util.List;

public interface MapClassTableSV extends BaseMybatisSV<MapClassTable, Long> {

    /**
     * 解析表信息
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    boolean parseTableInfo(String projectCode);

    /**
     * 根据项目编码查询所有表信息
     *
     * @param projectCode 项目编码
     * @return List<MapClassTable>
     */
    List<MapClassTable> query(String projectCode);

}
