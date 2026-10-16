package com.aicode.project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 构建阶段上报的**静态契约**守护（2026-10-18-build-stage-progress）。
 *
 * <p><b>为什么需要它</b>：前端用例是<b>自己往 ws 投递消息</b>的（mock 掉 {@code ws-client}），
 * <b>不经过 Java</b> —— 所以前端测试<b>抓不到</b>「某个阶段忘了上报」这类后端缺陷。
 * 本测试直接读 {@code GeneratorSVImpl} 源码，补上这个盲区。</p>
 *
 * <p>用<b>源码文本</b>而非反射的原因：阶段上报是「写死的 7 行调用」，反射拿不到它们；
 * 而源码文本的契约（本变更的 7 个 phase + 序号）正是我们要守住的东西 ——
 * 它比运行时反射更直接地表达意图。</p>
 */
class GeneratorStageProgressContractTest {

    /** 7 个步骤的稳定标识与序号（design D1：phase 是稳定英文标识，NOT 文案）。 */
    private static final String[][] EXPECTED_STAGES = {
            {"buildProject", "1"},
            {"convertModel", "2"},
            {"downloadTemplate", "3"},
            {"generateSource", "4"},
            {"readModule", "5"},
            {"versionControl", "6"},
            {"packageZip", "7"},
    };

    private static final Pattern REPORT_CALL =
            Pattern.compile("reportStage\\(\"(\\w+)\",\\s*(\\d+)");

    private static String readSource() throws Exception {
        // 测试运行时的工作目录是 aicode/ 模块根，故源码在 src/main/java 下
        Path p = Path.of("src", "main", "java", "com", "aicode", "project", "service",
                "GeneratorSVImpl.java");
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    /** 只截取 `aiCode` 方法体 —— `reportStage` 方法自身的定义不含调用。 */
    private static String aiCodeBody(String src) {
        int from = src.indexOf("public void aiCode");
        assertTrue(from > 0, "应能找到 aiCode 方法");
        int to = src.indexOf("WSClientManager.sendMessage(\"End\");", from);
        assertTrue(to > from, "应能找到 aiCode 的结尾锚点");
        return src.substring(from, to);
    }

    @Test
    @DisplayName("aiCode SHALL 恰好上报 7 个阶段，phase 与序号与契约一致")
    void aiCode_reportsSevenStagesInOrder() throws Exception {
        String body = aiCodeBody(readSource());
        List<String> actual = new ArrayList<>();
        Matcher m = REPORT_CALL.matcher(body);
        while (m.find()) {
            actual.add(m.group(1) + "/" + m.group(2));
        }

        List<String> expected = new ArrayList<>();
        for (String[] s : EXPECTED_STAGES) {
            expected.add(s[0] + "/" + s[1]);
        }

        assertEquals(expected, actual,
                "阶段序列 SHALL 与契约完全一致（7 步、按序、序号连续）。"
                        + "少一个阶段 = 用户在某一步看不到任何进度反馈；"
                        + "phase 用的是稳定标识，SHALL NOT 改成会随文案变化的字符串");
    }

    @Test
    @DisplayName("阶段序号 SHALL 从 1 连续递增到 7（SHALL NOT 有跳号或重复）")
    void stageNumbers_areContinuous() throws Exception {
        String body = aiCodeBody(readSource());
        Matcher m = REPORT_CALL.matcher(body);
        List<Integer> numbers = new ArrayList<>();
        while (m.find()) {
            numbers.add(Integer.parseInt(m.group(2)));
        }

        List<Integer> expected = List.of(1, 2, 3, 4, 5, 6, 7);
        assertEquals(expected, numbers, "序号 SHALL 连续 1..7 —— 跳号会让「第 N/7 步」的 N 不可信");
    }

    @Test
    @DisplayName("STAGE_TOTAL SHALL 为 7 且与阶段数一致")
    void stageTotal_matchesStageCount() throws Exception {
        String src = readSource();
        Matcher total = Pattern.compile("STAGE_TOTAL\\s*=\\s*(\\d+)").matcher(src);
        assertTrue(total.find(), "应能找到 STAGE_TOTAL 定义");
        assertEquals(EXPECTED_STAGES.length, Integer.parseInt(total.group(1)),
                "STAGE_TOTAL SHALL 等于阶段数 —— 否则前端进度条的 max 与实际步数不符");

        String body = aiCodeBody(src);
        int callCount = 0;
        Matcher m = REPORT_CALL.matcher(body);
        while (m.find()) {
            callCount++;
        }
        assertEquals(Integer.parseInt(total.group(1)), callCount,
                "上报次数 SHALL 等于 STAGE_TOTAL");
    }

    @Test
    @DisplayName("每个阶段上报 SHALL 带可读文案（否则面板只能显示 phase 标识）")
    void everyStage_hasHumanReadableMessage() throws Exception {
        String body = aiCodeBody(readSource());
        Matcher m = Pattern.compile("reportStage\\(\"\\w+\",\\s*\\d+,\\s*\"([^\"]+)\"\\)").matcher(body);
        List<String> messages = new ArrayList<>();
        while (m.find()) {
            messages.add(m.group(1));
        }
        assertEquals(EXPECTED_STAGES.length, messages.size(),
                "每个阶段 SHALL 有文案，实际=" + messages);
        for (String msg : messages) {
            assertTrue(!msg.isBlank() && !msg.equals("null"),
                    "文案 SHALL 非空且 SHALL NOT 是字面量 \"null\"，实际=" + msg);
        }
    }
}