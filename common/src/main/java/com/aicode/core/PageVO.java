package com.aicode.core;

import lombok.Data;

import java.util.List;

/**
 * 用于 page 分页返回时 vo 控制
 * 查询 page 与显示 page 进行分开
 *
 * <p><b>刻意不继承 {@link BaseVO}</b>（2026-10-18-pagevo-drop-basevo-noise）：
 * {@code BaseVO} 的 4 个字段（{@code createTimeBegin} / {@code createTimeEnd} /
 * {@code updateTimeBegin} / {@code updateTimeEnd}）语义是「<b>入参</b>：按时间范围过滤」，
 * 被出参容器继承就会让<b>每个分页响应</b>的 {@code data} 层携带这 4 个恒为 {@code null}
 * 的字段 —— 前端零消费，纯噪音，且固化进契约后删不掉。</p>
 *
 * <p>本容器对这 4 个字段零引用，删继承无编译影响。入参 DTO（如 {@code *PageDTO}）
 * 仍可继承 {@code BaseVO} —— 那是它们的正当用途。判据是「出现在响应的 data 上」
 * 还是「作为请求参数被绑定」，不是「叫 VO 还是叫 DTO」。</p>
 *
 * @author sys 11-12-16 下午4:56
 */
@Data
public final class PageVO<VO> implements java.io.Serializable {
    /**
     * 当前页，当传递到后台时候，就是要获取的页
     */
    private int curPage;

    /**
     * 分页大小(从系统公共配置中获取)
     */
    private int pageSize;

    /**
     * 总记录行数
     */
    private long totalRow;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 用于前台展示的VOList
     */
    private List<VO> records;

}
