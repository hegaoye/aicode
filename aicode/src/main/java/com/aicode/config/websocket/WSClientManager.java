package com.aicode.config.websocket;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器 —— **按房间（room）分组**。
 *
 * <p><b>2026-10-18 改造</b>（{@code openspec/changes/2026-10-18-websocket-room-routing}）：
 * 原实现用一个**硬编码的单一 key**（{@code token = "aicode"}）存所有会话，
 * 且 {@code put} 里只有在该 key 不存在时才保存 ——
 * 结果是<strong>只保留第一个连接，后续连接被直接丢弃</strong>。
 * 用户开第二个标签页就收不到任何推送。</p>
 *
 * <p><b>房间来自 URL 的 {@code buildId} 查询参数</b>（前端两个 ws hook 一直在传，
 * 后端此前完全忽略）。房间标识当前用项目编码，因此「项目 A 的构建日志」只会送到
 * 订阅了 A 的连接 —— 不会串到别的页面。</p>
 *
 * <p><b>房间上下文的传递方式</b>：异步执行（{@code @Async} 跑在虚拟线程上）与
 * 深层调用点之间靠 {@link #withRoom} 建立的 {@link ThreadLocal} 传递。
 * 全仓 43 处 {@code sendMessage} 调用点因此<strong>一行未改</strong>；
 * 若逐个加 room 参数，{@code GeneratorSVImpl} 内部 17 处需要把 projectCode
 * 沿方法签名下传 —— 那会动生成主链路。</p>
 *
 * <p><b>无房间上下文时广播给所有会话</b>（兜底）。最坏情况是「送达范围比预期宽」，
 * 而丢弃会让现有功能静默失效 —— 后者更难发现。</p>
 */
@Component
@Slf4j
public class WSClientManager {

    /**
     * URL 未携带 {@code buildId} 时的兜底房间。
     *
     * <p>刻意**不用**旧的硬编码值 {@code "aicode"} —— 那会让「无参数连接」与
     * 「显式 aicode 房间」混在一起，日志里也分辨不出。</p>
     */
    public static final String DEFAULT_ROOM = "__default__";

    /** 房间 → 该房间内的所有会话。 */
    private static final Map<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();

    /** 会话 → 所属房间（反向索引，供 {@link #remove(Session)} O(1) 反查）。 */
    private static final Map<Session, String> sessionRooms = new ConcurrentHashMap<>();

    /** 当前线程的任务房间上下文 —— 由 {@link #withRoom} 建立。 */
    private static final ThreadLocal<String> CURRENT_ROOM = new ThreadLocal<>();

    /** 静态变量，用来记录当前在线连接数。 */
    private static int onlineCount = 0;

    // ==================================================================
    // 会话登记 / 注销
    // ==================================================================

    /**
     * 登记一个会话到指定房间。
     *
     * <p><b>与原实现的差别</b>：原实现是「只有该 key 不存在时才保存」，导致第二个
     * 连接被静默丢弃。此处无条件登记到房间集合内，因此同房间的多个连接
     * （同一项目开两个标签页）可以共存。</p>
     *
     * @param session 已建立的会话
     * @param room    房间标识；为 {@code null} / 空时用 {@link #DEFAULT_ROOM}
     */
    public static void put(Session session, String room) {
        String key = normalizeRoom(room);
        roomSessions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRooms.put(session, key);
        addOnlineCount();
        log.info("新窗口加入房间:{}, 当前在线连接数:{}", key, getOnlineCount());
    }

    /**
     * 注销会话。
     *
     * <p><b>必须传 session</b>：多房间下无法凭硬编码 key 判断关的是哪个房间，
     * 只能通过反向索引反查。旧的 {@code remove()}（无参、按固定 key 删）在多房间下
     * 必然泄漏连接。</p>
     *
     * @param session 要注销的会话
     */
    public static void remove(Session session) {
        if (session == null) {
            return;
        }
        String room = sessionRooms.remove(session);
        if (room == null) {
            // 未登记过（例如登记前就断开）—— 不重复扣在线数
            return;
        }
        Set<Session> set = roomSessions.get(room);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                roomSessions.remove(room);
            }
        }
        subOnlineCount();
        log.info("窗口离开房间:{}, 当前在线连接数:{}", room, getOnlineCount());
    }

    // ==================================================================
    // 消息投递
    // ==================================================================

    /**
     * 投递消息到**当前线程的房间上下文**所指向的房间；无上下文时广播给所有会话。
     *
     * <p>房间上下文由 {@link #withRoom} 建立 —— 这让 43 处调用点无需知道房间。</p>
     *
     * @param message 消息文本
     */
    public static void sendMessage(String message) {
        String room = CURRENT_ROOM.get();
        if (room != null && !room.isEmpty()) {
            sendToRoom(room, message);
        } else {
            broadcast(message);
        }
    }

    /**
     * 投递消息到指定房间。
     *
     * @param room    房间标识
     * @param message 消息文本
     */
    public static void sendToRoom(String room, String message) {
        Set<Session> sessions = roomSessions.get(normalizeRoom(room));
        if (sessions == null || sessions.isEmpty()) {
            log.debug("房间 {} 无在线会话，跳过投递: {}", room, message);
            return;
        }
        deliver(sessions, message);
    }

    /**
     * 广播给**所有**房间的所有会话。
     *
     * <p>作为无房间上下文的兜底（如 {@link #sendShellMsg}）。最坏情况是送达范围
     * 比预期宽；相比「丢弃」，后者会让现有功能静默失效、更难发现。</p>
     *
     * @param message 消息文本
     */
    public static void broadcast(String message) {
        for (Set<Session> sessions : roomSessions.values()) {
            deliver(sessions, message);
        }
    }

    /** 逐个投递；遇到已关闭的会话时顺带从房间集合与反向索引中清理（避免泄漏）。 */
    private static void deliver(Collection<Session> sessions, String message) {
        // 复制一份：deliver 内会 remove，而原集合是并发的 keySet
        for (Session session : Set.copyOf(sessions)) {
            if (session == null || !session.isOpen()) {
                remove(session);
                continue;
            }
            try {
                session.getBasicRemote().sendText("# " + message);
            } catch (IOException e) {
                log.error("推送失败，移除该会话", e);
                remove(session);
            }
        }
    }

    public static void sendShellMsg(String username, String host, String path, String msg) {
        sendMessage(username + "@" + host + ":" + path + "# " + msg);
    }

    // ==================================================================
    // 房间上下文
    // ==================================================================

    /**
     * 在给定房间上下文中执行任务，结束后**清理**上下文。
     *
     * <p><b>try/finally 是硬要求</b>：异步执行跑在虚拟线程上，上下文若不清理，
     * 后续不相关的任务可能在同一线程上「继承」到上一个房间 —— 那会把消息推到
     * 错误的房间，比不清理更糟。</p>
     *
     * <p>用法：{@code WSClientManager.withRoom(projectCode, () -> { ...实际逻辑... })}。
     * 若实际逻辑需要抛异常，由调用方在自己的 try/catch 里包住（且该 catch
     * SHALL 在 {@code withRoom} 内部，否则 catch 里推的消息会因上下文已清理而广播出去）。</p>
     *
     * @param room 房间标识；空时用 {@link #DEFAULT_ROOM}
     * @param task  要执行的任务
     */
    public static void withRoom(String room, Runnable task) {
        CURRENT_ROOM.set(normalizeRoom(room));
        try {
            task.run();
        } finally {
            CURRENT_ROOM.remove();
        }
    }

    /**
     * 当前线程的房间上下文（测试与诊断用）。
     *
     * @return 房间标识；无上下文时返回 {@code null}
     */
    public static String currentRoom() {
        return CURRENT_ROOM.get();
    }

    /**
     * 某房间当前的连接数。
     *
     * @param room 房间标识
     * @return 在线会话数
     */
    public static int roomConnectionCount(String room) {
        Set<Session> set = roomSessions.get(normalizeRoom(room));
        return set == null ? 0 : set.size();
    }

    /** 所有房间的标识集合（诊断用）。 */
    public static Set<String> rooms() {
        return Set.copyOf(roomSessions.keySet());
    }

    // ==================================================================
    // 在线计数（沿用既有语义）
    // ==================================================================

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        if (onlineCount > 0) {
            onlineCount--;
        }
    }

    // ==================================================================
    // 内部
    // ==================================================================

    private static String normalizeRoom(String room) {
        return (room == null || room.isBlank()) ? DEFAULT_ROOM : room;
    }

    /**
     * 旧接口的兼容出口：返回该房间的**任一**在线会话。
     *
     * <p>已无语义价值（原实现靠硬编码 key 返回「那个唯一连接」）。
     * 仅供尚未迁移的调用方过渡使用，后续应改用 {@link #sendToRoom}。</p>
     *
     * @return 某个在线会话；无在线会话时返回 {@code null}
     */
    public static Session get() {
        for (Set<Session> sessions : roomSessions.values()) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    return s;
                }
            }
        }
        return null;
    }
}