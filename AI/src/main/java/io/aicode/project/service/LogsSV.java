/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;


import io.aicode.core.entity.BeanRet;

public interface LogsSV {

    /**
     * 创建日志文件
     *
     * @param projectCode 项目编码
     * @return String
     */
    String createLogFiles(String projectCode);


    /**
     * 保存日志数据
     *
     * @param logs    日志
     * @param path    日志路径
     * @param isClose 是否关闭连接
     * @return String
     */
    boolean saveLogs(String logs, String path, Boolean isClose);


    /**
     * 根据项目编码查询项目路径
     *
     * @param projectCode 项目编码
     * @return String
     */
    String loadFilePath(String projectCode);

    /**
     * 查询日志信息
     *
     * @param path 文件路径
     * @return String
     */
    BeanRet scanPath(String path);

}
