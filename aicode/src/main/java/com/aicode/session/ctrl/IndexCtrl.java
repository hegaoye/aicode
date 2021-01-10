package com.aicode.session.ctrl;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@Api(value = "登陆控制器", tags = "登陆控制器")
public class IndexCtrl {

    @RequestMapping("/")
    public String index() {
        return "forward:index.html";
    }


}
