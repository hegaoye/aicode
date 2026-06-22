package com.aicode.core.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Md5Test {

    @Test
    @DisplayName("MD5(888888) 产生稳定的 32 位十六进制摘要")
    void md5_ofAdminPassword_isStableHex() {
        String first = Md5.md5("888888");
        String second = Md5.md5("888888");

        assertNotNull(first);
        assertEquals(32, first.length(), "MD5 hex 长度应为 32");
        assertTrue(first.matches("[0-9a-f]{32}"), "MD5 输出应只含小写十六进制字符");
        assertEquals(first, second, "相同输入必须产生相同摘要");
    }

    @Test
    @DisplayName("MD5(admin) 与 MD5(888888) 不同")
    void md5_distinguishesDifferentInputs() {
        assertEquals(false, Md5.md5("admin").equals(Md5.md5("888888")));
    }

    @Test
    @DisplayName("MD5(空字符串) 仍返回 32 位 hex")
    void md5_ofEmptyString_returns32Hex() {
        String md5 = Md5.md5("");
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }

    @Test
    @DisplayName("MD5 与 JDK MessageDigest 一致")
    void md5_matchesJdkDigest() {
        // 来自 JDK: MessageDigest.getInstance("MD5").digest("hello".getBytes(UTF_8)) 的预期 hex
        String expected = "5d41402abc4b2a76b9719d911017c592";
        assertEquals(expected, Md5.md5("hello"));
    }
}
