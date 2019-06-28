/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.ProjectJob;
import org.springframework.web.socket.WebSocketSession;

public interface ProjectJobSV extends BaseMybatisSV<ProjectJob, Long> {

    /**
     * 创建项目任务
     *
     * @param projectJob
     */
    void build(ProjectJob projectJob);

    /**
     * 删除任务
     *
     * @param code 任务编码
     */
    void delete(String code);

    /**
     * 执行任务
     *
     * @param code 任务编码
     */
    ProjectJob execute(String code);

    /**
     * 执行任务
     *
     * @param code             任务编码
     * @param webSocketSession 对象
     */
    ProjectJob execute(String code, WebSocketSession webSocketSession);
}