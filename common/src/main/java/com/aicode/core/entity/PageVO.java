package com.aicode.core.entity;

import com.aicode.core.base.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 用于page分页返回时vo控制
 * 查询 page 与显示 page 进行分开
 *
 * @author sys 11-12-16 下午4:56
 */
@Data
public final class PageVO<VO> extends BaseVO implements java.io.Serializable {
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
    private int totalRow;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 用于前台展示的VOList
     */
    private List<VO> records;

}
