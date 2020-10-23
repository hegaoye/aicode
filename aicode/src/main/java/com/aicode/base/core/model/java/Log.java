package com.aicode.base.core.model.java;

/**
 * Created by jiyan on 2018/9/17.
 */
public class Log {
    String logInfo;//日志信息
    Long lastTimeFileSize;//最后一次日志位置

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Long getLastTimeFileSize() {
        return lastTimeFileSize;
    }

    public void setLastTimeFileSize(Long lastTimeFileSize) {
        this.lastTimeFileSize = lastTimeFileSize;
    }
}
