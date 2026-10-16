package com.aicode.project.service;

import com.aicode.config.websocket.WSClientManager;
import com.aicode.project.vo.ProjectInitProgress;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 异步执行项目逆解析（`POST /project/init`）。
 *
 * <p><b>为什么独立成 Bean</b>：{@code @Async} 必须经 Spring 代理拦截。若把 {@code @Async}
 * 加在 {@code ProjectServiceImpl} 上而由同类内部 {@code this.execute(...)} 调用，
 * 会绕过 AOP 代理、静默变回同步 —— HTTP 请求继续阻塞。
 * 同样的坑见 {@link ProjectJobExecutor} 的 Javadoc。</p>
 *
 * <p><b>异常处理</b>：{@code @Async} 方法运行在虚拟线程上，异常若逃逸到线程顶层会静默消失
 * （没有任何调用方在等它）。故本方法 try/catch 兜底，并把失败推给前端 ——
 * 见 AGENTS §6.7 对异步构建的同类要求。</p>
 *
 * <p><b>进度协议</b>：JSON 字符串，以 <code>{</code> 开头，与构建日志的 free-form 文本
 * 天然可区分，故可共存于同一 ws 通道。前端见 {@code use-init-progress.ts}。</p>
 *
 * @see ProjectInitProgress
 */
@Slf4j
@Component
public class ProjectInitExecutor {

    @Autowired
    private ProjectService projectService;

    /**
     * 最近一次进度快照 —— 供失败消息取用。
     *
     * <p>异常本身不带进度信息，而 {@code parse()} 是逐表写入且无事务的：失败时已写入的表
     * **不会回滚**。故必须从进度快照里取出"已完成 i/N"如实告知用户，否则会误导他们
     * 以为需要从头重跑（spec 的 Scenario「失败 SHALL 报告已完成工作量」）。</p>
     */
    private volatile ProjectInitProgress lastProgress;

    /**
     * 异步执行逆解析，进度经 WebSocket 推送。
     *
     * <p>方法返回即表示"任务已提交"—— 实际执行在虚拟线程池上进行，调用方（HTTP 线程）
     * 立即得到响应。进度与终态通过 ws 消息送达前端。</p>
     *
     * @param code 项目编码
     */
    @Async
    public void execute(String code) {
        log.info("异步执行逆解析, projectCode:{}", code);
        this.lastProgress = null;
        // 房间上下文：进度与终态只投递给订阅了本项目的连接。
        // try/catch SHALL 在 withRoom **内部** —— 否则 catch 里推的 error 消息
        // 会因 ThreadLocal 已清理而广播给所有连接（design D4）。
        WSClientManager.withRoom(code, () -> {
            try {
                Consumer<ProjectInitProgress> reporter = progress -> {
                    this.lastProgress = progress;
                    WSClientManager.sendMessage(JSON.toJSONString(progress));
                };
                projectService.execute(code, reporter);
                WSClientManager.sendMessage(
                        JSON.toJSONString(ProjectInitProgress.done("解析完成")));
            } catch (Exception e) {
                String msg = buildFailureMessage(e);
                log.error("逆解析异步执行异常, projectCode:{}, message:{}", code, msg, e);
                WSClientManager.sendMessage(JSON.toJSONString(ProjectInitProgress.error(msg)));
            }
        });
    }

    /**
     * 组装失败消息 —— 能从进度快照取到 parse 阶段信息时，补上"已完成 i/N"的前缀。
     */
    String buildFailureMessage(Exception e) {
        String reason = (e.getMessage() == null || e.getMessage().isBlank())
                ? e.getClass().getSimpleName()
                : e.getMessage();
        ProjectInitProgress p = this.lastProgress;
        if (p != null && "parse".equals(p.getPhase()) && p.getTotal() > 0) {
            return String.format("已解析 %d/%d 张表后失败：%s（已写入的表不会回滚，可重新解析）",
                    p.getCurrent(), p.getTotal(), reason);
        }
        return "解析失败：" + reason;
    }

    /**
     * 最近一次进度快照 —— 供测试断言"失败时如实上报已完成工作量"。
     *
     * <p>暴露为包级可见而非 public：本包测试可直接用，
     * 而外部代码不该依赖这个内部状态。</p>
     */
    ProjectInitProgress getLastProgress() {
        return lastProgress;
    }
}