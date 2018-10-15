/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.MapFieldColumn;

import java.util.List;

public interface MapFieldColumnSV extends BaseMybatisSV<MapFieldColumn, Long> {

    /**
     * 解析表字段信息
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    boolean parseColumnInfo(String projectCode);


    /**
     * 根据表映射编码，查询表的所有字段信息
     *
     * @param mapClassTableCode 映射编码
     * @return List<MapFieldColumn>
     */
    List<MapFieldColumn> listFields(String mapClassTableCode);
}
