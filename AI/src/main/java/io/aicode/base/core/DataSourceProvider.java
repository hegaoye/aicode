package io.aicode.base.core;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库链接
 * 1.执行sql脚本
 * 2.获取数据库信息
 */
@Data
public class DataSourceProvider {
    protected final static Logger logger = LoggerFactory.getLogger(DataSourceProvider.class);

    /**
     * 数据库链接
     */
    private Connection connection = null;

    /**
     * 获得数据库链接
     *
     * @param driver   数据库驱动
     * @param url      数据库地址
     * @param user     数据库账户
     * @param password 数据库密码
     * @return DataSourceProvider
     */
    public DataSourceProvider getConnection(String driver, String url, String user, String password) {
        Assert.hasText(driver, "数据库驱动错误");
        Assert.hasText(url, "数据库地址错误");
        Assert.hasText(user, "数据库用户名错误");
        Assert.hasText(password, "数据库密码错误");
        try {
            Class.forName(driver);
            logger.info("jdbc ===> " + url);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 获得数据库链接
     *
     * @param driver   数据库驱动
     * @param host     数据库地址
     * @param port     数据库端口
     * @param user     数据库账户
     * @param password 数据库密码
     * @return DataSourceProvider
     */
    public DataSourceProvider getConnection(String driver, String host, String port, String user, String password) {
        Assert.hasText(driver, "数据库驱动错误");
        Assert.hasText(host, "数据库地址错误");
        Assert.hasText(port, "数据库端口错误");
        Assert.hasText(user, "数据库用户名错误");
        Assert.hasText(password, "数据库密码错误");
        try {
            String url = "jdbc:mysql://{host}:{port}/ai-code?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&allowMultiQueries=true";
            url = url.replace("{host}", host)
                    .replace("port", port);
            Class.forName(driver);
            logger.info("jdbc ===> " + url);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 执行sql脚本
     *
     * @param sqlPath sql脚本路径
     * @return DataSourceProvider
     */
    public DataSourceProvider executeSqlScript(String sqlPath) throws FileNotFoundException {
        Assert.hasText(sqlPath, "数据库脚本路径错误！");
        File file = new File(sqlPath);
        if (!file.exists()) {
            throw new FileNotFoundException("数据库脚本文件不存在错误！");
        }
        logger.info("sql脚本 ===> " + sqlPath);
        this.readScript(sqlPath);
        FileSystemResource fileSystemResource = new FileSystemResource(sqlPath);
        EncodedResource encodedResource = new EncodedResource(fileSystemResource, "UTF-8");
        ScriptUtils.executeSqlScript(connection, encodedResource);
        return this;
    }

    /**
     * 读取脚本
     *
     * @param sqlPath sql脚本路径
     * @return sql
     */
    public DataSourceProvider readScript(String sqlPath) {
        try {
            Assert.hasText(sqlPath, "数据库脚本路径错误！");
            String sql = ScriptUtils.readScript(new LineNumberReader(new FileReader(sqlPath)), "--", ";");
            logger.info("sql脚本 ===> " + sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 创建数据库
     *
     * @param database 数据库名称
     * @return DataSourceProvider
     */
    public DataSourceProvider createDatabase(String database) {
        try {
            Assert.hasText(database, "数据库名称错误！");

            String sql = "CREATE DATABASE {database};USE {database};";
            sql = sql.replace("{database}", database).replace("{database}", database);
            Statement statement = null;
            if (connection.isClosed()) {
                return null;
            }
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 关闭链接
     *
     * @return DataSourceProvider
     */
    public DataSourceProvider close() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }


}
