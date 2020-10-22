/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.module.service;

import com.aicode.module.entity.ModuleFile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 模块文件信息
 *
 * @author hegaoye
 */
public interface ModuleFileService extends IService<ModuleFile> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ModuleFile>
     */
    List<ModuleFile> list(QueryWrapper<ModuleFile> queryWrapper, int offset, int limit);
}


