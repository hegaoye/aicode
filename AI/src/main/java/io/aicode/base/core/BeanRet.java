/*
 *      http://www.aicode.io
 *      本代码仅用于AI-Code.
 */

package io.aicode.base.core;

import java.io.Serializable;

public class BeanRet implements Serializable {
    private boolean success = false;
    private String info = null;
    private Object data = null;

    public static BeanRet create(boolean success, String info) {
        return new BeanRet(success, info);
    }

    public static BeanRet create(boolean success, String info, Object data) {
        return new BeanRet(success, info, data);
    }

    public BeanRet() {
    }

    public BeanRet(boolean success, String info) {
        this.success = success;
        this.info = info;
    }

    public BeanRet(boolean success, String info, Object data) {
        this.success = success;
        this.info = info;
        this.data = data;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
