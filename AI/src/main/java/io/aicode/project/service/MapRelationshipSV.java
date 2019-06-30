/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.base.BaseMybatisSV;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapRelationship;

import java.util.List;

public interface MapRelationshipSV extends BaseMybatisSV<MapRelationship, Long> {

    /**
     * 根据表名查询所有模型关系
     *
     * @param tableName 表名
     * @return List<MapRelationship>
     */
    List<MapRelationship> queryList(String tableName);


    /**
     * 根据项目编码查询所有项目数据表
     *
     * @param projectCode 项目编码
     * @return List<MapRelationship>
     */
    List<MapClassTable> listMapClassTable(String projectCode);

    /**
     * 根据表名查询所有模型关系
     *
     * @param mapClassTableCode 类表映射编码
     * @return List<MapRelationship>
     */
    List<MapRelationship> listByProjectCode(String mapClassTableCode);


    /**
     * 根据code删除模型关系
     *
     * @param code 模型关系code
     */
    void deleteByCode(String code);

    /**
     * 根据项目编码统计是否已经设置表关系
     *
     * @param projectCode 项目编码
     * @return int
     */
    int countByProjectCode(String projectCode);


}
