package com.aicode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 占位 smoke test：验证 aicode 模块的 JUnit 5 测试基础设施已就绪。
 * 见 <a href="file:../../../../../../openspec/changes/add-core-business-tests/tasks.md">tasks.md §1.4</a>。
 */
class SmokeTest {

    @Test
    @DisplayName("aicode 模块 JUnit 5 测试基础设施就绪")
    void junit5_works() {
        assertTrue(true);
    }
}
