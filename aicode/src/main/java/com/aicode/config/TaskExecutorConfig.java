package com.aicode.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * @Async 任务的虚拟线程执行器。
 *
 * <p>Spring 6.1+ 的 {@link SimpleAsyncTaskExecutor} 已内置 {@code setVirtualThreads(true)}，
 * 等价于之前自定义的 {@code Thread.startVirtualThread} 包装。配合 {@link MdcTaskDecorator}
 * 跨虚拟线程传递 traceId，让异步日志链路可追踪。</p>
 */
@Slf4j
@EnableAsync
@Configuration
public class TaskExecutorConfig implements AsyncConfigurer {

    @Autowired
    private MdcTaskDecorator mdcTaskDecorator;

    @Bean(name = "threadPoolExecutor")
    @Override
    public Executor getAsyncExecutor() {
        SimpleAsyncTaskExecutor exec = new SimpleAsyncTaskExecutor("aicode-");
        exec.setVirtualThreads(true);                 // Spring 6.1+ 内置：每个任务跑在 VT
        exec.setTaskDecorator(mdcTaskDecorator);      // 跨 VT 复制 MDC（关键修复）
        return exec;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
