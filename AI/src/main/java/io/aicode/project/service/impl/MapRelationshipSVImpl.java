/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.project.dao.MapClassTableDAO;
import io.aicode.project.dao.MapRelationshipDAO;
import io.aicode.project.dao.ProjectMapDAO;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapRelationship;
import io.aicode.project.entity.ProjectMap;
import io.aicode.project.service.MapRelationshipSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Service
public class MapRelationshipSVImpl extends BaseMybatisSVImpl<MapRelationship, Long> implements MapRelationshipSV {

    @Resource
    private MapRelationshipDAO mapRelationshipDAO;

    @Resource
    private MapClassTableDAO mapClassTableDAO;

    @Resource
    private ProjectMapDAO projectMapDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return mapRelationshipDAO;
    }

    /**
     * 根据表名查询所有模型关系
     *
     * @param tableName 表名
     * @return List<MapRelationship>
     */
    @Override
    public List<MapRelationship> queryList(String tableName) {
        List<MapRelationship> mapRelationships = null;
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", tableName);
        MapClassTable mapClassTable = mapClassTableDAO.load(params);
        if (mapClassTable != null) {
            String code = mapClassTable.getCode();
            params = new HashMap<>();
            params.put("mapClassTableCode", code);
            mapRelationships = this.queryList(params);
        }
        return mapRelationships;
    }

    /**
     * 根据表名查询所有模型关系
     * 1.参数合法性验证
     * 2.根据项目编码查询项目是否存在
     * 3.根据项目编码查询项目数据表
     *
     * @param projectCode 项目编码
     * @return List<MapRelationship>
     */
    @Override
    public List<MapClassTable> listMapClassTable(String projectCode) {
        //1.参数合法性验证
        Assert.hasText(projectCode, "查询参数为空！");
        //2.根据项目编码查询项目是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("projectCode", projectCode);
        ProjectMap projectMap = projectMapDAO.load(params);
        List<MapClassTable> mapClassTables = null;
        if (projectMap != null) {
            mapClassTables = mapClassTableDAO.queryByProductCode(projectCode);
        }
        return mapClassTables;
    }

    /**
     * 根据映射编码查询所有模型关系
     * <p>
     * 1.参数合法性验证
     * 2.根据映射编码查询模型关系
     *
     * @param mapClassTableCode 类表映射编码
     * @return List<MapRelationship>
     */
    @Override
    public List<MapRelationship> listByProjectCode(String mapClassTableCode) {
        //1.参数合法性验证
        Assert.hasText(mapClassTableCode, "查询参数为空！");
        //2.根据映射编码查询模型关系
        Map<String, Object> params = new HashMap<>();
        params.put("mapClassTableCode", mapClassTableCode);
        return  mapRelationshipDAO.query(params);
    }

    /**
     * 根据code删除模型关系
     *
     * @param code 模型关系code
     */
    @Override
    public void deleteByCode(String code) {
        mapRelationshipDAO.delete(code);
    }

    /**
     * 根据项目编码统计是否已经设置表关系
     * @param projectCode    项目编码
     * @return int
     */
    @Override
    public int countByProjectCode(String projectCode) {
        return mapRelationshipDAO.countByProjectCode(projectCode);
    }


}
