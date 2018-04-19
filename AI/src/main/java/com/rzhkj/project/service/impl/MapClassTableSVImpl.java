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
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.project.dao.MapClassTableDAO;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.dao.TableDAO;
import com.rzhkj.project.entity.MapClassTable;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.entity.Table;
import com.rzhkj.project.service.MapClassTableSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Service
public class MapClassTableSVImpl extends BaseMybatisSVImpl<MapClassTable, Long> implements MapClassTableSV {

    @Resource
    private MapClassTableDAO mapClassTableDAO;

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private TableDAO tableDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return mapClassTableDAO;
    }

    /**
     * 解析表信息
     * 1.查询项目信息
     * 2.查询表信息
     * 3.存储表信息
     *
     * @param projectCode 项目编码
     * @return true/false
     */
    @Override
    public boolean parseTableInfo(String projectCode) {
        if (StringUtils.isBlank(projectCode)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        //1.查询项目信息
        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", projectCode);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        //2.查询表信息
        map.clear();
        map.put("database", project.getEnglishName());
        List<Table> tableList = tableDAO.query(map);
        if (tableList.isEmpty()) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        List<MapClassTable> mapClassTables = new ArrayList<>();
        tableList.forEach(table -> {
            mapClassTables.add(new MapClassTable(String.valueOf(uidGenerator.getUID()), table.getTableName(), table.getTableComment()));
        });
        //3.存储表信息
        mapClassTableDAO.batchInsert(mapClassTables);
        return true;
    }
}
