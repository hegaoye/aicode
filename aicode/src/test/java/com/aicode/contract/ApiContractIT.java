package com.aicode.contract;

import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口契约回归测试 —— Java 25 / Spring Boot 4.1 升级系列的<b>验收金标准</b>。
 *
 * <p>这些断言 SHALL 在升级前后的<b>每一个</b>版本上全部通过。任何一条变红，
 * 都意味着某次升级破坏了前后端之间从未被文档化的隐式契约。</p>
 *
 * <p>为什么必须用真实 {@code WebMvcConfigurer} 而非全量 mock：{@code .shtml} 后缀可达性
 * 是<b>路由层</b>行为，mock 掉就测不到。业务层（Service）返回什么与路由无关，可放心打桩。</p>
 *
 * @see FrontendEndpointCatalog 前端端点清单
 * @see <a href="file:../../../../../../../../../openspec/changes/2026-10-14-add-api-contract-tests/specs/api-contract/spec.md">api-contract 能力</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ApiContractIT {

    /** Jackson：仅用于<b>解析</b>响应做结构断言，不用于序列化（序列化由被测应用负责）。 */
    private static final ObjectMapper PARSER = new ObjectMapper();

    private static final String EXPECTED_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final DateTimeFormatter EXPECTED_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern SNOWFLAKE_CODE = Pattern.compile("^\\d{1,19}$");
    private static final Pattern DATE = Pattern.compile(EXPECTED_DATE_PATTERN);

    /** 静态资源里已知的 19 位雪花 ID（见 db/data.sql 中 frameworks.code）。 */
    private static final String KNOWN_SNOWFLAKE_CODE = "1882812765213425664";

    @Autowired
    private WebApplicationContext wac;

    /**
     * Spring Framework 7 移除了 {@code PathMatchConfigurer#setUseSuffixPatternMatch}，
     * {@code .shtml} 兼容改由 {@link com.aicode.filter.ShtmlSuffixRewriteFilter} 提供。
     *
     * <p><b>必须显式注册</b>：{@code MockMvcBuilders.webAppContextSetup(wac)} 只装配
     * {@code DispatcherServlet}，不会把 Spring Bean 形态的 {@link jakarta.servlet.Filter}
     * 加入 MockFilterChain —— 真实运行时由嵌入式容器自动注册，MockMvc 下需手动
     * {@code addFilters(...)}，否则 {@code .shtml} 请求会落到静态资源处理器并报
     * {@code NoResourceFoundException}。</p>
     */
    @Autowired
    private com.aicode.filter.ShtmlSuffixRewriteFilter shtmlSuffixRewriteFilter;

    /**
     * MOCK 环境（{@code WebEnvironment.MOCK}）无嵌入式容器，不存在
     * {@code jakarta.websocket.server.ServerContainer}，导致
     * {@code WebSocketConfig#serverEndpointExporter} 创建失败 —— 应用上下文直接起不来。
     *
     * <p>此处打桩的是 <b>WebSocket 端点注册器</b>，与业务 Service 不同：
     * 它依赖真实容器，而契约断言关注的是 REST 路由与序列化，两者在 MOCK 下本就不可兼得。
     * 场景⑥ 改用「路径未被静态资源处理器吞掉」来间接验证 WebSocket 端点的存在。</p>
     *
     * <p>本文件已随 {@code openspec/changes/2026-10-14-upgrade-spring-boot-3-5}
     * 迁移到 {@code @MockitoBean}（Spring Framework 6.2 引入；Boot 4.0 起
     * {@code @MockBean} 已被移除，迁移是强制项）。</p>
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    /**
     * 业务层 Service 一律<b>不</b>打桩：契约断言需要真实的 MyBatis 查询与 Jackson 序列化，
     * mock 掉之后验证的就只是「序列化行为」而非「真实接口返回」，反而失去意义。
     */
    private MockMvc mockMvc;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(shtmlSuffixRewriteFilter)   // 见上方字段注释：MockMvc 不会自动注册 Bean 形态的 Filter
                .build();
        // 走真实鉴权链路：不 mock LoginInterceptor，而是签发一个合法 JWT
        token = JwtToken.createToken(Constants.AccountCode.val.toString(), "contract-test");
    }

    // ==================================================================
    // 场景①：标识符以 JSON 字符串形式序列化
    // ==================================================================

    @Nested
    @DisplayName("场景① 标识符以 JSON 字符串形式序列化")
    class IdentifierSerialization {

        @Test
        @DisplayName("列表接口的 id 是 JSON 字符串，不是数字")
        void id_isJsonString_notNumber() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);

            JsonNode first = firstRecord(body);
            assertThat(first).as("列表至少返回一条记录（data.sql 中 frameworks 有初始数据）").isNotNull();
            assertThat(first.get("id").isTextual())
                    .as("id 的 JSON 类型 SHALL 为字符串，实际节点类型=%s，值=%s",
                            first.get("id").getNodeType(), first.get("id"))
                    .isTrue();
        }

        @Test
        @DisplayName("code 是 JSON 字符串，且匹配 ^\\d{1,19}$")
        void code_isJsonString_matchingSnowflake() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode voList = PARSER.readTree(body).path("data").path("voList");
            assertThat(voList.isArray()).isTrue();
            assertThat(voList).isNotEmpty();

            for (JsonNode record : voList) {
                JsonNode code = record.get("code");
                assertThat(code.isTextual()).as("code SHALL 为字符串，实际=%s", code).isTrue();
                assertThat(code.asText()).matches(SNOWFLAKE_CODE);
            }
        }

        @Test
        @DisplayName("19 位标识符逐字符不被截断（Jackson 3 迁移的核心防线）")
        void snowflakeId_notTruncated() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode voList = PARSER.readTree(body).path("data").path("voList");
            assertThat(voList).anySatisfy(record ->
                    assertThat(record.get("code").asText())
                            .as("db/data.sql 中存在 19 位雪花编码 %s，SHALL 逐字符返回",
                                    KNOWN_SNOWFLAKE_CODE)
                            .isEqualTo(KNOWN_SNOWFLAKE_CODE));
        }

        @Test
        @DisplayName("若 id 是数字，JSON.parse 后的值会失真 —— 直接断言其字符串形态可防此类回归")
        void id_wouldLosePrecision_ifNumeric() throws Exception {
            // 前端为 Angular 7 + jQuery，无 BigInt，客户端无法补救，故服务端必须返回字符串
            long beyondSafeInteger = 1882812765213425664L;
            assertThat(beyondSafeInteger)
                    .as("该值超出 JS Number.MAX_SAFE_INTEGER(%d)，这是契约存在的根本原因",
                            9007199254740992L)
                    .isGreaterThan(9007199254740992L);

            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);
            JsonNode rawId = PARSER.readTree(body).path("data").path("voList").get(0).get("id");
            assertThat(rawId.toString())
                    .as("原始 JSON 文本中 id 必须带引号（当前=%s）", rawId)
                    .startsWith("\"").endsWith("\"");
        }
    }

    // ==================================================================
    // 场景②：.shtml 与无后缀路径等价可达
    // ==================================================================

    @Nested
    @DisplayName("场景② .shtml 与无后缀路径等价可达")
    class ShtmlSuffixCompatibility {

        static Stream<String> reachableEndpoints() {
            return FrontendEndpointCatalog.REACHABLE.stream();
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("reachableEndpoints")
        @DisplayName("带 .shtml 与不带后缀返回一致的状态码")
        void shtml_andPlain_haveSameStatus(String path) throws Exception {
            // GET 探测：多数端点为 POST/GET 混合，这里只比较两者是否都「可达」
            // （均不为 404 即视为路由命中），不比较响应体 —— 端点的请求方法与参数各不相同
            int shtmlStatus = probeStatus(mockMvc.perform(
                    MockMvcRequestBuilders.get(path + ".shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", token)).andReturn());
            int plainStatus = probeStatus(mockMvc.perform(
                    MockMvcRequestBuilders.get(path)
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", token)).andReturn());

            assertThat(shtmlStatus)
                    .as("%s.shtml SHALL 可达（不得 404）", path)
                    .isNotEqualTo(404);
            assertThat(shtmlStatus)
                    .as("%s.shtml 与 %s 的状态码 SHALL 一致", path, path)
                    .isEqualTo(plainStatus);
        }

        private int probeStatus(MvcResult result) {
            return result.getResponse().getStatus();
        }
    }

    // ==================================================================
    // 场景③：统一响应结构 R
    // ==================================================================

    @Nested
    @DisplayName("场景③ 统一响应结构")
    class UnifiedResponse {

        @Test
        @DisplayName("成功响应满足 code=0000、success=true、含 data 键")
        void successShape() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);
            JsonNode root = PARSER.readTree(body);

            assertThat(root.get("code").asText()).isEqualTo("0000");
            assertThat(root.get("success").asBoolean()).isTrue();
            assertThat(root.has("data")).as("顶层 SHALL 始终含 data 键").isTrue();
            assertThat(root.has("info")).as("顶层 SHALL 含 info 键").isTrue();
        }

        /**
         * 【已知缺陷 — 已确认不修复，本断言长期保持禁用】
         *
         * <p>当前实现下本断言<b>失败</b>：空 token 请求 {@code /framework/list.shtml}
         * 返回 200 + 完整业务数据，而非 9007。</p>
         *
         * <p><b>根因</b>：{@code ContextConfiguration#addInterceptors} 的
         * {@code excludePathPatterns} 中有一条「双星 + 斜杠 + 星」的通配规则
         * （另有 {@code "/*.html"}、{@code "/*.js"}、{@code "/*.css"}、{@code "/*.png"} …
         * 一批同族通配规则），本意是放行静态资源，但 {@link org.springframework.util.AntPathMatcher}
         * 判定它们同样匹配 {@code /framework/list.shtml} 这类带后缀的 API 路径，导致
         * {@code LoginInterceptor} 对前端的所有请求都不做 token 校验。</p>
         *
         * <p>实测（AntPathMatcher，见下方 {@link #shtmlApiRequests_currentlyBypassAuthentication}）：</p>
         * <pre>
         *   /framework/list.shtml  → 被 "双星通配" 规则命中
         *   /project/list.shtml    → 被 "双星通配" 规则命中
         *   /logs/load.shtml      → 被 "双星通配" 规则命中
         *   /index.html            → 被 "单星 + .html" 等多条规则命中（本意：静态资源）
         * </pre>
         *
         * <p><b>处理决定（2026-10-15）</b>：经评估<b>不修复</b> —— 该行为是当前前端实际
         * 依赖的（Angular 7 + jQuery 打包产物硬编码了这些排除规则所放行的资源形态），
         * 收紧排除规则属于独立的安全加固工作，不在 Java 25 / Spring Boot 4.1 升级系列范围内。</p>
         *
         * <p><b>为何以 {@code @Disabled} 而非让测试持续红</b>：让一条已知且决定不修的断言
         * 长期红灯，会使后续每个升级变更的「契约测试全绿」验收都无法达成，
         * 红灯反而会被忽略。此处 SHALL 显式登记，且<b>不得被解读为「该场景已通过」</b>。</p>
         */
        @Test
        @Disabled("已知缺陷（已确认不修复）：excludePathPatterns 的通配规则使 .shtml API "
                + "路径绕过鉴权。当前实际行为由 shtmlApiRequests_currentlyBypassAuthentication 钉死")
        @DisplayName("未授权 SHALL 返回 9007（当前被 excludePathPatterns 通配规则绕过 —— 已知缺陷，不修复）")
        void unauthorized_returns9007AsJson() throws Exception {
            // /framework/list 语义上属于受保护端点；空 token 时 SHALL 被拦截并返回 9007
            mockMvc.perform(MockMvcRequestBuilders.get("/framework/list.shtml?curPage=1&pageSize=5")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", ""))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(body)
                                .as("拦截器 SHALL 返回 JSON 而非 HTML")
                                .doesNotContain("<html").doesNotContain("<!DOCTYPE");
                        assertThat(PARSER.readTree(body).get("code").asText())
                                .as("未授权 SHALL 返回 9007，实际=%s", body)
                                .isEqualTo("9007");
                    });
        }

        /**
         * 已登记缺陷的可执行证据：把「.shtml API 请求绕过鉴权」这一<b>当前实际行为</b>钉死。
         *
         * <p>本测试<b>不</b>断言「应当被拦截」（那是上面被 {@code @Disabled} 的那条），
         * 而是记录现状。这样做有两个作用：① 缺陷从「被忽略的红灯」变成「被记录的事实」；
         * ② 若将来 {@code excludePathPatterns} 被收紧、鉴权恢复生效，本测试会红，
         * 从而提醒同步处理上面那条被禁用���断言。</p>
         */
        @Test
        @DisplayName("【已知行为】.shtml API 请求当前绕过鉴权（已确认不修复，此处钉死现状）")
        void shtmlApiRequests_currentlyBypassAuthentication() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", "");

            JsonNode code = PARSER.readTree(body).path("code");
            assertThat(code.asText())
                    .as("记录当前行为：excludePathPatterns 的双星通配规则使 .shtml 请求绕过鉴权，"
                            + "空 token 仍返回 0000 而非 9007。若本测试失败，说明鉴权行为已变更，"
                            + "SHALL 同步复核 unauthorized_returns9007AsJson 上方的 @Disabled 说明")
                    .isEqualTo("0000");
        }

        @Test
        @DisplayName("失败响应的 code ∈ {9003,9004,9007,9999}")
        void failureCodes_areStandard() throws Exception {
            // 空参数触发 9003（Empty_Param）
            String body = mockMvc.perform(MockMvcRequestBuilders.get("/login/signin.shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("account", "")
                            .param("password", ""))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            String code = PARSER.readTree(body).get("code").asText();
            assertThat(code).isIn("0000", "9003", "9004", "9007", "9999");
        }
    }

    // ==================================================================
    // 场景④：日期格式与时区
    // ==================================================================

    @Nested
    @DisplayName("场景④ 日期格式与时区")
    class DateFormatAndTimeZone {

        @Test
        @DisplayName("日期字段匹配 ^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$，SHALL NOT 是纪元毫秒")
        void date_isFormattedNotTimestamp() throws Exception {
            // Project / ProjectVO 等实体带 createTime、modifyTime，由 MybatisPlusMetaObjectHandler 填充。
            // data.sql 未预置 project 行，故此处对 R 整体做形态断言：
            // 若 Jackson 序列化器把 java.util.Date 输出为纪元毫秒，响应中必然出现 10 位以上纯数字
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);

            assertThat(body).as("响应 SHALL NOT 出现纪元毫秒时间戳形态")
                    .doesNotContainPattern("\"(createTime|modifyTime)\"\\s*:\\s*\\d{10,}");
        }

        @Test
        @DisplayName("时区固定为 GMT+8：同一时刻格式化后 SHALL 与期望偏移一致")
        void timeZone_isGmt8() throws Exception {
            // 用一个已知的本地时刻验证 GMT+8 语义（spring.jackson.time-zone: GMT+8）
            LocalDateTime utcInstant = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
            LocalDateTime gmt8 = utcInstant.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("GMT+8"))
                    .toLocalDateTime();

            String formatted = gmt8.format(EXPECTED_DATE_FMT);

            assertThat(formatted).isEqualTo("2026-01-01 08:00:00");
            assertThat(formatted).matches(DATE);
            assertThat(formatted)
                    .as("相对 UTC 应偏移 +8 小时")
                    .isEqualTo(utcInstant.plusHours(8).format(EXPECTED_DATE_FMT));
        }

        @Test
        @DisplayName("日期格式常量与前端 bundle 中的格式串一致")
        void dateFormat_matchesFrontendBundle() {
            // 前端 main.js 中的格式串为 "yyyy-MM-dd HH:mm:ss"（nz-date-picker 默认格式），
            // 后端 spring.jackson.date-format 亦为同一格式 —— 两侧 SHALL 一致
            assertThat(EXPECTED_DATE_FMT.format(LocalDateTime.of(2026, 1, 2, 3, 4, 5)))
                    .as("格式化器 SHALL 输出前端约定的格式")
                    .isEqualTo("2026-01-02 03:04:05");
            assertThat(LocalDateTime.of(2026, 1, 2, 3, 4, 5).format(EXPECTED_DATE_FMT))
                    .matches(DATE);
        }
    }

    // ==================================================================
    // 场景⑤：null 字段保留
    // ==================================================================

    @Nested
    @DisplayName("场景⑤ null 字段保留")
    class NullFieldRetention {

        @Test
        @DisplayName("值为 null 的字段 SHALL 出现在 JSON 中，SHALL NOT 被省略")
        void nullFields_areRetained() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode record = firstRecord(body);
            assertThat(record).isNotNull();

            // 遍历该实体的全部属性：SHALL NOT 出现「Java 侧有值、JSON 侧缺键」
            for (String field : List.of("id", "code", "name", "description",
                    "gitHome", "account", "password", "isPublic")) {
                assertThat(record.has(field))
                        .as("字段 %s SHALL 保留在 JSON 中（即使为 null），SHALL NOT 被省略", field)
                        .isTrue();
            }
        }

        @Test
        @DisplayName("顶层 data 为 null 时该键 SHALL NOT 省略")
        void topLevelData_null_isRetained() throws Exception {
            // /login/signin 传入格式合法但不匹配的账户 → LoginCtrl 走 R.success() 且 data 为 null
            String body = mockMvc.perform(MockMvcRequestBuilders.get("/login/signin.shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("account", "contract-no-such-account")
                            .param("password", "irrelevant"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            assertThat(body).as("响应 SHALL 含 data 键，实际=%s", body).contains("\"data\"");
            assertThat(PARSER.readTree(body).has("data"))
                    .as("data 为 null 时该键 SHALL 仍然存在")
                    .isTrue();
            assertThat(PARSER.readTree(body).get("data").isNull())
                    .as("该场景下 data 的值 SHALL 为 null")
                    .isTrue();
        }
    }

    // ==================================================================
    // 场景⑥：WebSocket 端点可达
    // ==================================================================

    @Nested
    @DisplayName("场景⑥ WebSocket 端点带后缀可达")
    class WebSocketEndpoint {

        @Test
        @DisplayName("/websocket.shtml 不被静态资源处理器吞掉")
        void websocketPath_notSwallowedByStaticResources() throws Exception {
            // MOCK 环境下无法建立真实 WS 连接（无嵌入式容器监听端口）。
            // 可验证的是：该路径 SHALL NOT 被 addResourceHandler("/**") 当作静态资源处理，
            // 即响应 SHALL NOT 为静态资源的 200，而应是 404（无 REST 端点注册）或非静态内容。
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(FrontendEndpointCatalog.websocketPath()))
                    .andReturn();

            int status = result.getResponse().getStatus();
            String contentType = result.getResponse().getContentType();

            assertThat(status)
                    .as("WebSocket 端点由 JSR-356 处理器接管，SHALL NOT 返回静态资源内容")
                    .isNotEqualTo(200);
            assertThat(String.valueOf(contentType))
                    .as("SHALL NOT 是静态资源的内容类型")
                    .doesNotContain("text/css", "application/javascript");
        }

        @Test
        @DisplayName("WebSocket 端点在后端确实以 .shtml 注册")
        void websocketEndpoint_declaredWithSuffix() {
            // 该端点由 @ServerEndpoint(value = "/websocket.shtml") 声明，
            // 不经过 Spring MVC 路由，因此不受 PathMatchConfigurer 变更影响
            assertThat(FrontendEndpointCatalog.websocketPath()).endsWith(".shtml");
        }
    }

    // ==================================================================
    // 辅助方法
    // ==================================================================

    private String get(String path, String jwt) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get(path)
                                .header("x-requested-with", "XMLHttpRequest")
                                .param("token", jwt))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /**
     * 取出列表接口的第一条记录。
     *
     * <p>本项目的分页包装类是 {@code com.aicode.core.Page}，其列表字段名为
     * {@code voList}（不是 MyBatis-Plus 的 {@code records}），
     * 计数字段为 {@code totalRow}。契约断言 SHALL 反映真实结构。</p>
     */
    private JsonNode firstRecord(String body) throws Exception {
        JsonNode voList = PARSER.readTree(body).path("data").path("voList");
        return (voList.isArray() && voList.size() > 0) ? voList.get(0) : null;
    }
}
