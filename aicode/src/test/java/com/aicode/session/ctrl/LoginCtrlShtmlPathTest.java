package com.aicode.session.ctrl;

import com.aicode.account.service.AccountService;
import com.aicode.config.websocket.WebSocketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 回归测试：保证前端以 `.shtml` 后缀调用登录端点不会被 LoginInterceptor 错误拦截。
 *
 * <p>Bug 背景：feature/20260622/delta 在 P2-1 改动中挂载了 LoginInterceptor，但
 * `excludePathPatterns("/login/signin", "/login/reg")` 不匹配前端实际请求路径
 * `/login/signin.shtml`（带 .shtml 后缀），导致 token=空 时返回 9007 错误。
 * 修复方案：将排除路径改为 `/login/**`。</p>
 *
 * <p>本测试使用 @SpringBootTest + webAppContextSetup 加载完整 Spring 上下文
 * （含 LoginInterceptor 排除规则），验证 .shtml 路径能调到 LoginCtrl。</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class LoginCtrlShtmlPathTest {

    @Autowired
    private WebApplicationContext wac;

    @MockBean private AccountService accountService;
    @MockBean private WebSocketConfig webSocketConfig;
    @MockBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // 模拟"未匹配到账户"——LoginCtrl 走 R.success() 空 data 分支，返回 code="0000"
        when(accountService.getOne(any())).thenReturn(null);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @DisplayName("GET /login/signin.shtml（带 .shtml 后缀 + 空 token）：能调到 controller，返回 0000")
    void loginSigninShtml_emptyToken_reachesController() throws Exception {
        mockMvc.perform(get("/login/signin.shtml")
                        .header("x-requested-with", "XMLHttpRequest")
                        .param("account", "admin")
                        .param("password", "888888")
                        .param("token", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));
    }

    @Test
    @DisplayName("GET /login/signin（无后缀 + 空 token）：也能调到 controller，返回 0000")
    void loginSignin_emptyToken_reachesController() throws Exception {
        mockMvc.perform(get("/login/signin")
                        .header("x-requested-with", "XMLHttpRequest")
                        .param("account", "admin")
                        .param("password", "888888")
                        .param("token", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));
    }

    @Test
    @DisplayName("GET /login/signin/extra（子路径）：被排除，响应 code 不是 9007")
    void loginSigninSubpath_excluded() throws Exception {
        // /login/signin/anything 也应被 /login/** 排除
        // 即使路径不存在 controller 方法或缺参数，关键断言是响应 code ≠ "9007"
        // （如果是 LoginInterceptor 拦截，会写 R.failed(Session_Out) JSON）
        mockMvc.perform(get("/login/signin/anything")
                        .header("x-requested-with", "XMLHttpRequest")
                        .param("token", ""))
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    // LoginInterceptor 拦截会写入 {"code":"9007"}；这里必须不是它
                    org.junit.jupiter.api.Assertions.assertFalse(
                            body.contains("\"code\":\"9007\""),
                            "子路径不应被 LoginInterceptor 拦截（body 应不包含 9007）。实际: " + body);
                });
    }
}