package com.aicode.core.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenTest {

    @Test
    @DisplayName("createToken(k,v) 生成的 token 验签通过")
    void roundTripWithDefaultSecret() {
        String token = JwtToken.createToken("accountCode", "1307912345678901234");
        assertNotNull(token, "createToken 必须返回非空 token");
        assertTrue(JwtToken.verifier(token), "签发的 token 自身必须能验签通过");
    }

    @Test
    @DisplayName("getTokenValue 能从合法 token 取出 claim")
    void getTokenValue_returnsClaim() {
        String token = JwtToken.createToken("accountCode", "abc-123");
        assertEquals("abc-123", JwtToken.getTokenValue(token, "accountCode"));
    }

    @Test
    @DisplayName("getTokenValue 缺失 claim 返回 null")
    void getTokenValue_missingClaim_returnsNull() {
        String token = JwtToken.createToken("accountCode", "abc-123");
        assertNull(JwtToken.getTokenValue(token, "noSuchKey"));
    }

    @Test
    @DisplayName("篡改的 token 验签失败")
    void tamperedToken_failsVerification() {
        String token = JwtToken.createToken("accountCode", "abc-123");
        // 在 token 末尾追加 1 字符即可触发签名不匹配
        String tampered = token + "x";
        assertFalse(JwtToken.verifier(tampered));
    }

    @Test
    @DisplayName("格式非法的 token 验签返回 false 而不抛错")
    void malformedToken_returnsFalse() {
        assertFalse(JwtToken.verifier("not.a.jwt"));
        assertFalse(JwtToken.verifier(""));
        assertFalse(JwtToken.verifier(null));
    }

    @Test
    @DisplayName("自定义密钥签发的 token 不能用默认密钥验签")
    void customSecret_separateNamespace() {
        String customSecret = "another-secret-key-2026";
        String token = JwtToken.createToken("k", "v", customSecret, 60L);
        assertTrue(JwtToken.verifier(token, customSecret), "相同密钥验签通过");
        assertFalse(JwtToken.verifier(token), "默认密钥不应验签通过");
    }

    @Test
    @DisplayName("极短过期时间（0 分钟）签发后立即过期")
    void expiredToken_failsVerification() throws InterruptedException {
        // 0 分钟过期 → expiresAt = now
        String token = JwtToken.createToken("k", "v", "test-secret", 0L);
        // 等到下一毫秒确保 token 已过期
        TimeUnit.MILLISECONDS.sleep(50);
        assertFalse(JwtToken.verifier(token, "test-secret"));
    }
}
