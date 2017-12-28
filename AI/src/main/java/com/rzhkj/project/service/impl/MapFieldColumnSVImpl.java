/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ColumnInfoException;
import com.rzhkj.project.dao.ColumnDAO;
import com.rzhkj.project.dao.MapClassTableDAO;
import com.rzhkj.project.dao.MapFieldColumnDAO;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.entity.Column;
import com.rzhkj.project.entity.MapClassTable;
import com.rzhkj.project.entity.MapFieldColumn;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.service.MapFieldColumnSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Service
public class MapFieldColumnSVImpl extends BaseMybatisSVImpl<MapFieldColumn, Long> implements MapFieldColumnSV {

    @Resource
    private MapFieldColumnDAO mapFieldColumnDAO;

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private ColumnDAO columnDAO;


    @Resource
    private MapClassTableDAO mapClassTableDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return mapFieldColumnDAO;
    }

    /**
     * 解析表字段信息
     * 1.查询项目信息
     * 2.查询表字段信息
     * 3.存储表字段信息
     * 4.更新表的统计字段数
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    @Override
    public boolean parseColumnInfo(String projectCode) {
        //1.查询项目信息
        if (StringUtils.isBlank(projectCode)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ColumnInfoException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", projectCode);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ColumnInfoException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        //2.查询表字段信息
        map.clear();
        List<MapClassTable> mapClassTables = mapClassTableDAO.query(map);
        if (mapClassTables.isEmpty()) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ColumnInfoException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        mapClassTables.forEach(MapClassTable -> {
            //3.存储表字段信息
            map.clear();
            map.put("database", project.getEnglishName());
            map.put("tableName", MapClassTable.getTableName());
            List<Column> columnList = columnDAO.query(map);
            if (columnList.isEmpty()) {
                return;
            }

            List<MapFieldColumn> mapFieldColumns = new ArrayList<>();
            columnList.forEach(column -> {
                mapFieldColumns.add(new MapFieldColumn(MapClassTable.getCode(), String.valueOf(uidGenerator.getUID()),
                        column.getColumnName(), column.getDataType(), column.getColumnComment(), column.getColumnDefault(),
                        column.getColumnKey().equalsIgnoreCase("PRI") ? YNEnum.Y.name() : YNEnum.N.name()));
            });
            mapFieldColumnDAO.batchInsert(mapFieldColumns);

            //4.更新表的统计字段数
            mapClassTableDAO.update(MapClassTable);
        });
        return true;
    }
}
