package com.aicode.config;

import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 百度 UID 注入
 */
@Configuration
public class BaiduUidConfig {

    @Bean
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }

    @Bean
    public DefaultUidGenerator uidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        DefaultUidGenerator defaultUidGenerator = new DefaultUidGenerator();
        defaultUidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        defaultUidGenerator.setTimeBits(29);
        defaultUidGenerator.setWorkerBits(21);
        defaultUidGenerator.setSeqBits(13);
        defaultUidGenerator.setEpochStr("2016-09-20");
        return defaultUidGenerator;
    }

}
