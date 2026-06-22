/*
 * aicode
 */
package com.aicode.frameworks.service;

import com.aicode.core.tools.PasswordCrypto;
import com.aicode.frameworks.dao.FrameworksDAO;
import com.aicode.frameworks.dao.mapper.FrameworksMapper;
import com.aicode.frameworks.entity.Frameworks;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 框架技术池
 *
 * @author aicode
 */
@Slf4j
@Service
public class FrameworksServiceImpl extends ServiceImpl<FrameworksMapper, Frameworks> implements FrameworksService {

    @Autowired
    private FrameworksDAO frameworksDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(Frameworks entity) {
        entity.setId(uidGenerator.getUID());
        // 入库前加密（私有仓库凭据）。PasswordCrypto 幂等：已加密的串不会双重加密
        if (entity.getPassword() != null) {
            entity.setPassword(PasswordCrypto.encrypt(entity.getPassword()));
        }
        return super.save(entity);
    }

}


