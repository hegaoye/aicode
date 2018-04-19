package ${basePackage}.core.base;

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
