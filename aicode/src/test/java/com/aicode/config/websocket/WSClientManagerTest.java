package com.aicode.config.websocket;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link WSClientManager} 的房间路由单测
 * （{@code openspec/changes/2026-10-18-websocket-room-routing}）。
 *
 * <p><b>守护的核心行为</b>：改造前该类用一个硬编码 key 存所有会话、且
 * {@code put} 只在「该 key 不存在时才保存」—— 结果<strong>只保留第一个连接</strong>，
 * 第二个标签页被静默丢弃。本类的用例 {@code put_differentRooms_coexist} 正是这一点的回归防线。</p>
 *
 * <p>会话替身用 Mockito 而非手写实现 —— {@code jakarta.websocket.Session} 的方法太多
 * （含泛型 MessageHandler），手写实现既冗长又易随接口演进而失效。</p>
 */
class WSClientManagerTest {

    /** 建一个「在线、可收消息」的会话替身，并记录收到的文本。 */
    private static Session newOpenSession() {
        Session s = mock(Session.class);
        List<String> inbox = new ArrayList<>();
        RemoteEndpoint.Basic remote = mock(RemoteEndpoint.Basic.class);
        try {
            org.mockito.Mockito.doAnswer(inv -> {
                inbox.add(inv.getArgument(0, String.class));
                return null;
            }).when(remote).sendText(anyString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        when(s.isOpen()).thenReturn(true);
        when(s.getBasicRemote()).thenReturn(remote);
        // inbox 通过 getBasicRemote() 间接暴露，测试里用 received(s) 读取
        INBOX.put(s, inbox);
        return s;
    }

    /** 会话 → 它的收件箱（避免用自定义实现）。 */
    private static final java.util.Map<Session, List<String>> INBOX =
            new java.util.IdentityHashMap<>();

    private static List<String> received(Session s) {
        return INBOX.getOrDefault(s, List.of());
    }

    private final List<Session> created = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Session s : created) {
            WSClientManager.remove(s);
        }
        created.clear();
        INBOX.clear();
    }

    private Session track(Session s) {
        created.add(s);
        return s;
    }

    // ---- ① 多连接共存（本变更的核心回归防线） ----

