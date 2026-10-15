package com.aicode.database.dao;

import com.aicode.database.dao.mapper.DatabaseMapper;
import com.aicode.exceptions.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DisplayAttribute DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Slf4j
@Repository
public class DatabaseDAO {


    @Autowired
    private DatabaseMapper databaseMapper;


    public long count(String database) {
        return databaseMapper.count(database);
    }

    /**
     * 建库 + 建表。
     *
     * <p><b>失败处理约定</b>（2026-10-15 修正）：此前本方法把 {@code createTable} 的异常
     * 全部吞掉（只打一行 {@code e.getMessage()}，无堆栈），随后删库并正常返回，
     * 导致调用方 {@code ProjectServiceImpl.execute()} 以为成功继续往下走，
     * 最终在 {@code parse()} 阶段报出语义完全无关的 9006「查询结果不存在」。
     * 「用户 SQL 写错」与「元数据查询条件失效」两种截然不同的成因产出同一个错误码，无法排障。</p>
     *
     * <p>现在改为：清理半成品 schema 后<b>抛出</b>携带 schema 名与原始 cause 的异常。
     * 保留清理动作是合理的（不留下半成品 schema），问题在于「清理完还装作成功」。</p>
     *
     * @param database        目标 schema 名
     * @param sql             用户导入的 DDL（含 CREATE SCHEMA / USE 前缀）
     * @param defaultDatabase 清理连接上下文时切回的默认 schema
     * @throws DatabaseException 建库或建表失败
     */
    public void createDatabase(String database, String sql, String defaultDatabase) {
        try {
            //创建数据库
            databaseMapper.createDatabase(database);
            //切换数据库
            databaseMapper.useDatabase(database);
            //创建表
            databaseMapper.createTable(sql);
        } catch (Exception e) {
            // 带堆栈记录 —— 此前只有 getMessage()，看不到 SQL 失败的具体位置
            log.error("建库建表失败, schema={}", database, e);
            try {
                // 清理半成品 schema，避免下次「库已存在」分支误判
                databaseMapper.dropDatabase(database);
            } catch (Exception err) {
                log.error("清理半成品 schema 失败, schema={}", database, err);
            }
            // 上抛：保留原始 cause，并带上 schema 名以便定位
            throw DatabaseException.of("9999",
                    "数据库[" + database + "]建库建表失败，请检查导入的 SQL 是否合法", e);
        } finally {
            //无论执行成功或失败，始终切换到默认数据库
            try {
                databaseMapper.useDatabase(defaultDatabase);
            } catch (Exception e) {
                log.error("切回默认数据库失败, schema={}", defaultDatabase, e);
            }
        }
    }
}
