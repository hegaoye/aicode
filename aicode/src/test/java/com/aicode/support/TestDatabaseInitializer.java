package com.aicode.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 测试环境数据库初始化器 —— 对<b>业务数据源</b>（tidb）执行 schema.sql + data.sql。
 *
 * <h3>为什么需要它</h3>
 * 本项目在 {@code DataSourceConfig} 中自定义了两个 {@link DataSource} Bean：
 * {@code tidbDataSource}（业务库）与 {@code uidDataSource}（百度 UID 的 worker_node 库，
 * 标注了 {@code @Primary}）。
 *
 * <p>Spring Boot 的 {@code SqlInitializationAutoConfiguration} 只把
 * {@code SqlDataSourceScriptDatabaseInitializer} 绑定到「唯一或 @Primary 的 DataSource」。
 * 在本项目里那就是 <b>uidDataSource</b> —— 于是 {@code spring.sql.init.mode=always}
 * 只会初始化 uid 库，而所有业务表（{@code frameworks} / {@code project} / {@code map_*} …）
 * 都在 tidb 库，最终表现为：</p>
 *
 * <pre>
 *   JdbcSQLSyntaxErrorException: Table "FRAMEWORKS" not found
 *   SELECT COUNT( * ) AS total FROM frameworks WHERE (id &lt;&gt; ?)
 * </pre>
 *
 * <p>因此测试 profile 把 {@code spring.sql.init.mode} 设为 {@code never}，
 * 改由本类显式初始化。生产环境（{@code application.yml}）保持 {@code always} 不变。</p>
 *
 * @see <a href="file:../../../../../../../../src/test/resources/application-test.yml">application-test.yml</a>
 */
@Component
public class TestDatabaseInitializer implements InitializingBean {

    private final DataSource businessDataSource;

    public TestDatabaseInitializer(@Qualifier("tidbDataSource") DataSource businessDataSource) {
        this.businessDataSource = businessDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/schema.sql"));
        populator.addScript(new ClassPathResource("db/data.sql"));
        populator.setContinueOnError(false);
        populator.execute(businessDataSource);
    }
}
