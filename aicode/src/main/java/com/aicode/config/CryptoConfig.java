package com.aicode.config;

import com.aicode.core.tools.PasswordCrypto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 凭据加密密钥引导。
 * <p>应用启动时从 {@code aicode.crypto.key}（支持 JVM 属性 / 环境变量 / application.yml 注入）
 * 读取对称密钥并注入到 {@link PasswordCrypto}。生产应通过
 * {@code export AICODE_CRYPTO_KEY=...} 或 JVM 参数 {@code -Daicode.crypto.key=...} 提供，
 * 避免在 application.yml 中以明文落盘。</p>
 */
@Slf4j
@Configuration
public class CryptoConfig {

    @Value("${aicode.crypto.key:}")
    private String cryptoKey;

    @PostConstruct
    public void init() {
        if (cryptoKey == null || cryptoKey.isEmpty()) {
            // 从 JVM 属性 / 环境变量兜底
            String fromProp = System.getProperty("aicode.crypto.key",
                    System.getenv("AICODE_CRYPTO_KEY"));
            if (fromProp != null && !fromProp.isEmpty()) {
                cryptoKey = fromProp;
            }
        }
        if (cryptoKey == null || cryptoKey.isEmpty()) {
            log.warn("未配置 aicode.crypto.key，凭据将以明文落库（仅供开发）。生产必须显式配置");
            cryptoKey = "";
        } else {
            log.info("PasswordCrypto 密钥已加载（长度 {}）", cryptoKey.length());
        }
        PasswordCrypto.setKey(cryptoKey);
    }
}
