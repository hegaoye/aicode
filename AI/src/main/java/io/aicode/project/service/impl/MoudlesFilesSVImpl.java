/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ModuleFileDAO;
import io.aicode.project.entity.ModuleFile;
import io.aicode.project.service.ModuleFileSV;
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
