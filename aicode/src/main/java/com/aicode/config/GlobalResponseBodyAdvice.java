package com.aicode.config;//package com.aicode.config;
//
//import com.aicode.core.BaseException;
//import com.aicode.core.R;
//import com.alibaba.fastjson2.JSON;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 统一响应结果
// *
// * @author hello50
// * @date 2021/1/14 12:18 下午
// */
//@Slf4j
//@ControllerAdvice
//public class GlobalResponseBodyAdvice implements ResponseBodyAdvice {
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class converterType) {
//        return true;
//    }
//
//    private String[] expact = {"/doc.html", "/webjars", "v3/api-docs", "swagger-resources", "res"};
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
//                                  ServerHttpRequest request, ServerHttpResponse response) {
//        String uri = request.getURI().getPath();
//        //filter 中异常拦截处理封装
//        if (body instanceof Map) {
//            Map<String, Object> map = (Map<String, Object>) body;
//            String msgKey = "message";
//            if (map.containsKey(msgKey)) {
//                R r = JSON.parseObject(map.get(msgKey).toString(), R.class);
//                return r;
//            }
//        }
//
//        for (String s : expact) {
//            if (uri.contains(s)) {
//                log.info("统一返回结果封装拦截器-{}", body);
//                return body;
//            }
//        }
//
//        //如果body为空，返回默认信息
//        if (body == null) {
//            log.info("统一返回结果封装拦截器-{}", body);
//            return R.success();
//        }
//        //匹配 R
//        if (body instanceof R) {
//            log.info("统一返回结果封装拦截器-{}", body);
//            return body;
//        }
//
//        //boolean 返回
//        if (body instanceof Boolean) {
//            Boolean flag = (Boolean) body;
//            log.info("统一返回结果封装拦截器-{}", body);
//            if (flag) {
//                return R.success();
//            } else {
//                return R.failed(BaseException.BaseExceptionEnum.Server_Error);
//            }
//
//        }
//
//        //分页转化
//        if (body instanceof IPage) {
//            IPage page = (IPage) body;
//            List list = page.getRecords();
//            if (!CollectionUtils.isEmpty(list)) {
//                List listAfterFilter = new ArrayList();
//                for (Object o : list) {
//                    listAfterFilter.add(o);
//                }
//                page.getRecords().clear();
//                page.getRecords().addAll(listAfterFilter);
//            }
//            log.info("统一返回结果封装拦截器-{}", body);
//            return R.success(body);
//        }
//
//        log.info("统一返回结果封装拦截器-{}", body);
//        return R.success(body);
//    }
//
//}