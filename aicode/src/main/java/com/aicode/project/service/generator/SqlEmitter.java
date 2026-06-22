package com.aicode.project.service.generator;

import com.aicode.project.dao.mapper.ProjectSqlMapper;
import com.aicode.project.entity.ProjectSql;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 项目 SQL 发射器：把用户提供的建表 SQL 加上分布式 ID worker_node 表 DDL，
 * 合并写入产物目录下的 {@code <englishName>.sql}。
 */
@Slf4j
@Component
public class SqlEmitter {

    /**
     * worker_node 建表 DDL（百度 UidGenerator worker 注册表）
     */
    static final String WORKER_NODE_DDL = "\nCREATE TABLE `worker_node` (\n"
            + "  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',\n"
            + "  `HOST_NAME` varchar(64) NOT NULL COMMENT 'host name',\n"
            + "  `PORT` varchar(64) NOT NULL COMMENT 'port',\n"
            + "  `TYPE` int(11) NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',\n"
            + "  `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',\n"
            + "  `MODIFIED` timestamp NOT NULL COMMENT 'modified time',\n"
            + "  `CREATED` timestamp NOT NULL COMMENT 'created time',\n"
            + "  PRIMARY KEY (`ID`)\n"
            + ")COMMENT='分布式id注册表';\n";

    @Autowired
    private ProjectSqlMapper projectSqlMapper;

    /**
     * 写出合并后的 SQL 脚本到 {@code <projectPath>/<englishName>.sql}。
     *
     * @return 追加的 worker_node DDL（供日志展示）
     */
    public String emit(String projectPath, String englishName, String projectCode) {
        ProjectSql projectSql = projectSqlMapper.selectOne(
                new LambdaQueryWrapper<ProjectSql>().eq(ProjectSql::getProjectCode, projectCode));
        String header = "-- AI-Code 为您构建代码，享受智慧生活!\n";
        String tsql = header
                + (projectSql != null && projectSql.getTsql() != null ? projectSql.getTsql() : "")
                + WORKER_NODE_DDL;
        try {
            FileUtils.writeByteArrayToFile(new File(projectPath + "/" + englishName + ".sql"), tsql.getBytes());
        } catch (IOException e) {
            log.error("写入 SQL 脚本失败: {}/{}.sql", projectPath, englishName, e);
        }
        return WORKER_NODE_DDL;
    }
}
