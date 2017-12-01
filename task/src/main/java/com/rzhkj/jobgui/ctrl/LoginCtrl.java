package com.rzhkj.jobgui.ctrl;

import com.google.common.collect.Maps;
import com.rzhkj.base.core.BeanRet;
import com.rzhkj.core.tools.security.Md5;
import com.rzhkj.sys.entity.SystemAccount;
import com.rzhkj.sys.service.SystemAccountSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
@RequestMapping("/login")
public class LoginCtrl extends BaseCtrl {
    private final static Logger logger = LoggerFactory.getLogger(LoginCtrl.class);

    @Resource
    private SystemAccountSV systemAccountSV;

    /**
     * 管理员登录
     *
     * @param account  账号
     * @param password 密码
     * @param response
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public BeanRet loginCheck(String account, String password, HttpServletResponse response) {
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("account", account);
            map.put("password", Md5.md5(password));
            map.put("state", SystemAccount.State.Activate.name());
            SystemAccount systemAccount = systemAccountSV.load(map);
            if (systemAccount == null) return BeanRet.create(false, "用户名/密码错误");

            //初始化登陆用户信息
//            createAccount(systemAccount, response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return BeanRet.create(false, "登录失败！");
        }
        return BeanRet.create(true, "登录成功");
    }

    //检测登录信息
    @RequestMapping("/check")
    @ResponseBody
    public BeanRet checkLogin() {
        BeanRet ret = new BeanRet();
        SystemAccount systemAccount = getAccount();    //取得当前登陆用户
        if (systemAccount == null) {
            ret.setInfo("未获取到登录用户信息");
        } else {
            ret.setInfo("获取登录用户信息成功");
            ret.setSuccess(true);
            ret.setData(systemAccount);
        }
        return ret;
    }

    //退出登录
    @RequestMapping("/loginOut")
    @ResponseBody
    public BeanRet loginOut() {
        invalidateSession();  //注销session
        return BeanRet.create(true, "成功！");
    }


}
