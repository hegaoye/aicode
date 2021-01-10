package com.aicode.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 多数据源配置
 */
@Configuration
public class DataSourceConfig {
    //主数据源配置 tidb数据源
//    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.tidb")
    @Bean(name = "tidbDataSourceProperties")
    public DataSourceProperties tidbDataSourceProperties() {
        return new DataSourceProperties();
    }

    //主数据源 tidb数据源
//    @Primary
    @Bean(name = "tidbDataSource")
    public DataSource tidbDataSource(@Qualifier("tidbDataSourceProperties") DataSourceProperties tidbDataSourceProperties) {
        return tidbDataSourceProperties.initializeDataSourceBuilder().build();
    }

    //第二个uid数据源配置
    @Primary
    @Bean(name = "uidDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.uid")
    public DataSourceProperties uidDataSourceProperties() {
        return new DataSourceProperties();
    }

    //第二个uid数据源
    @Primary
    @Bean("uidDataSource")
    public DataSource uidDataSource(@Qualifier("uidDataSourceProperties") DataSourceProperties uidDataSourceProperties) {
        return uidDataSourceProperties.initializeDataSourceBuilder().build();
    }


}
