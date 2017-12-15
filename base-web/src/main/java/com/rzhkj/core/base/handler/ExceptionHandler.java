package com.rzhkj.core.base.handler;

import com.rzhkj.core.exceptions.BaseException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lixin 2016-08-03 11:27
 */
public class ExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        String errorMsg = null;
        if (e instanceof BaseException) {
            errorMsg = e.getMessage();
        } else {
            errorMsg = BaseException.PonddyException.Server_Error.toString();
        }
        try {
            response.setHeader("serverError", "ex");   //系统错误
            response.getWriter().write(errorMsg);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
        return new ModelAndView();
    }


}
