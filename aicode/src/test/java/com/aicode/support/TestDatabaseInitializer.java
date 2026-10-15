package com.aicode.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 测试环境数据库初始化器 —— 对<b>业务数据源</b>执行 schema.sql + data.sql。
 *
 * <h3>为什么需要它</h3>
 * 本项目在 {@code DataSourceConfig} 中自定义了名为 {@code tidbDataSource} 的
 * {@link DataSource} Bean。Spring Boot 的 {@code SqlInitializationAutoConfiguration}
 * 在绑定 {@code SqlDataSourceScriptDatabaseInitializer} 时依赖
 * {@code DataSourceProperties} 的 {@code name}（此处为 {@code h2-test-tidb}），
 * 与自定义 Bean 名不一致，导致自动初始化不会作用于业务库，表现为：
 *
 * <pre>
 *   JdbcSQLSyntaxErrorException: Table "FRAMEWORKS" not found
 *   SELECT COUNT( * ) AS total FROM frameworks WHERE (id &lt;&gt; ?)
 * </pre>
 *
 * <p>因此 test profile 把 {@code spring.sql.init.mode} 设为 {@code never}，
 * 改由本类显式初始化。生产 {@code application.yml} 不受本机制影响。</p>
 *
 * <h3>历史说明</h3>
 * 此前项目还有第二个 {@code uidDataSource}（标了 {@code @Primary}，存放百度 UID 的
 * {@code worker_node} 表），Boot 的自动初始化恰好绑在它身上。这曾让生产与测试
 * 出现不一致 —— 生产两个数据源指向同一个 H2 文件所以看不出问题，测试用两个独立
 * 内存库则暴露「业务库从未被初始化」。该数据源已随百度 UID 组件移除
 * （见 {@code openspec/changes/archive/2026-10-14-replace-baidu-uid-generator}），
 * 初始化职责随之收敛到本类。
 *
 * @see <a href="file:../../../../../../../../src/test/resources/application-test.yml">application-test.yml</a>
 */
@org.springframework.stereotype.Component
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
