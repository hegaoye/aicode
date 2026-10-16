/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.project.entity.Project;
import com.aicode.project.vo.ProjectInitProgress;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;
import java.util.function.Consumer;

/**
 * 项目信息
 *
 * @author hegaoye
 */
public interface ProjectService extends IService<Project> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Project>
     */
    List<Project> list(QueryWrapper<Project> queryWrapper, int offset, int limit);

    /**
     * 删除项目
     * 1.判断项目是否存在
     * 2.删除数据
     * 3.删除项目
     *
     * @param code 项目编码
     */
    void delete(String code);

    void execute(String code);

    /**
     * 执行逆解析并上报进度（2026-10-18 异步化）。
     *
     * <p>与 {@link #execute(String)} 的业务逻辑逐行一致，区别仅在于通过回调上报阶段与
     * 逐表进度。调用方（{@code ProjectInitExecutor}）据此转成 ws 消息；
     * 同步调用方可传 no-op。</p>
     *
     * <p>详见 {@code openspec/specs/init-async-progress/spec.md}。</p>
     *
     * @param code       项目编码
     * @param onProgress 进度回调；业务逻辑 SHALL NOT 依赖其实现
     */
    void execute(String code, Consumer<ProjectInitProgress> onProgress);
}


