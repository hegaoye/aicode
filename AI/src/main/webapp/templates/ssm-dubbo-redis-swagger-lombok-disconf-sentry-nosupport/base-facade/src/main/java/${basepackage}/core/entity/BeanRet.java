/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.entity;

import java.io.Serializable;

public class BeanRet implements Serializable {
    private boolean success = false;
    private String info = "操作失败";
    private Object data = null;

    public static BeanRet create() {
        return new BeanRet();
    }

    public static BeanRet create(String info) {
        return new BeanRet(false, info);
    }

    public static BeanRet create(boolean success, String info) {
        return new BeanRet(success, info);
    }

    public static BeanRet create(boolean success, String info, Object data) {
        return new BeanRet(success, info, data);
    }

    private BeanRet() {

    }

    private BeanRet(boolean success, String info) {
        this.success = success;
        this.info = info;
    }

    private BeanRet(boolean success, String info, Object data) {
        this.success = success;
        this.info = info;
        this.data = data;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        this.info = success ? "操作成功" : info;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }


}
