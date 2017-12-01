/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.MoudlesDAO;
import com.rzhkj.project.entity.Moudles;
import com.rzhkj.project.service.MoudlesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class MoudlesSVImpl extends BaseMybatisSVImpl<Moudles, Long> implements MoudlesSV {

    @Resource
    private MoudlesDAO moudlesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return moudlesDAO;
    }

}
