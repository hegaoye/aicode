package com.aicode.session.ctrl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@Tag(name = "登陆控制器", description = "登陆控制器")
public class IndexCtrl {

    @Deprecated
    @RequestMapping("/")
    public String index() {
        return "forward:index.html";
    }

}
