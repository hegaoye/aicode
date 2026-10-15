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
 * 项目 SQL 发射器：把用户提供的建表 SQL 写入产物目录下的 {@code <englishName>.sql}。
 *
 * <p><b>历史说明</b>：本类此前会在用户 SQL 之后追加一段 {@code worker_node} 建表 DDL，
 * 那是百度 UidGenerator 的 worker 注册表。该组件已随
 * {@code openspec/changes/archive/2026-10-14-replace-baidu-uid-generator} 移除，
 * 且 {@code worker_node} 表的用途本就是 aicode 平台自身的 ID 分配 ——
 * 生成出来的项目并不需要它，因此一并从产物中移除。</p>
 */
@Slf4j
@Component
public class SqlEmitter {

    @Autowired
    private ProjectSqlMapper projectSqlMapper;

    /**
     * 写出合并后的 SQL 脚本到 {@code <projectPath>/<englishName>.sql}。
     *
     * @return 实际写入文件的 SQL 内容（供日志展示）
     */
    public String emit(String projectPath, String englishName, String projectCode) {
        ProjectSql projectSql = projectSqlMapper.selectOne(
                new LambdaQueryWrapper<ProjectSql>().eq(ProjectSql::getProjectCode, projectCode));
        String header = "-- AI-Code 为您构建代码，享受智慧生活!\n";
        // 说明：写入的 tsql 是用户导入的项目建表脚本，其中出现的 code 列属于生成项目的
        // 业务编码，与 aicode 平台的实体主键策略（数据库自增）无关。
        String tsql = header
                + (projectSql != null && projectSql.getTsql() != null ? projectSql.getTsql() : "");
        try {
            FileUtils.writeByteArrayToFile(new File(projectPath + "/" + englishName + ".sql"), tsql.getBytes());
        } catch (IOException e) {
            log.error("写入 SQL 脚本失败: {}/{}.sql", projectPath, englishName, e);
        }
        return tsql;
    }
}
