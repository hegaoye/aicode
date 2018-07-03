package ${basePackage}.core.base.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import ${basePackage}.core.entity.BeanRet;
import ${basePackage}.core.exceptions.BaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lixin 2016-08-03 11:27
 */
public class ExceptionHandler implements HandlerExceptionResolver {
    protected final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        String errorMsg = null;
        Map<String, Object> map = Maps.newHashMap();
        if (e != null) {
            if (e instanceof RuntimeException) {
                if (StringUtils.isNotBlank(e.getMessage())) {
                    if (e.getMessage().contains("{") && e.getMessage().contains("}")) {
                        errorMsg = e.getMessage().substring(e.getMessage().indexOf("{"), e.getMessage().indexOf("}") + 1);
                        map = JSON.parseObject(errorMsg, Map.class);
                    }
                } else {
                    errorMsg = BaseException.BaseExceptionEnum.Server_Error.toString();
                    map = JSON.parseObject(errorMsg, Map.class);
                }
            }
            logger.error(e.getMessage());
            e.printStackTrace();
        } else {
            errorMsg = BaseException.BaseExceptionEnum.Server_Error.toString();
            map = JSON.parseObject(errorMsg, Map.class);
        }

        try {
            response.setHeader("serverError", "ex");   //系统错误
            response.getWriter().write(JSON.toJSONString(BeanRet.create(false, "", map)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return new ModelAndView();
    }


}
