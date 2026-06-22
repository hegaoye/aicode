package com.aicode.core.tools;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 凭据加解密工具（AES/CBC/PKCS5Padding）。
 *
 * <p>设计目标：</p>
 * <ul>
 *   <li>不引新依赖，纯 JDK 实现</li>
 *   <li>密文统一前缀 {@code ENC:}，未带前缀视为明文（向后兼容旧数据）</li>
 *   <li>密钥通过 {@link #setKey(String)} 在应用启动时注入，缺省取 JVM 属性 {@code aicode.crypto.key}</li>
 *   <li>common 模块不依赖 Spring，密钥由 aicode 的 {@code CryptoConfig} 注入</li>
 * </ul>
 */
@Slf4j
public class PasswordCrypto {

    public static final String CIPHER_PREFIX = "ENC:";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static volatile String key;
    private static volatile byte[] keyBytes;

    /**
     * 注入对称密钥（应用启动时调用一次）。key 长度需为 16/24/32 字节（不足时右侧补 0x20）。
     */
    public static void setKey(String newKey) {
        if (newKey == null || newKey.isEmpty()) {
            log.warn("PasswordCrypto.setKey 收到空密钥，加解密将退化为回声");
            key = "";
            keyBytes = new byte[0];
            return;
        }
        key = newKey;
        byte[] raw = newKey.getBytes(StandardCharsets.UTF_8);
        int len = raw.length;
        if (len < 16) {
            byte[] padded = new byte[16];
            System.arraycopy(raw, 0, padded, 0, len);
            keyBytes = padded;
        } else if (len > 16 && len < 24) {
            byte[] padded = new byte[24];
            System.arraycopy(raw, 0, padded, 0, len);
            keyBytes = padded;
        } else if (len > 24 && len < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, len);
            keyBytes = padded;
        } else {
            byte[] padded = new byte[32];
            System.arraycopy(raw, 0, padded, 0, Math.min(len, 32));
            keyBytes = padded;
        }
    }

    private static byte[] resolveKey() {
        if (keyBytes == null) {
            // 兜底：从 JVM 属性读（仅用于 main 方法 / 测试场景）
            String fallback = System.getProperty("aicode.crypto.key", "");
            setKey(fallback);
        }
        return keyBytes;
    }

    /**
     * 加密明文。空串或 null 原样返回；已加密（带 {@link #CIPHER_PREFIX}）的串幂等返回。
     */
    public static String encrypt(String plain) {
        if (plain == null || plain.isEmpty()) {
            return plain;
        }
        if (plain.startsWith(CIPHER_PREFIX)) {
            return plain;
        }
        byte[] keyBytes = resolveKey();
        if (keyBytes == null || keyBytes.length == 0) {
            log.warn("未配置加密密钥，跳过加密（明文返回）。生产请配置 aicode.crypto.key");
            return plain;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            // AES/CBC 要求 IV 固定 16 字节。从 key 的前 16 字节派生 IV
            // （避免在密文中额外存 IV；对短凭据足够，碰撞窗口可控）
            IvParameterSpec iv = new IvParameterSpec(first16(keyBytes));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] cipherBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return CIPHER_PREFIX + Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            log.error("加密失败，原文返回", e);
            return plain;
        }
    }

    /**
     * 解密。已解密（不带 {@link #CIPHER_PREFIX}）的串幂等返回。
     * 解密失败回退原文（不抛错），让旧数据/脏数据不阻塞业务流程。
     */
    public static String decrypt(String cipherOrPlain) {
        if (cipherOrPlain == null || cipherOrPlain.isEmpty()) {
            return cipherOrPlain;
        }
        if (!cipherOrPlain.startsWith(CIPHER_PREFIX)) {
            return cipherOrPlain;
        }
        byte[] keyBytes = resolveKey();
        if (keyBytes == null || keyBytes.length == 0) {
            log.warn("未配置加密密钥，无法解密；按密文返回");
            return cipherOrPlain;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(first16(keyBytes));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] cipherBytes = Base64.getDecoder().decode(cipherOrPlain.substring(CIPHER_PREFIX.length()));
            return new String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("解密失败，按原文返回。原文可能本来就是非密文字符串", e);
            return cipherOrPlain;
        }
    }

    private static byte[] first16(byte[] src) {
        byte[] out = new byte[16];
        System.arraycopy(src, 0, out, 0, Math.min(src.length, 16));
        return out;
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(CIPHER_PREFIX);
    }

    public static String getKey() {
        return key;
    }
}
