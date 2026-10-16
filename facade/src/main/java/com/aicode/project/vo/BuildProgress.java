/**
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代码构建的终态事件 —— 经 WebSocket 推送（2026-10-18-build-log-terminal-signal）。
 *
 * <p><b>与 {@link ProjectInitProgress} 字段完全一致，但不复用同一个类</b>：两者是不同
 * 任务域的进度（构建 vs 逆解析），共用一个类会让人误以为可以互相消费对方的进度事件。
 * 字段一致只为**协议族统一** —— 消费端一套分流逻辑处理两者。</p>
 *
 * <p><b>本类只用于终态</b>：构建的中间日志行（"git 努力下载中....." 等 17 处）
 * 仍保持自由文本形态 —— 包装成 JSON 只增加噪音，零消费方使用。见
 * {@code openspec/specs/init-async-progress/spec.md} 的
 * 「中间日志行 SHALL 保持自由文本（协议边界）」。</p>
 *
 * <p>{@code phase} / {@code current} / {@code total} 三个字段在本类中**不使用**
 * （构建日志无阶段切分）。保留它们是为了让前端一套类型处理两个任务域，
 * 且未来若给构建加阶段进度，无需变更协议。</p>
 *
 * <p><b>⚠️ 无契约守护</b>：ws 通道不在 OpenAPI 覆盖范围内，与前端 TS 类型
 * （{@code progress-event.ts} 的 {@code ProgressEvent}）的字段名一致性
 * 只能靠测试与 review 保障。</p>
 */
@Data
public class BuildProgress implements java.io.Serializable {

    /**
     * 事件类型。构建场景只用 done / error（没有 progress —— 中间日志行是自由文本）
     */
    @Schema(description = "事件类型；构建场景只用 done / error")
    private String type;

    /**
     * 阶段标识。构建场景当前不使用，保留以统一协议形状
     */
    @Schema(description = "阶段标识；构建场景当前不使用")
    private String phase;

    /**
     * 已完成项数。构建场景当前不使用
     */
    @Schema(description = "已完成项数；构建场景当前不使用")
    private int current;

    /**
     * 总项数。构建场景当前不使用
     */
    @Schema(description = "总项数；构建场景当前不使用")
    private int total;

    /**
     * 可读文案（直接展示给用户）
     */
    @Schema(description = "可读文案")
    private String message;

    // ---- 构造工厂（避免 type 的裸字符串散落） ----

    /**
     * 构建阶段事件 —— 在每个步骤边界上报（2026-10-18-build-stage-progress）。
     *
     * <p>字段与 {@code ProjectInitProgress.progress} <b>刻意一致</b>，让前端的
     * {@code ProgressEvent} 一套类型通吃构建与解析两个任务域，防止两侧协议漂移。</p>
     *
     * @param phase   步骤的**稳定标识**（如 {@code downloadTemplate}），SHALL NOT 用会随
     *                文案变化的字符串 —— 文案一改，断言它的测试就红
     * @param current 步骤序号（从 1 开始）
     * @param total   步骤总数
     * @param message 该步骤的可读名称（展示用）
     */
    public static BuildProgress progress(String phase, int current, int total, String message) {
        BuildProgress p = new BuildProgress();
        p.setType("progress");
        p.setPhase(phase);
        p.setCurrent(current);
        p.setTotal(total);
        p.setMessage(message);
        return p;
    }

    /**
     * 构建成功终态。
     *
     * <p>替代原文本 {@code "Finished: SUCCESS"} —— 该消息此前从未被前端识别
     * （{@code useBuildLog} 对每条消息一律 {@code appendEntry}），只在服务端日志里
     * 可 grep。改为结构化后前端能据此自动收起面板。</p>
     */
    public static BuildProgress done(String message) {
        BuildProgress p = new BuildProgress();
        p.setType("done");
        p.setMessage(message);
        return p;
    }

    /**
     * 构建失败终态。替代原文本 {@code "Finished: ERROR"} 与 catch 里的
     * {@code "构建失败，详见日志（projectJob=xxx）"}。
     */
    public static BuildProgress error(String message) {
        BuildProgress p = new BuildProgress();
        p.setType("error");
        p.setMessage(message);
        return p;
    }
}