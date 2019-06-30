/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.tools.Md5;
import io.aicode.project.dao.AccountDAO;
import io.aicode.project.entity.Account;
import io.aicode.project.service.AccountSV;
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
        entity.setPassword(Md5.md5(entity.getPassword()));
        super.save(entity);
    }
}
