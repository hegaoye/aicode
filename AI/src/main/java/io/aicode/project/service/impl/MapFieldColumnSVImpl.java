/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.core.enums.YNEnum;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.exceptions.ColumnInfoException;
import io.aicode.project.dao.ColumnDAO;
import io.aicode.project.dao.MapClassTableDAO;
import io.aicode.project.dao.MapFieldColumnDAO;
import io.aicode.project.dao.ProjectDAO;
import io.aicode.project.entity.Column;
import io.aicode.project.entity.MapClassTable;
import io.aicode.project.entity.MapFieldColumn;
import io.aicode.project.entity.Project;
import io.aicode.project.service.MapFieldColumnSV;
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

    /**
     * 根据表映射编码，查询表的所有字段信息
     *
     * @param mapClassTableCode 映射编码
     * @return List<MapFieldColumn>
     */
    @Override
    public List<MapFieldColumn> listFields(String mapClassTableCode) {
        if (StringUtils.isBlank(mapClassTableCode)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ColumnInfoException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        return  mapFieldColumnDAO.listFields(mapClassTableCode);
    }
}
