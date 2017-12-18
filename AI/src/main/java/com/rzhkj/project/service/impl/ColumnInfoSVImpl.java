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
import com.rzhkj.project.dao.ColumnInfoDAO;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.dao.TableInfoDAO;
import com.rzhkj.project.entity.Column;
import com.rzhkj.project.entity.ColumnInfo;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.entity.TableInfo;
import com.rzhkj.project.service.ColumnInfoSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Service
public class ColumnInfoSVImpl extends BaseMybatisSVImpl<ColumnInfo, Long> implements ColumnInfoSV {

    @Resource
    private ColumnInfoDAO columnInfoDAO;

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private ColumnDAO columnDAO;


    @Resource
    private TableInfoDAO tableInfoDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return columnInfoDAO;
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
        List<TableInfo> tableInfos = tableInfoDAO.query(map);
        if (tableInfos.isEmpty()) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ColumnInfoException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        tableInfos.forEach(tableInfo -> {
            //3.存储表字段信息
            map.clear();
            map.put("database", project.getEnglishName());
            map.put("tableName", tableInfo.getName());
            List<Column> columnList = columnDAO.query(map);
            if (columnList.isEmpty()) {
                return;
            }

            List<ColumnInfo> columnInfos = new ArrayList<>();
            columnList.forEach(column -> {
                columnInfos.add(new ColumnInfo(tableInfo.getCode(), String.valueOf(uidGenerator.getUID()),
                        column.getColumnName(), column.getDataType(), column.getColumnComment(), column.getColumnDefault(),
                        column.getColumnKey().equalsIgnoreCase("PRI") ? YNEnum.Y.name() : YNEnum.N.name()));
            });
            columnInfoDAO.batchInsert(columnInfos);

            //4.更新表的统计字段数
            tableInfo.setColumnNumber(columnList.size());
            tableInfoDAO.update(tableInfo);
        });
        return true;
    }
}
