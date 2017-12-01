package com.rzhkj.core.enums;

/**
 * 模块定义
 * Created by lixin on 2017/6/7.
 */
public enum ModuleEnum {
    ELDER("用户模块"), UPLOAD("上传模块"), HEALTH("健康模块");
    public String module;

    ModuleEnum(String module) {
        this.module = module;
    }
}
