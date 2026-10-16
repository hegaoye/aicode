package com.aicode.core.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 口令散列工具 —— BCrypt（带随机盐 + 自适应 cost）。
 *
 * <p><b>2026-10-18 起替代 {@link Md5} 用于账户口令</b>。原因是无盐 MD5 可用彩虹表 +
 * GPU 秒级爆破，而账户口令散列曾随 {@code GET /account/list} 的响应泄漏
 * （见 {@code openspec/specs/api-data-object/spec.md} 的凭据 Requirement），
 * 落盘在前端生成物与代理日志中，这类泄漏**不可追溯清理**。</p>
 *
 * <p><b>⚠️ 本类只适用于「只需校验、无需取回明文」的凭据</b>。</p>
 * <p>需要解密后重新使用的凭据（如 Git 仓库口令 —— 要 clone / push）
 * SHALL NOT 用本类，继续用 {@link PasswordCrypto} 的对称加密。</p>
 *
 * <p><b>存量兼容</b>：{@link #matches} 按散列前缀分派 —— 以 {@code $2} 开头（BCrypt
 * 的模块 crypt 规范固定 {@code $2a$} / {@code $2b$} / {@code $2y$}）走 BCrypt 校验，
 * 否则按 {@link Md5} 十六进制串校验。配合 {@link #needsUpgrade} 可在用户下次成功登录时
 * 把弱散列透明升级为 BCrypt。</p>
 *
 * <p><b>为什么校验必须离开 SQL</b>：带盐散列每次结果不同，相同口令两次散列不相等，
 * 所以 {@code WHERE password = ?} 这种等值匹配**在算法层面就不可行**；且它还会让
 * 散列值出现在 SQL 日志里。</p>
 */
public final class PasswordHasher {

    /** BCrypt 散列前缀 —— 模块 crypt 规范固定为 {@code $2} + 版本位。 */
    private static final String BCRYPT_PREFIX = "$2";

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordHasher() {
    }

    /**
     * 计算 BCrypt 散列（含随机盐）。
     *
     * @param raw 明文口令
     * @return 形如 {@code $2a$10$...} 的散列串
     */
    public static String hash(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("口令不能为 null");
        }
        return ENCODER.encode(raw);
    }

    /**
     * 校验口令，**兼容存量 MD5 散列**。
     *
     * @param raw    用户提交的口令明文
     * @param stored 库中存的散列（可能是 BCrypt，也可能是无盐 MD5）
     * @return 是否匹配
     */
    public static boolean matches(String raw, String stored) {
        if (raw == null || stored == null || stored.isEmpty()) {
            return false;
        }
        if (isBcrypt(stored)) {
            try {
                return ENCODER.matches(raw, stored);
            } catch (IllegalArgumentException e) {
                // 形似 BCrypt 但非法（脏数据）—— 按不匹配处理，SHALL NOT 抛异常导致 500
                return false;
            }
        }
        try {
            return Md5.md5(raw).equals(stored);
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 该散列是否需要升级为 BCrypt。
     *
     * <p><b>判据是「是否 BCrypt 前缀」，刻意不含 cost 阈值</b> —— 若把 cost 判断也写进来，
     * 「登录时是否改写」的行为会随配置漂移、难以推理。cost 调优是独立议题
     * （{@link #hash} 当前用 spring-security-crypto 的默认 cost）。</p>
     *
     * @param stored 库中存的散列
     * @return true 表示需要重算（如存的是无盐 MD5）
     */
    public static boolean needsUpgrade(String stored) {
        return stored == null || !isBcrypt(stored);
    }

    /**
     * 是否为 BCrypt 散列。
     *
     * @param stored 库中存的散列
     * @return true 表示是 BCrypt 格式
     */
    public static boolean isBcrypt(String stored) {
        return stored != null && stored.startsWith(BCRYPT_PREFIX);
    }
}