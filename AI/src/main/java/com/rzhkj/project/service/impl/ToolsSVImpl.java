/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ToolsException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ToolsDAO;
import com.rzhkj.project.entity.Tools;
import com.rzhkj.project.entity.ToolsStateEnum;
import com.rzhkj.project.service.ToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ToolsSVImpl extends BaseMybatisSVImpl<Tools, Long> implements ToolsSV {

    @Resource
    private ToolsDAO toolsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return toolsDAO;
    }


    @Override
    public void save(Tools entity) throws BaseException {
        if (entity == null
                || StringTools.isEmpty(entity.getDescription())
                || StringTools.isEmpty(entity.getName())
                || StringTools.isEmpty(entity.getToolsCategoryCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ToolsException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setState(ToolsStateEnum.Enable.name());
        super.save(entity);
    }
}
