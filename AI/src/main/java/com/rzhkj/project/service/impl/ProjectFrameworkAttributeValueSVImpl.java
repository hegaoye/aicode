/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.FrameworksException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.FrameworkAttributeDAO;
import com.rzhkj.project.dao.ProjectFrameworkAttributeValueDAO;
import com.rzhkj.project.entity.FrameworkAttribute;
import com.rzhkj.project.entity.ProjectFrameworkAttributeValue;
import com.rzhkj.project.service.ProjectFrameworkAttributeValueSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Component
@Service
public class ProjectFrameworkAttributeValueSVImpl extends BaseMybatisSVImpl<ProjectFrameworkAttributeValue, Long> implements ProjectFrameworkAttributeValueSV {

    @Resource
    private ProjectFrameworkAttributeValueDAO projectFrameworkAttributeValueDAO;

    @Resource
    private FrameworkAttributeDAO frameworkAttributeDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectFrameworkAttributeValueDAO;
    }

    @Override
    public void save(ProjectFrameworkAttributeValue entity) throws BaseException {
        if (entity == null
                || StringTools.isEmpty(entity.getFrameworkCode())
                || StringTools.isEmpty(entity.getAttributeCode())
                || StringTools.isEmpty(entity.getProjectCode())
                || StringTools.isEmpty(entity.getAttributeValue())
                ) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new FrameworksException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("code", entity.getAttributeCode());
        FrameworkAttribute frameworkAttribute = frameworkAttributeDAO.load(map);

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setAttribute(frameworkAttribute.getAttribute());
        super.save(entity);
    }
}
