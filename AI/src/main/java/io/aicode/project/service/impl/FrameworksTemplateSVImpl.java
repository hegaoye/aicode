/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.dao.FrameworksTemplateDAO;
import io.aicode.project.entity.FrameworksTemplate;
import io.aicode.project.service.FrameworksTemplateSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworksTemplateSVImpl extends BaseMybatisSVImpl<FrameworksTemplate, Long> implements FrameworksTemplateSV {

    @Resource
    private FrameworksTemplateDAO frameworksTemplateDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworksTemplateDAO;
    }

}
