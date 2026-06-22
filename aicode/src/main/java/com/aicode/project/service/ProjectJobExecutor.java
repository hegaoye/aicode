package com.aicode.project.service;

import com.aicode.config.websocket.WSClientManager;
import com.aicode.project.entity.ProjectJob;
import com.baidu.fsg.uid.UidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步执行项目构建任务。
 *
 * <p>独立成 Bean 是为了让 {@code @Async} 经 Spring 代理拦截：
 * {@code ProjectJobServiceImpl} 内部 {@code this.generateCode(...)} 会绕过 AOP，
 * 导致构建同步阻塞 HTTP 线程。</p>
 */
@Slf4j
@Component
public class ProjectJobExecutor {

    @Autowired
    private GenerateSV generateSV;

    @Autowired
    private UidGenerator uidGenerator;

    @Async
    public void execute(String projectCode, ProjectJob projectJob) {
        log.info("异步执行构建, projectCode:{}, projectJobCode:{}", projectCode, projectJob.getCode());
        try {
            generateSV.aiCode(projectCode, projectJob);
        } catch (Exception e) {
            log.error("项目构建异步执行异常, projectCode:{}, projectJobCode:{}", projectCode, projectJob.getCode(), e);
            WSClientManager.sendMessage("构建调度异常，详见日志");
        }
    }
}
