package com.aicode.project.service;

import com.aicode.project.vo.BuildProgress;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 构建终态消息的形态守护（2026-10-18-build-log-terminal-signal）。
 *
 * <p><b>背景</b>：{@code GeneratorSVImpl.aiCode()} 的 finally 块<b>早已</b>推送
 * {@code "Finished: SUCCESS"} / {@code "Finished: ERROR"}，但前端 {@code useBuildLog}
 * 对每条消息一律 {@code appendEntry}、从不识别 —— grep 该文件 {@code Finished} 零命中。
 * 即「后端有信号、前端漏消费」。本变更把终态升级为结构化 JSON，使前端能自动收起面板。</p>
 *
 * <p>本测试守护<b>消息形态</b>（字段名与 {@code type`` 取值），不触碰生成主链路本身 ——
 * 那由既有测试覆盖。</p>
 *
 * @see BuildProgress
 */
class BuildProgressTerminalSignalTest {

    @Test
    @DisplayName("BuildProgress.done SHALL 序列化为含 type=done 与非空 message 的 JSON")
    void done_serializesToStructuredJson() {
        String json = JSON.toJSONString(BuildProgress.done("构建成功"));
        JSONObject obj = JSON.parseObject(json);

        assertThat(obj.getString("type")).isEqualTo("done");
        assertThat(obj.getString("message")).isEqualTo("构建成功").isNotEmpty();
    }

    @Test
    @DisplayName("BuildProgress.error SHALL 序列化为含 type=error 与非空 message 的 JSON")
    void error_serializesToStructuredJson() {
        String json = JSON.toJSONString(BuildProgress.error("构建失败，详见日志（projectJob=123）"));
        JSONObject obj = JSON.parseObject(json);

        assertThat(obj.getString("type")).isEqualTo("error");
        assertThat(obj.getString("message"))
                .contains("构建失败")
                .contains("projectJob=123");
    }

    @Test
    @DisplayName("终态消息 SHALL 以 { 开头（与 free-form 中间日志天然分流）")
    void terminalMessage_startsWithBrace() {
        // 前端 parseProgressEvent 的判据就是首字符 `{` —— 若这点变了，分流逻辑失效
        assertThat(JSON.toJSONString(BuildProgress.done("构建成功"))).startsWith("{");
        assertThat(JSON.toJSONString(BuildProgress.error("构建失败"))).startsWith("{");
    }

    @Test
    @DisplayName("字段名 SHALL 与前端 ProgressEvent 严格对应（ws 无契约守护，靠本测试钉住）")
    void fieldNames_matchFrontendContract() {
        // 前端：aicode-web/src/lib/realtime/progress-event.ts 的 ProgressEvent
        // 改字段名时两边必须同步 —— ws 不在 OpenAPI 范围内，只能靠测试与 review
        JSONObject done = JSON.parseObject(JSON.toJSONString(BuildProgress.done("x")));
        Set<String> keys = done.keySet();

        assertThat(keys).contains("type", "message");
        // current / total 是 int 基本类型，fastjson2 恒输出（即使为 0）
        assertThat(keys).contains("current", "total");
        assertThat(done.getIntValue("current")).isZero();
        assertThat(done.getIntValue("total")).isZero();
        // phase 是 String 且未赋值 → **fastjson2 默认省略 null 字段**（见 AGENTS §6.5），
        // 故构建的终态消息**不带 phase**。前端的 ProgressEvent.phase 是可选的（phase?），
        // 故这一形态是被支持的 —— 但若将来把 phase 改成必填，这里会先红。
        assertThat(keys).doesNotContain("phase");
    }

    @Test
    @DisplayName("BuildProgress SHALL 与 ProjectInitProgress 字段一致（协议族统一）")
    void fieldShape_matchesProjectInitProgress() {
        // design D2：字段一致是为了让前端一套分流逻辑处理两个任务域。
        // 若某侧改了字段名，两侧的类型会分叉，前端需要两套解析 —— 本测试是防线。
        Set<String> buildKeys = JSON.parseObject(JSON.toJSONString(BuildProgress.done("x"))).keySet();
        Set<String> initKeys =
                JSON.parseObject(JSON.toJSONString(com.aicode.project.vo.ProjectInitProgress.done("x")))
                        .keySet();

        assertThat(buildKeys).isEqualTo(initKeys);
    }

    // ================================================================
    // 阶段事件（2026-10-18-build-stage-progress）
    // ================================================================

    @Test
    @DisplayName("BuildProgress.progress SHALL 序列化出 type=progress 与阶段字段")
    void progress_carriesStageFields() {
        String json = JSON.toJSONString(
                BuildProgress.progress("downloadTemplate", 3, 7, "下载技术模板"));
        JSONObject obj = JSON.parseObject(json);

        assertThat(obj.getString("type")).isEqualTo("progress");
        assertThat(obj.getString("phase")).isEqualTo("downloadTemplate");
        assertThat(obj.getIntValue("current")).isEqualTo(3);
        assertThat(obj.getIntValue("total")).isEqualTo(7);
        assertThat(obj.getString("message")).isEqualTo("下载技术模板");
    }

    @Test
    @DisplayName("progress 的字段集 SHALL 与 ProjectInitProgress.progress 一致")
    void progress_fieldShapeMatchesInitProgress() {
        // 两侧刻意同形，前端一套 ProgressEvent 通吃 —— 任何一侧改字段都会让本测试红。
        Set<String> build = JSON.parseObject(
                JSON.toJSONString(BuildProgress.progress("p", 1, 2, "m"))).keySet();
        Set<String> init = JSON.parseObject(
                JSON.toJSONString(com.aicode.project.vo.ProjectInitProgress.progress("p", 1, 2, "m")))
                .keySet();

        assertThat(build).isEqualTo(init);
    }
}