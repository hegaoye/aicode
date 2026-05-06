package com.aicode.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class TaskExecutorConfig implements AsyncConfigurer {

    @Bean(name = "threadPoolExecutor")
    @Override
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor("virtual-") {
            @Override
            public void execute(Runnable task) {
                Thread.startVirtualThread(task);
            }
        };
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
