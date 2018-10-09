/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.MapFieldColumn;

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
