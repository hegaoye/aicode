package com.aicode.feign.fallback;

import com.aicode.feign.UidClient;
import org.springframework.stereotype.Component;

@Component
public class UidFallback implements UidClient {


    @Override
    public Long uid() {
        return null;
    }


}
