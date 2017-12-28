/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ModuleFileDAO;
import com.rzhkj.project.entity.ModuleFile;
import com.rzhkj.project.service.ModuleFileSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class MoudlesFilesSVImpl extends BaseMybatisSVImpl<ModuleFile, Long> implements ModuleFileSV {

    @Resource
    private ModuleFileDAO moduleFileDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return moduleFileDAO;
    }

}
