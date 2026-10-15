package com.aicode.contract;

import java.util.List;

/**
 * 前端端点清单 —— 从打包产物中提取，**不是**从后端枚举。
 *
 * <h3>来源</h3>
 * <ul>
 *   <li>文件：{@code src/main/resources/static/main.05f069fd87c10680adbe.js}</li>
 *   <li>模块：webpack 打包后的 {@code c2t9} 模块，导出全局 {@code URL} 配置对象</li>
 *   <li>提取日期：2026-10-14</li>
 *   <li>提取方式：正则扫描该模块内 {@code URL = {...}} 的全部字符串字面量</li>
 * </ul>
 *
 * <h3>为什么必须从前端提取</h3>
 * 前端是 Angular 7.0.4 + jQuery 的<b>冻结打包产物</b>，无法在 Java 侧做类型检查。
 * 若改从后端 {@code @RequestMapping} 枚举，会漏掉「前端仍在调用、但后端已下线」的端点，
 * 而这类漂移恰恰是最该被契约测试显式记录的。
 *
 * <h3>维护约定</h3>
 * 新增 / 删除后端端点时，若前端同步调用了它，<b>SHALL</b> 同步维护本清单；
 * 否则该端点将失去 {@code .shtml} 双路径可达性的回归保护。
 *
 * @see ApiContractIT 契约断言
 */
public final class FrontendEndpointCatalog {

    private FrontendEndpointCatalog() {
    }

    // ------------------------------------------------------------------
    // 前端配置但后端未实现 —— 已确认的接口漂移
    // ------------------------------------------------------------------

    /**
     * 前端 {@code URL.base.enum} 配置了枚举接口，后端无对应 Controller。
     * 前端大概率是早期版本的残留入口。显式登记以避免"静默遗漏"。
     */
    public static final String BASE_ENUM = "/res/enum/";

    /**
     * 前端 {@code URL.base.uuid} 配置了 UID 获取接口，后端无对应 Controller。
     * 该能力已由百度 UID 组件（本仓库 vendored jar）接管，REST 入口下线。
     *
     * @see openspec/changes/2026-10-14-replace-baidu-uid-generator
     */
    public static final String BASE_UUID = "/upload/basic/uid";

    /** 前端配置的 WebSocket 端点，同样带 {@code .shtml} 后缀。 */
    public static final String WEBSOCKET = "/websocket.shtml";

    // ------------------------------------------------------------------
    // 六个已注册的一级路由前缀 —— 决定 .shtml 后缀兼容层的白名单
    // ------------------------------------------------------------------

    public static final List<String> REGISTERED_PREFIXES = List.of(
            "/project",
            "/framework",
            "/displayAttribute",
            "/logs",
            "/login",
            "/account");

    // ------------------------------------------------------------------
    // 全部端点（46 个，含 2 个后端未实现的）
    // ------------------------------------------------------------------

    /** 全部前端配置路径，含后端未实现的 {@link #BASE_ENUM} 与 {@link #BASE_UUID}。 */
    public static final List<String> ALL = List.of(
            // URL.base
            BASE_ENUM, BASE_UUID,
            // URL.projectCtrl
            "/project/build", "/project/delete", "/project/init", "/project/list",
            "/project/load", "/project/modify", "/project/scan/path",
            // URL.projectJobCtrl
            "/project/job/build", "/project/job/execute", "/project/job/delete",
            "/project/job/list", "/project/job/load", "/project/job/modify",
            // URL.projectJobLogsCtrl
            "/logs/load",
            // URL.frameworksCtrl
            "/framework/build", "/framework/delete", "/framework/list",
            "/framework/load", "/framework/modify", "/framework/updateIsPublic",
            // URL.projectFramworkCtrl
            "/project/framwork/add", "/project/framwork/delete",
            "/project/framwork/list", "/project/framwork/load",
            // URL.projectRepositoryAccountCtrl
            "/project/repository/build", "/project/repository/delete",
            "/project/repository/list", "/project/repository/load",
            "/project/repository/modify",
            // URL.projectSqlCtrl
            "/project/sql/build", "/project/sql/delete",
            "/project/sql/list", "/project/sql/load", "/project/sql/modify",
            // URL.mapRelationship
            "/project/relationship/listMapClassTable",
            "/project/relationship/listByProjectCode",
            "/project/relationship/list",
            "/project/relationship/listMapFieldColumn",
            "/project/relationship/build",
            "/project/relationship/delete",
            // URL.attributes
            "/displayAttribute/list", "/displayAttribute/save",
            // URL.login
            "/login/signin", "/login/reg");

    /** 后端未实现、因而 SHALL NOT 参与可达性断言的端点。 */
    public static final List<String> BACKEND_MISSING = List.of(BASE_ENUM, BASE_UUID);

    /**
     * 参与可达性断言的端点（= {@link #ALL} 去掉 {@link #BACKEND_MISSING}）。
     *
     * <p>调用方 SHALL 对每个元素做两次请求：{@code <path>.shtml} 与 {@code <path>}，
     * 并断言两次的状态码与响应体一致 —— 这是 Spring Framework 7 移除后缀匹配
     * API 后的主要回归防线。</p>
     */
    public static final List<String> REACHABLE = ALL.stream()
            .filter(p -> !BACKEND_MISSING.contains(p))
            .toList();

    /** WebSocket 端点路径（带后缀）。 */
    public static String websocketPath() {
        return WEBSOCKET;
    }
}
