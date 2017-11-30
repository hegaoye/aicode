/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.MoudlesFilesDAO;
import com.rzhkj.project.entity.MoudlesFiles;
import com.rzhkj.project.service.MoudlesFilesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class MoudlesFilesSVImpl extends BaseMybatisSVImpl<MoudlesFiles, Long> implements MoudlesFilesSV {

    @Resource
    private MoudlesFilesDAO moudlesFilesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return moudlesFilesDAO;
    }

}
