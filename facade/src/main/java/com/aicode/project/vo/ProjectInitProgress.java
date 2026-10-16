/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 长耗时任务（`POST /project/init` 逆解析）的进度事件 —— 经 WebSocket 推送。
 *
 * <p>序列化后的形态是 JSON 字符串，以 <code>{</code> 开头，与既有的 free-form 文本日志
 * （构建日志）天然可区分 —— 故两类消息可共存于同一 ws 通道，消费端按首字符分流。
 * 详见 {@code openspec/specs/init-async-progress/spec.md}。</p>
 *
 * <p><b>⚠️ 无契约守护</b>：ws 通道不在 OpenAPI 覆盖范围内，因此本类与前端 TS 类型
 * （{@code use-init-progress.ts} 的 {@code InitProgressEvent}）的字段名一致性
 * <b>只能靠测试与 review 保障</b>。改字段名时须同步改前端并跑前端测试。</p>
 *
 * <p><b>刻意不放在契约里</b>：本类不会出现在任何 HTTP 响应的 schema 中
 * （`POST /project/init` 返 {@code R<Void>}），故无需 {@code @Schema} 的值域约束。</p>
 */
@Data
public class ProjectInitProgress implements java.io.Serializable {

    /**
     * 事件类型：progress（进行中）/ done（成功结束）/ error（失败结束）
     */
    @Schema(description = "事件类型：progress / done / error")
    private String type;

    /**
     * 阶段标识：createDatabase（建库并执行建表脚本）/ parse（逆解析表与列）
     */
    @Schema(description = "阶段标识：createDatabase / parse")
    private String phase;

    /**
     * 已完成项数。不可知时为 0（建库阶段内部无可切分的进度点，诚实报未知）
     */
    @Schema(description = "已完成项数；不可知时为 0")
    private int current;

    /**
     * 总项数。不可知时为 0
     */
    @Schema(description = "总项数；不可知时为 0")
    private int total;

    /**
     * 可读文案（直接展示给用户）
     */
    @Schema(description = "可读文案")
    private String message;

    // ---- 构造工厂（避免 type 的裸字符串散落） ----

    /** 进行中事件。`current` 取**已完成数**（前端可直接渲染 current/total，无需 +1）。 */
    public static ProjectInitProgress progress(String phase, int current, int total, String message) {
        ProjectInitProgress p = new ProjectInitProgress();
        p.setType("progress");
        p.setPhase(phase);
        p.setCurrent(current);
        p.setTotal(total);
        p.setMessage(message);
        return p;
    }

    /** 成功结束事件。 */
    public static ProjectInitProgress done(String message) {
        ProjectInitProgress p = new ProjectInitProgress();
        p.setType("done");
        p.setMessage(message);
        return p;
    }

    /**
     * 失败结束事件。
     *
     * <p>调用方 SHALL 把已完成工作量写进 {@code message}（如"已解析 4/12 张表后失败"）——
     * {@code parse()} 逐表写入且无事务，失败时已写入的部分**不回滚**，
     * 不如实告知会让用户误以为需要重跑全部。详见 spec 的
     * 「失败 SHALL 如实上报已完成工作量」。</p>
     */
    public static ProjectInitProgress error(String message) {
        ProjectInitProgress p = new ProjectInitProgress();
        p.setType("error");
        p.setMessage(message);
        return p;
    }
}