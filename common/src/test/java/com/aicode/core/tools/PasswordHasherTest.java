package com.aicode.core.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PasswordHasher} 的单测。
 *
 * <p><b>本类守护的是登录主链路</b> —— 改错会锁死所有人。故除正向用例外，
 * 重点覆盖「脏数据不抛异常」与「双格式兼容」两条。</p>
 *
 * <p>断言风格跟随 {@code common} 模块既有约定（JUnit 原生；该模块未引 AssertJ）。</p>
 *
 * @see PasswordHasher
 */
class PasswordHasherTest {

    // ---- BCrypt 基本行为 ----

    @Test
    @DisplayName("hash 产出以 $2 开头的 BCrypt 散列（模块 crypt 规范）")
    void hash_producesBcryptFormat() {
        String h = PasswordHasher.hash("admin-password");

        assertTrue(h.startsWith("$2"), "SHALL 是 BCrypt 格式，实际=" + h);
        assertTrue(PasswordHasher.isBcrypt(h));
        assertNotEquals("admin-password", h, "SHALL NOT 是明文");
    }

    @Test
    @DisplayName("同一口令散列两次 SHALL 不同（随机盐）")
    void hash_isSalted_soSamePasswordDiffers() {
        String a = PasswordHasher.hash("same-password");
        String b = PasswordHasher.hash("same-password");

        assertNotEquals(a, b,
                "带盐散列必须每次不同 —— 否则等同于无盐，等于没升级");
        // 但两者都能校验通过
        assertTrue(PasswordHasher.matches("same-password", a));
        assertTrue(PasswordHasher.matches("same-password", b));
    }

    @Test
    @DisplayName("matches：BCrypt 散列对正确口令为 true、错误口令为 false")
    void matches_bcrypt() {
        String h = PasswordHasher.hash("correct-password");

        assertTrue(PasswordHasher.matches("correct-password", h));
        assertFalse(PasswordHasher.matches("wrong-password", h));
        assertFalse(PasswordHasher.matches("", h));
    }

    // ---- 存量 MD5 兼容（透明升级的前提） ----

    @Test
    @DisplayName("matches：无盐 MD5 存量散列仍能校验通过（否则存量用户全部无法登录）")
    void matches_legacyMd5() {
        String legacy = Md5.md5("legacy-password");

        assertTrue(PasswordHasher.matches("legacy-password", legacy),
                "存量的无盐 MD5 必须继续可校验 —— 透明升级依赖这一点");
        assertFalse(PasswordHasher.matches("wrong-password", legacy));
    }

    // ---- needsUpgrade ----

    @Test
    @DisplayName("needsUpgrade：MD5 散列为 true、BCrypt 为 false")
    void needsUpgrade_detectsLegacyFormat() {
        assertTrue(PasswordHasher.needsUpgrade(Md5.md5("x")));
        assertFalse(PasswordHasher.needsUpgrade(PasswordHasher.hash("x")));
    }

    // ---- 脏数据不得导致 500 ----

    @Test
    @DisplayName("matches：null / 空 / 非法散列 SHALL 返回 false 而非抛异常")
    void matches_dirtyData_returnsFalseNotThrows() {
        String h = PasswordHasher.hash("x");
        assertFalse(PasswordHasher.matches(null, h));
        assertFalse(PasswordHasher.matches("x", null));
        assertFalse(PasswordHasher.matches("x", ""));
        assertFalse(PasswordHasher.matches(null, null));
        // 形似 BCrypt 但非法 —— BCryptPasswordEncoder.matches 本身会抛
        assertFalse(PasswordHasher.matches("x", "$2a$10$not-a-valid-hash"),
                "脏数据不得冒泡成 500，SHALL 按不匹配处理");
    }

    @Test
    @DisplayName("hash(null) SHALL 抛 IllegalArgumentException（写入路径的编程错误应显式暴露）")
    void hash_nullThrows() {
        assertEquals(IllegalArgumentException.class,
                assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null))
                        .getClass());
    }
}