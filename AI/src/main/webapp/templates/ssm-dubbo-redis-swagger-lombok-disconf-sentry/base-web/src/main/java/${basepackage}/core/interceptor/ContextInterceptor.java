/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.interceptor;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseCtrl;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.tools.CookieTools;
import ${basePackage}.core.tools.security.Md5;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class ContextInterceptor extends HandlerInterceptorAdapter {
    private final static Logger logger = LoggerFactory.getLogger(ContextInterceptor.class);

    /**
     * 此方法中返回值如果为false，则拦截器不会继续往下执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /*防乱码过滤*/
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
            String url = request.getServletPath() + pathInfo;
            // * 1.获得所有的请求参数<p/>
            Enumeration<String> param_names = request.getParameterNames();
            // * 2.单个获得参数然后进行value过滤处理
//            String params = "?";
            while (param_names.hasMoreElements()) {
                String param_name = param_names.nextElement();/*获得参数名称*/
                String[] values = request.getParameterValues(param_name); /*根据参数名称获得value值*/
//                params += param_name + "=" + JSON.toJSONString(values) + "&";
                for (int i = 0; i < values.length; i++) {
                    if (checkSQLInject(values[i], url)) {/*判断内容是否为空，不为空进行处理*/
                        errorResponse(response, param_name);    //返回异常信息
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        testMethodTimeStart();/*测试请求执行时间*/
        initBaseAction(request, response, handler);/*进入action设置*/
        testMethoTimeEnd();/*测试请求执行时间*/
        return true;
    }

    /**
     * 检查是否存在非法字符，防止SQL注入
     *
     * @param str 被检查的字符串
     * @return ture-字符串中存在非法字符，false-不存在非法字符
     */
    private boolean checkSQLInject(String str, String url) {
        if (StringUtils.isEmpty(str)) {
            return false;// 如果传入空串则认为不存在非法字符
        }

        // 判断黑名单
        String[] inj_stra = {"script", "master", "truncate", "insert", "select", "update", "declare", "alert", "atestu", "\\", "onload", "onmouseover", "onfocus", "onerror"};

        str = str.toLowerCase(); // sql不区分大小写

        for (int i = 0; i < inj_stra.length; i++) {
            if (str.indexOf(inj_stra[i]) >= 0) {
                logger.info("xss防攻击拦截url:" + url + "，原因：特殊字符，传入str=" + str + ",包含特殊字符：" + inj_stra[i]);
                return true;
            }
        }
        return false;
    }

    private void errorResponse(HttpServletResponse response, String paramNm) throws IOException {
        String warning = "输入项中不能包含非法字符。";

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.write(JSON.toJSONString(BeanRet.create(false, warning + ",fieldName:" + paramNm)));
    }

    /*测试方法执行时间开始*/
    private long startTime = 0;

    private void testMethodTimeStart() {
        startTime = System.currentTimeMillis();
    }

    private void testMethoTimeEnd() {
        SimpleDateFormat sp = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss:ms");
        long finalL = System.currentTimeMillis();
        long lastL = (System.currentTimeMillis() - startTime);

        Date startDate = new Date(startTime);
        Date finalDate = new Date(finalL);
        Date lastDate = new Date(lastL);
        logger.info("开始时间：" + sp.format(startDate));
        logger.info("结束时间：：" + sp.format(finalDate));
        logger.info("请求总时间为：" + sp.format(lastDate));
    }

    /*测试方法执行时间结束*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


    /*初始化BaseActon*/
    private void initBaseAction(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //取得action对象
            if (handlerMethod.getBean() instanceof BaseCtrl) {
                BaseCtrl action = (BaseCtrl) handlerMethod.getBean();
                //调用初始化函数
//                action.init(request, response);
            }
        }
    }


}