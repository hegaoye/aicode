/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.TempalteCategoryDAO;
import com.rzhkj.project.entity.TempalteCategory;
import com.rzhkj.project.service.TempalteCategorySV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class TempalteCategorySVImpl extends BaseMybatisSVImpl<TempalteCategory, Long> implements TempalteCategorySV {

    @Resource
    private TempalteCategoryDAO tempalteCategoryDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return tempalteCategoryDAO;
    }

}
