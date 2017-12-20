/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.FrameworksConfigureTemplateException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.FrameworksConfigureTemplateDAO;
import com.rzhkj.project.entity.FrameworksConfigureTemplate;
import com.rzhkj.project.entity.FrameworksConfigureTemplateStateEnum;
import com.rzhkj.project.service.FrameworksConfigureTemplateSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworksConfigureTemplateSVImpl extends BaseMybatisSVImpl<FrameworksConfigureTemplate, Long> implements FrameworksConfigureTemplateSV {

    @Resource
    private FrameworksConfigureTemplateDAO frameworksConfigureTemplateDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworksConfigureTemplateDAO;
    }

    @Override
    public void save(FrameworksConfigureTemplate entity) throws BaseException {
        if (entity == null
                || StringTools.isEmpty(entity.getDescription())
                || StringTools.isEmpty(entity.getName())
                || StringTools.isEmpty(entity.getFrameworksCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new FrameworksConfigureTemplateException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setState(FrameworksConfigureTemplateStateEnum.Enable.name());
        super.save(entity);
    }

}
