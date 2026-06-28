package com.aicode.project.ctrl;

import com.aicode.account.service.AccountService;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.frameworks.service.FrameworksService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.project.service.ProjectFramworkService;
import com.aicode.project.service.ProjectJobService;
import com.aicode.project.service.ProjectMapService;
import com.aicode.project.service.ProjectModuleService;
import com.aicode.project.service.ProjectRepositoryAccountService;
import com.aicode.project.service.ProjectService;
import com.aicode.project.service.ProjectSqlService;
import com.aicode.setting.service.SettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 回归测试：ProjectController 的 @Deprecated 端点（download / loadByCode）必须被恢复。
 *
 * <p>背景：feature/20260622/delta 在 remove-deprecated-unused-apis 变更中误删了
 * <pre>ProjectController.downloadFile</pre> 与
 * <pre>ProjectController.loadByCode</pre>，导致：
 * 1. <pre>GET /project/download/{name}</pre> 请求被 Spring 当作静态资源（找不到 → 9999）
 * 2. <pre>GET /project/load/code/{code}</pre> 同样问题</p>
 *
 * <p>本测试通过反射检查 controller 类的关键方法 + 注解存在，验证未来删除会触发测试失败。
 * （MockMvc 方式会因 mock service 返回 null 触发业务层 NPE，污染测试目标；反射方式
 * 聚焦"端点契约"而非"业务行为"。）</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ProjectControllerDownloadTest {

    @MockBean private ProjectService projectService;
    @MockBean private SettingService settingService;
    @MockBean private ProjectFramworkService projectFramworkService;
    @MockBean private ProjectMapService projectMapService;
    @MockBean private ProjectModuleService projectModuleService;
    @MockBean private ProjectJobService projectJobService;
    @MockBean private FrameworksService frameworksService;
    @MockBean private ProjectSqlService projectSqlService;
    @MockBean private ProjectRepositoryAccountService projectRepositoryAccountService;
    @MockBean private DisplayAttributeService displayAttributeService;
    @MockBean private MapRelationshipService mapRelationshipService;
    @MockBean private AccountService accountService;
    @MockBean private com.aicode.config.websocket.WebSocketConfig webSocketConfig;
    @MockBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    private Method findMethod(String name, Class<?>... paramTypes) {
        try {
            Method m = ProjectController.class.getDeclaredMethod(name, paramTypes);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Test
    @DisplayName("downloadFile 方法存在（防误删核心契约）")
    void downloadFile_methodExists() {
        Method m = findMethod("downloadFile", String.class, jakarta.servlet.http.HttpServletResponse.class);
        assertNotNull(m, "downloadFile(String, HttpServletResponse) 方法必须存在（生产环境核心下载端点）");
        assertTrue(java.lang.reflect.Modifier.isPublic(m.getModifiers()), "downloadFile 必须是 public");
    }

    @Test
    @DisplayName("loadByCode 方法存在（防误删兜底端点）")
    void loadByCode_methodExists() {
        Method m = findMethod("loadByCode", String.class);
        assertNotNull(m, "loadByCode(String) 方法必须存在（dev 分支保留的兜底端点）");
        assertEquals("com.aicode.project.vo.ProjectVO",
                m.getReturnType().getName(),
                "loadByCode 应返回 ProjectVO（与 dev 分支一致）");
    }

    @Test
    @DisplayName("downloadFile 标 @Deprecated 保留意图（前端未直接调用）")
    void downloadFile_isDeprecated() {
        Method m = findMethod("downloadFile", String.class, jakarta.servlet.http.HttpServletResponse.class);
        assertNotNull(m);
        assertNotNull(m.getAnnotation(Deprecated.class),
                "downloadFile 必须保留 @Deprecated 注解（与 dev 一致：标 @Deprecated 但不删）");
    }

    @Test
    @DisplayName("loadByCode 标 @Deprecated 保留意图")
    void loadByCode_isDeprecated() {
        Method m = findMethod("loadByCode", String.class);
        assertNotNull(m);
        assertNotNull(m.getAnnotation(Deprecated.class),
                "loadByCode 必须保留 @Deprecated 注解");
    }

    @Test
    @DisplayName("downloadFile 标 @GetMapping('/download/{projectName}')")
    void downloadFile_hasGetMapping() {
        Method m = findMethod("downloadFile", String.class, jakarta.servlet.http.HttpServletResponse.class);
        assertNotNull(m);
        org.springframework.web.bind.annotation.GetMapping gm =
                m.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
        assertNotNull(gm, "downloadFile 必须标 @GetMapping");
        assertTrue(gm.value().length > 0, "@GetMapping 必须有路径");
        assertTrue(gm.value()[0].contains("download"),
                "@GetMapping 路径必须含 'download'，实际=" + gm.value()[0]);
    }

    @Test
    @DisplayName("loadByCode 标 @GetMapping('/load/code/{code}')")
    void loadByCode_hasGetMapping() {
        Method m = findMethod("loadByCode", String.class);
        assertNotNull(m);
        org.springframework.web.bind.annotation.GetMapping gm =
                m.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
        assertNotNull(gm, "loadByCode 必须标 @GetMapping");
        assertTrue(gm.value().length > 0);
        assertTrue(gm.value()[0].contains("load/code"),
                "@GetMapping 路径必须含 'load/code'，实际=" + gm.value()[0]);
    }
}