    @Test
    @DisplayName("不同房间的两个连接 SHALL 共存（改造前第二个会被静默丢弃）")
    void put_differentRooms_coexist() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());

        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-B");

        assertEquals(1, WSClientManager.roomConnectionCount("project-A"), "房间 A SHALL 有 1 个连接");
        assertEquals(1, WSClientManager.roomConnectionCount("project-B"),
                "房间 B SHALL 有 1 个连接（改造前这里会是 0 —— 第二个连接被丢弃）");
        assertTrue(WSClientManager.rooms().containsAll(List.of("project-A", "project-B")));
    }

    @Test
    @DisplayName("同房间的多个连接 SHALL 共存（同一项目开两个标签页）")
    void put_sameRoom_multipleSessionsCoexist() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());

        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-A");

        assertEquals(2, WSClientManager.roomConnectionCount("project-A"));
    }

    // ---- ② 消息只送到对应房间 ----

    @Test
    @DisplayName("sendMessage SHALL 只投递给当前房间，不投递给其他房间")
    void sendMessage_onlyToCurrentRoom() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());
        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-B");

        WSClientManager.withRoom("project-A", () -> WSClientManager.sendMessage("A 的构建日志"));

        assertEquals(List.of("# A 的构建日志"), received(a));
        assertTrue(received(b).isEmpty(),
                "B 房间 SHALL NOT 收到 A 的消息，实际=" + received(b));
    }

    // ---- ③ remove 只影响对应连接 ----

    @Test
    @DisplayName("remove(A) SHALL NOT 影响 B 房间的连接")
    void remove_onlyAffectsThatSession() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());
        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-B");

        WSClientManager.remove(a);

        assertEquals(0, WSClientManager.roomConnectionCount("project-A"));
        assertEquals(1, WSClientManager.roomConnectionCount("project-B"), "关闭 A SHALL NOT 影响 B");

        WSClientManager.withRoom("project-B", () -> WSClientManager.sendMessage("仍在"));
        assertEquals(1, received(b).size(), "B 的连接 SHALL 仍可收消息");
    }

    // ---- ④ 无房间上下文时广播（兜底） ----

    @Test
    @DisplayName("无房间上下文时 SHALL 广播给所有连接（兜底而非丢弃）")
    void sendMessage_withoutRoom_broadcasts() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());
        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-B");

        assertNull(WSClientManager.currentRoom());
        WSClientManager.sendMessage("shell 消息");

        assertEquals(1, received(a).size(), "无上下文 SHALL 广播");
        assertEquals(1, received(b).size(), "无上下文 SHALL 广播");
    }

    // ---- ⑤ withRoom 的 try/finally 清理 ----

    @Test
    @DisplayName("withRoom：任务抛异常时上下文 SHALL 被清理（否则会串房间）")
    void withRoom_clearsContextOnException() {
        Session a = track(newOpenSession());
        Session b = track(newOpenSession());
        WSClientManager.put(a, "project-A");
        WSClientManager.put(b, "project-B");

        assertThrows(RuntimeException.class, () -> WSClientManager.withRoom("project-A", () -> {
            throw new RuntimeException("任务失败");
        }));

        assertNull(WSClientManager.currentRoom(),
                "异常退出后 SHALL NOT 残留房间上下文 —— 否则下一个不相关任务会推到错误的房间");

        WSClientManager.sendMessage("之后的消息");
        assertEquals(1, received(a).size(), "清理后 SHALL 退回广播");
        assertEquals(1, received(b).size(), "清理后 SHALL 退回广播");
    }

    @Test
    @DisplayName("withRoom 正常结束后 SHALL 清理上下文")
    void withRoom_clearsContextOnSuccess() {
        WSClientManager.withRoom("project-A", () -> assertEquals("project-A", WSClientManager.currentRoom()));
        assertNull(WSClientManager.currentRoom());
    }

    // ---- ⑥ 已关闭 / 投递失败的会话被跳过并清理 ----

    @Test
    @DisplayName("sendMessage 遇到已关闭的会话 SHALL 跳过并清理（不抛异常、不泄漏）")
    void sendMessage_skipsClosedSession() {
        Session closed = mock(Session.class);
        when(closed.isOpen()).thenReturn(false);
        Session alive = track(newOpenSession());
        WSClientManager.put(closed, "room-1");
        WSClientManager.put(alive, "room-1");
        created.add(closed);

        WSClientManager.withRoom("room-1", () -> WSClientManager.sendMessage("消息"));

        assertEquals(1, received(alive).size(), "在线的 SHALL 收到");
        assertEquals(1, WSClientManager.roomConnectionCount("room-1"),
                "已关闭的 SHALL 被顺带清理，不留泄漏");
    }

    @Test
    @DisplayName("投递抛 IOException SHALL 被捕获并清理该会话，不影响同房间其他连接")
    void deliver_ioException_cleansUpAndContinues() {
        Session bad = mock(Session.class);
        RemoteEndpoint.Basic badRemote = mock(RemoteEndpoint.Basic.class);
        try {
            doThrow(new IOException("连接已断")).when(badRemote).sendText(anyString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        when(bad.isOpen()).thenReturn(true);
        when(bad.getBasicRemote()).thenReturn(badRemote);

        Session good = track(newOpenSession());
        WSClientManager.put(bad, "room-x");
        WSClientManager.put(good, "room-x");
        created.add(bad);

        WSClientManager.withRoom("room-x", () -> WSClientManager.sendMessage("消息"));

        assertEquals(1, received(good).size(), "一个会话投递失败 SHALL NOT 影响同房间其他连接");
        assertEquals(1, WSClientManager.roomConnectionCount("room-x"), "失败的会话 SHALL 被清理");
    }

    // ---- 边界：URL 未带 buildId ----

    @Test
    @DisplayName("put 房间为 null 时 SHALL 兜底到 DEFAULT_ROOM（旧客户端兼容）")
    void put_nullRoom_fallsBackToDefault() {
        Session s = track(newOpenSession());

        WSClientManager.put(s, null);

        assertEquals(1, WSClientManager.roomConnectionCount(WSClientManager.DEFAULT_ROOM));
        assertFalse(WSClientManager.rooms().contains("aicode"),
                "兜底房间 SHALL NOT 用旧的硬编码值 aicode（会与显式房间混淆）");
    }
}