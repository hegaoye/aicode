package com.aicode.core.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 吸烟枪测试：clone 完整仓库（验证 CDS bug 已修复）。
 *
 * <p>背景：JGit 4.9.2 在 CDS archive 模式下，HTTP transport 用 sun.* 静态初始化器，
 * CDS archive 加载跳过这些类，导致 HTTP 连接中途 EOF，clone 只下载 1 个文件
 * （{@code tidb.yml}），而整个仓库实际有 30+ framework 子目录 + 数百个模板文件。</p>
 *
 * <p>修复后：JGit 6.6.0 + http.apache 子模块切到 Apache HttpClient 4.x transport，
 * clone 完整。这个测试断言 {@code listFiles().length > 5}，CDS bug 下永远 ≤ 1。</p>
 *
 * <p>需要网络访问 GitHub。CI 离线时通过 {@code -Dnetwork.tests=true} 显式启用。</p>
 */
@EnabledIfSystemProperty(named = "network.tests", matches = "true")
class GitToolsCloneCompletenessTest {

    /**
     * 完整 clone aicode-tamplate.git。CDS bug 下只下载 1 个文件（tidb.yml），
     * 修复后应至少 30+ framework 子目录（每个 framework 一个子目录）。
     */
    @Test
    void cloneRepoGetsAllFiles(@TempDir Path temp) throws Exception {
        File target = temp.resolve("aicode-tamplate").toFile();

        boolean ok = GitTools.cloneGit(
                "https://github.com/hegaoye/aicode-tamplate.git",
                target,
                null,
                null);

        assumeTrue(ok, "git clone 失败（可能网络问题）；本测试跳过而非失败");

        File[] rootEntries = target.listFiles();
        assertNotNull(rootEntries, "clone 成功后 target 应该是目录");

        // ★ 吸烟枪断言：CDS bug 下只 clone 出 1 个文件（tidb.yml）。
        // 修复后至少 30 个 framework 子目录（springcloud3.3.9-... 等）。
        assertTrue(rootEntries.length > 5,
                "CDS bug = only 1 file; 修复 = 30+ framework 子目录. 实际 rootEntries.length="
                        + rootEntries.length);

        // 关键子目录必须存在（demo09876 关联 framework id=34）
        File frameworkDir = new File(target, "springcloud3.3.9-mybatisplus-redis-java21");
        assertTrue(frameworkDir.isDirectory(),
                "framework 子目录应被 clone 下来；当前 path=" + frameworkDir.getAbsolutePath());

        // aicode.json 必须存在（决定 engine 选择的关键文件）
        File aicodeJson = new File(frameworkDir, "aicode.json");
        assertTrue(aicodeJson.exists() && aicodeJson.isFile(),
                "aicode.json 是决定 engine 选择的关键文件，必须 clone 下来；当前 path="
                        + aicodeJson.getAbsolutePath());
    }
}