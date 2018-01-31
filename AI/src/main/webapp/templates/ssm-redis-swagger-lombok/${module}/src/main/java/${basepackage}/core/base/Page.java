package ${basePackage}.core.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前后数据交互的Bean类，用于前后台的所有交互中数据的传输和封装。<p/>
 * 所有的构造方法当中，总会初始化voList和params.
 *
 * @author sys 11-12-16 下午4:56
 */
public class Page<VO> implements java.io.Serializable {
    /**
     * 全局分页大小定义
     */
    public static final int limit = 25;
    /**
     * 查询用的参数
     */
    private Map<String, Object> params;
    /**
     * 用于前台展示的VOList
     */
    private List<VO> voList;
    /**
     * 总记录行数
     */
    private int totalRow = 0;
    /**
     * 当前页，当传递到后台时候，就是要获取的页
     */
    private int curPage = 1;
    /**
     * 分页大小(从系统公共配置中获取)
     */
    private int pageSize = limit;
    /**
     * 排序列的sql串，用来构建用于排序的SQL，不能再包含Order By串。
     */
    private String sortColumns;

    /**
     * 批量（插入、更新、删除）操作的参数，用于存放ID、实体对象等，这些操作之后通常需要查询更新视图的展示
     */
    private List optObjectList;
    /**
     * 单个（插入、更新、删除）操作的参数，用于存放ID、实体对象等，这些操作之后通常需要查询更新视图的展示
     */
    private Object optObject;

    /**
     * 默认构造方法，所有的构造方法当中，总会初始化voList和params
     */
    public Page() {
        voList = new ArrayList<VO>(this.pageSize);
        params = new HashMap<String, Object>(0);
        if (curPage > getTotalPage()) {
            this.curPage = getTotalPage();
        }
        if (curPage < 1) {
            curPage = 1;
        }
    }

    /**
     * Page构造方法
     *
     * @param pageSize 分页大小
     */
    public Page(int pageSize) {
        voList = new ArrayList<VO>(this.pageSize);
        params = new HashMap<String, Object>(0);
        this.pageSize = pageSize;
        if (curPage > getTotalPage()) {
            this.curPage = getTotalPage();
        }
        if (curPage < 1) {
            curPage = 1;
        }
    }

    /**
     * Page构造方法
     *
     * @param pageSize 分页大小
     * @param curPage  当前页
     */
    public Page(int pageSize, int curPage) {
        this.pageSize = pageSize;
        this.curPage = curPage;
        params = new HashMap<String, Object>(0);
        voList = new ArrayList<VO>(this.pageSize);
        if (curPage > getTotalPage()) {
            this.curPage = getTotalPage();
        }
        if (curPage < 1) {
            this.curPage = 1;
        }
    }

    /**
     * Page构造方法
     *
     * @param pageSize 分页大小
     * @param curPage  当前页
     * @param totalRow 总行数
     */
    public Page(int pageSize, int curPage, int totalRow) {
        voList = new ArrayList<VO>(this.pageSize);
        params = new HashMap<String, Object>(0);
        this.totalRow = totalRow;
        this.pageSize = pageSize;
        this.curPage = curPage;
        if (curPage > getTotalPage()) {
            this.curPage = getTotalPage();
        }
        if (curPage < 1) {
            curPage = 1;
        }
    }

    /**
     * Page构造方法
     *
     * @param pageSize    分页大小
     * @param curPage     当前页
     * @param totalRow    总行数
     * @param sortColumns 排序字符串（不含order by部分）
     */
    public Page(int pageSize, int curPage, int totalRow, String sortColumns) {
        voList = new ArrayList<VO>(this.pageSize);
        params = new HashMap<String, Object>(0);
        this.totalRow = totalRow;
        this.pageSize = pageSize;
        this.curPage = curPage;
        this.sortColumns = sortColumns;
        if (curPage > getTotalPage()) {
            this.curPage = getTotalPage();
        }
        if (curPage < 1) {
            curPage = 1;
        }
    }

    /**
     * 获取当前Page对象的SqlMap的查询参数，类型为Map类型
     *
     * @return 获取当前Page对象的SqlMap的查询参数
     */
    public Map<String, Object> genSqlmapParams() {
        if (sortColumns == null || sortColumns.trim().equalsIgnoreCase("") || sortColumns.trim().equalsIgnoreCase("null")) {
            return genSqlmapParams();
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(params);
        params.put("sortColumns", sortColumns);
        return params;
    }

    /**
     * 判断是否需要做后台的总数统计,当前台查询条件改变时候，回传的totalRow的值必须值为0
     *
     * @return 需要做后台总数统计时返回true，否则返回false
     */
    public boolean isNeedCountQuery() {
        return totalRow > 0 ? false : true;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotalPage() {
        return totalRow <= pageSize ? 1 : (totalRow + pageSize - 1) / pageSize;
    }

    /**
     * 获取起始行数,不包含起始行,对于MySQL来说，limit 0，x 表示，从0行开始，不含0行，取x条记录。
     *
     * @return 起始行数
     */
    public int genRowStart() {
        return curPage * pageSize - pageSize;
    }

    /**
     * 是否最后一页
     *
     * @return 是否最后一页，是返回true，否返回false
     */
    public boolean isLastPage() {
        if (totalRow == 0 || totalRow <= pageSize) {
            return true;
        }
        if (curPage == ((totalRow + pageSize - 1) / pageSize)) {
            return true;
        }
        return false;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<VO> getVoList() {
        return voList;
    }

    public void setVoList(List<VO> st) {
        this.voList = st;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortColumns() {
        return sortColumns;
    }

    public void setSortColumns(String sortColumns) {
        this.sortColumns = sortColumns;
    }

    public List getOptObjectList() {
        return optObjectList;
    }

    public void setOptObjectList(List optObjectList) {
        this.optObjectList = optObjectList;
    }

    public Object getOptObject() {
        return optObject;
    }

    public void setOptObject(Object optObject) {
        this.optObject = optObject;
    }
}
