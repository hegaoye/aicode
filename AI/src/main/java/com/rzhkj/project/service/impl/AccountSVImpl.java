/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.project.dao.AccountDAO;
import com.rzhkj.project.entity.Account;
import com.rzhkj.project.service.AccountSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Component
@Service
public class AccountSVImpl extends BaseMybatisSVImpl<Account, Long> implements AccountSV {

    @Resource
    private AccountDAO accountDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return accountDAO;
    }

    @Override
    public void save(Account entity) throws BaseException {
        Map<String, Object> map = Maps.newHashMap();
        map.put("account", entity.getAccount());
        Account account = accountDAO.load(map);
        if (account != null) {
            throw new BaseException(BaseException.BaseExceptionEnum.Exists);
        }
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        super.save(entity);
    }
}
