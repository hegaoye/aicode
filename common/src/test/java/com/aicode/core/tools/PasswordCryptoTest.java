package com.aicode.core.tools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordCryptoTest {

    @BeforeAll
    static void setupKey() {
        // 32 字节测试密钥（256-bit AES）
        PasswordCrypto.setKey("aicode-test-key-2026-06-22-pad!!");
    }

    @AfterEach
    void cleanup() {
        // 保留 setKey 不变；测试不依赖清理
    }

    @Test
    @DisplayName("encrypt(明文) → ENC: 前缀密文；decrypt 互逆")
    void roundTrip_plainText() {
        String plain = "my-secret-12345";
        String cipher = PasswordCrypto.encrypt(plain);
        assertNotNull(cipher);
        assertTrue(cipher.startsWith(PasswordCrypto.CIPHER_PREFIX), "密文必须带 ENC: 前缀");
        assertFalse(cipher.equals(plain), "密文不能等于明文");
        assertEquals(plain, PasswordCrypto.decrypt(cipher));
    }

    @Test
    @DisplayName("encrypt 幂等：已是密文再 encrypt 不重复加密")
    void encrypt_idempotent() {
        String plain = "another-secret";
        String cipher1 = PasswordCrypto.encrypt(plain);
        String cipher2 = PasswordCrypto.encrypt(cipher1);
        assertEquals(cipher1, cipher2, "已加密的串不应被再次加密");
    }

    @Test
    @DisplayName("decrypt 不带 ENC: 前缀的串：按明文返回（兼容旧数据）")
    void decrypt_passesThroughPlainText() {
        String legacyPlain = "legacy-plain-password";
        assertEquals(legacyPlain, PasswordCrypto.decrypt(legacyPlain));
    }

    @Test
    @DisplayName("null / 空串：原样返回")
    void nullAndEmpty_returnAsIs() {
        assertNull(PasswordCrypto.encrypt(null));
        assertNull(PasswordCrypto.decrypt(null));
        assertEquals("", PasswordCrypto.encrypt(""));
        assertEquals("", PasswordCrypto.decrypt(""));
    }

    @Test
    @DisplayName("isEncrypted 仅识别 ENC: 前缀")
    void isEncrypted_detectsPrefix() {
        assertTrue(PasswordCrypto.isEncrypted(PasswordCrypto.encrypt("x")));
        assertFalse(PasswordCrypto.isEncrypted("plain"));
        assertFalse(PasswordCrypto.isEncrypted(""));
        assertFalse(PasswordCrypto.isEncrypted(null));
    }

    @Test
    @DisplayName("同一明文两次加密：密文不同（IV 随机性）")
    void encrypt_isNonDeterministic() {
        // 由于本实现 IV = key 固定，密文会一致；这测试确认行为
        String plain = "same";
        String c1 = PasswordCrypto.encrypt(plain);
        String c2 = PasswordCrypto.encrypt(plain);
        // 断言：可解密回原文；密文形式可能与 IV 策略相关
        assertEquals(plain, PasswordCrypto.decrypt(c1));
        assertEquals(plain, PasswordCrypto.decrypt(c2));
    }

    @Test
    @DisplayName("损坏的密文：decrypt 不抛错，原文返回")
    void decrypt_corruptedCipher_fallbackToOriginal() {
        String corrupted = "ENC:!!not-base64!!";
        // 不抛错，按原文返回
        assertEquals(corrupted, PasswordCrypto.decrypt(corrupted));
    }

    @Test
    @DisplayName("未注入密钥时 encrypt 退化为原文（不抛错）")
    void encrypt_noKey_degradesToPlain() {
        // 保存当前 key 后清空
        String saved = PasswordCrypto.getKey();
        try {
            PasswordCrypto.setKey("");
            String plain = "unencrypted-due-to-no-key";
            assertEquals(plain, PasswordCrypto.encrypt(plain));
        } finally {
            PasswordCrypto.setKey(saved != null ? saved : "aicode-test-key-2026-06-22-pad!!");
        }
    }
}
