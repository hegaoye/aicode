/*
 * demo
 */
package com.aicode.display.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aicode.display.entity.DisplayAttribute;

/**
 * 显示属性
 *
 * @author aicode
 */
public interface DisplayAttributeService extends IService<DisplayAttribute> {

    int countByProjectCode(String code);
}


