/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.MoudlesException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ModuleDAO;
import com.rzhkj.project.entity.Module;
import com.rzhkj.project.service.ModuleSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ModuleSVImpl extends BaseMybatisSVImpl<Module, Long> implements ModuleSV {

    @Resource
    private ModuleDAO moduleDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return moduleDAO;
    }

    @Override
    public void save(Module entity) throws BaseException {
        if (entity == null
                || StringTools.isEmpty(entity.getDescription())
                || StringTools.isEmpty(entity.getName())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new MoudlesException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        super.save(entity);
    }
}
