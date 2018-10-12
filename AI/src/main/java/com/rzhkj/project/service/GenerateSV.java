package com.rzhkj.project.service;

import com.rzhkj.base.tools.WSTools;
import com.rzhkj.project.entity.ProjectJob;

/**
 * 生成文件接口定义
 * Created by lixin on 2018/2/1.
 */
public interface GenerateSV {
    /**
     * 根据项目码创建项目代码
     * @param projectCode 项目编码
     * @param projectJob  项目job
     * @param webSocketSession
     */
    void aiCode(String projectCode, ProjectJob projectJob, WSTools webSocketSession);
}
