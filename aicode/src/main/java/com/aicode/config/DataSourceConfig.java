package com.aicode.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置。
 *
 * <p>历史说明：本类曾配置 tidb / uid 两个数据源，其中 {@code uidDataSource} 标了
 * {@code @Primary}，专用于存放百度 UID 组件的 {@code worker_node} 表。该组件已在
 * {@code openspec/changes/2026-10-14-replace-baidu-uid-generator} 中移除，
 * 第二数据源随之删除。</p>
 *
 * @see <a href="file:../../../../../../../../../openspec/specs/identifier-generation/spec.md">identifier-generation 能力</a>
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.tidb")
    @Bean(name = "tidbDataSourceProperties")
    public DataSourceProperties tidbDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "tidbDataSource")
    public DataSource tidbDataSource(@Qualifier("tidbDataSourceProperties")
                                     DataSourceProperties tidbDataSourceProperties) {
        return tidbDataSourceProperties.initializeDataSourceBuilder().build();
    }
}
