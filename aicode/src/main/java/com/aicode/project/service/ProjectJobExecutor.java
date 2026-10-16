package com.aicode.project.service;

import com.aicode.config.websocket.WSClientManager;
import com.aicode.project.entity.ProjectJob;
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
    @Async
    public void execute(String projectCode, ProjectJob projectJob) {
        log.info("异步执行构建, projectCode:{}, projectJobCode:{}", projectCode, projectJob.getCode());
        // 房间上下文：让 aiCode 内部 17 处 sendMessage 只投递给订阅了本项目的连接，
        // 而不必把 projectCode 沿方法签名一路下传（那会动生成主链路）。
        // try/catch SHALL 在 withRoom **内部** —— 否则 catch 里推的消息会因
        // ThreadLocal 已清理而广播给所有连接。
        WSClientManager.withRoom(projectCode, () -> {
            try {
                generateSV.aiCode(projectCode, projectJob);
            } catch (Exception e) {
                log.error("项目构建异步执行异常, projectCode:{}, projectJobCode:{}",
                        projectCode, projectJob.getCode(), e);
                WSClientManager.sendMessage("构建调度异常，详见日志");
            }
        });
    }
}
