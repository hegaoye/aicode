package com.rzhkj.project.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.base.core.DataSourceProvider;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.ConfigUtil;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.dao.ProjectJobLogsDAO;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.entity.ProjectJobLogs;
import com.rzhkj.project.service.ProjectSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Map;


@Component
@Service
public class ProjectSVImpl extends BaseMybatisSVImpl<Project, Long> implements ProjectSV {

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectDAO;
    }

    /**
     * 创建数据库
     * 1.判断必要参数
     * 2.创建数据库
     * 3.记录任务日志
     *
     * @param projectCode 项目信息
     * @return true/false
     */
    @Override
    public boolean createDatabase(String projectCode) {
        //1.判断必要参数
        if (projectCode == null) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("projectCode", projectCode);
        Project project = projectDAO.load(map);
        if (project == null) {
            logger.error(BaseException.BaseExceptionEnum.Result_Not_Exist.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }

        String sql = project.getSql();
        String database = project.getEnglishName();
        if (StringUtils.isBlank(sql) || StringUtils.isBlank(database)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        //2.创建数据库
        try {
            String driver = ConfigUtil.getValue("jdbc.driver", "jdbc.properties");
            String url = ConfigUtil.getValue("jdbc.url", "jdbc.properties");
            String username = ConfigUtil.getValue("jdbc.username", "jdbc.properties");
            String password = ConfigUtil.getValue("jdbc.password", "jdbc.properties");
            if (StringUtils.isBlank(url) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
                throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
            }
            new DataSourceProvider()
                    .getConnection(driver, url, username, password)
                    .createDatabase(database)
                    .executeSqlScript(sql)
                    .close();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }


        //3.记录任务日志
        ProjectJobLogs projectJobLogs = new ProjectJobLogs();
        projectJobLogsDAO.insert(projectJobLogs);
        return true;
    }
}
