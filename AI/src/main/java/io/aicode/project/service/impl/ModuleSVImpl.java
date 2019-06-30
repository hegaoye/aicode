/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.exceptions.MoudlesException;
import io.aicode.base.tools.StringTools;
import io.aicode.project.dao.ModuleDAO;
import io.aicode.project.entity.Module;
import io.aicode.project.service.ModuleSV;
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
