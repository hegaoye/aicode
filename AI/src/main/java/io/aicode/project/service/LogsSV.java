/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;


import io.aicode.core.entity.BeanRet;

import java.util.Date;

public interface LogsSV {

    /**
     * 创建日志文件
     *
     * @param projectCode 项目编码
     * @param date
     * @return String
     */
    String createLogFiles(String projectCode, Date date);


    /**
     * 保存日志数据
     *
     * @param logs    日志
     * @param path    日志路径
     * @return String
     */
    boolean saveLogs(String logs, String path);


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
     * @param projectCode 文件路径
     * @param datetime    构建时间
     * @return String
     */
    BeanRet scanPath(String projectCode, String datetime);

}
