package com.aicode.session.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.core.tools.Md5;
import com.aicode.core.tools.PasswordHasher;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 口令散列从无盐 MD5 迁到 BCrypt 的**集成**守护（2026-10-18-password-bcrypt-migration）。
 *
 * <p><b>本类守护的是登录主链路** —— 改错会锁死所有人。三条核心场景：</p>
 * <ol>
 *   <li>MD5 存量 → 登录成功，<b>且库中口令已透明升级为 BCrypt</b>；</li>
 *   <li>口令错误时 <b>SHALL NOT</b> 改写 —— 否则攻击者能用任意错误口令把
 *       合法用户的口令替换成他不知道的值（账户锁死）；</li>
 *   <li>BCrypt 存量 → 登录成功且口令列<b>保持原值</b>（不每次登录都重算）。</li>
 * </ol>
 *
 * <p>使用<b>真实数据库</b>（H2 test profile + {@code data.sql} 种子）—— 透明升级是
 * 「读库 → 校验 → 写库」的跨层行为，打桩的 Mapper 测不到。</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class LoginPasswordMigrationIT {

    /** db/data.sql 里 admin 账户的口令（**无盐 MD5**）。 */
    private static final String SEED_ADMIN_MD5 = "21218cca77804d2ba1922c33e0151105";
    /** 与种子匹配的明文 —— MD5("888888") == SEED_ADMIN_MD5（README 的默认账户）。 */
    private static final String SEED_ADMIN_PASSWORD = "888888";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AccountService accountService;

    /**
     * MOCK 环境下没有 Servlet 容器，`ServerEndpointExporter` 创建会失败
     * （`jakarta.websocket.server.ServerContainer not available`）——
     * 与本类要测的登录链路无关，打桩掉。
     */
    @MockitoBean
    private ServerEndpointExporter serverEndpointExporter;

    private MockMvc mockMvc;

    private String storedPasswordOf(String account) {
        Account acc = accountService.getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getAccount, account));
        return acc == null ? null : acc.getPassword();
    }

    /** 把 admin 的口令**预置**成给定散列（用于构造 BCrypt 存量场景）。 */
    private void presetStoredPassword(String account, String hashed) {
        Account toUpdate = new Account();
        toUpdate.setId(storedIdOf(account));
        toUpdate.setPassword(hashed);
        accountService.updateById(toUpdate);
    }

    private Long storedIdOf(String account) {
        return accountService.getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getAccount, account)).getId();
    }

    // ---- 场景①：MD5 存量的透明升级 ----

    @Test
    @DisplayName("MD5 存量口令 → 登录成功，且库中口令被透明升级为 BCrypt")
    void signin_md5Legacy_upgradesToBcrypt() throws Exception {
        // 前提：种子里 admin 的口令是 MD5
        assertEquals(SEED_ADMIN_MD5, storedPasswordOf("admin"),
                "前提不成立：种子口令应为 MD5");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/login/signin").param("account", "admin")
                        .param("password", SEED_ADMIN_PASSWORD))
                .andExpect(status().isOk())
                // data 非空 = 签发了 token
                .andExpect(result -> assertFalse(
                        result.getResponse().getContentAsString().contains("\"data\":null"),
                        "MD5 存量用户 SHALL 仍能登录成功"));

        String after = storedPasswordOf("admin");
        assertNotEquals(SEED_ADMIN_MD5, after, "库中口令 SHALL 已被改写");
        assertTrue(PasswordHasher.isBcrypt(after), "SHALL 变成 BCrypt，实际=" + after);
        assertTrue(PasswordHasher.matches(SEED_ADMIN_PASSWORD, after),
                "升级后同一口令 SHALL 仍能校验通过（否则下次登录就登不上了）");
    }

    // ---- 场景②：口令错误 SHALL NOT 改写 ----

    @Test
    @DisplayName("口令错误时 SHALL NOT 改写库中口令（否则可把用户锁死）")
    void signin_wrongPassword_doesNotUpgrade() throws Exception {
        presetStoredPassword("aicode", Md5.md5("correct-password"));
        String before = storedPasswordOf("aicode");

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/login/signin").param("account", "aicode")
                        .param("password", "attacker-guess"))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains("\"data\":null"),
                        "口令错误 SHALL NOT 签发 token"));

        assertEquals(before, storedPasswordOf("aicode"),
                "口令错误时 SHALL NOT 触发改写 —— 否则攻击者能锁死合法账户");
    }

    // ---- 场景③：BCrypt 存量不被反复重算 ----

    @Test
    @DisplayName("BCrypt 存量口令 → 登录成功且口令列保持原值（不每次登录都重算）")
    void signin_bcryptLegacy_keepsValue() throws Exception {
        String original = PasswordHasher.hash("stable-password");
        presetStoredPassword("aicode", original);

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/login/signin").param("account", "aicode")
                        .param("password", "stable-password"))
                .andExpect(status().isOk())
                .andExpect(result -> assertFalse(
                        result.getResponse().getContentAsString().contains("\"data\":null"),
                        "BCrypt 存量 SHALL 能登录"));

        assertEquals(original, storedPasswordOf("aicode"),
                "已是 BCrypt 的口令 SHALL 保持原值 —— 否则每次登录都重算（白白消耗 CPU）");
    }

    // ---- 场景④：口令错误在任何格式下都不通过 ----

    @Test
    @DisplayName("BCrypt 存量 + 错误口令 SHALL 不通过")
    void signin_bcryptWrongPassword_rejected() throws Exception {
        presetStoredPassword("aicode", PasswordHasher.hash("right-password"));

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/login/signin").param("account", "aicode")
                        .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains("\"data\":null")));
    }
}