<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <setting name="cacheEnabled" value="true"/>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
    <typeAliases>
    <#list classes as class>
        <!--${class.notes} 对应数据库表名 ${class.tableName}-->
        <package name="${basePackage}.${class.classModel}.entity"/>
    </#list>
    </typeAliases>

    <plugins>
        <plugin interceptor="com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor">
            <property name="dialectClass" value="com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect"/>
        </plugin>
    </plugins>

</configuration>
