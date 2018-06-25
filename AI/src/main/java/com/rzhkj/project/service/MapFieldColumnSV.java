/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service;

import com.rzhkj.core.base.BaseMybatisSV;
import com.rzhkj.project.entity.MapFieldColumn;

public interface MapFieldColumnSV extends BaseMybatisSV<MapFieldColumn, Long> {

    /**
     * 解析表字段信息
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    boolean parseColumnInfo(String projectCode);
}