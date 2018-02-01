/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.tools;

/**
 * Created by lixin on 2017/10/18.
 */
public class SSHResInfo {
    private int exitStuts;//返回状态码 （在linux中可以通过 echo $? 可知每步执行令执行的状态码）
    private String outRes;//标准正确输出流内容
    private String errRes;//标准错误输出流内容


    public SSHResInfo(int exitStuts, String outRes, String errRes) {
        super();
        this.exitStuts = exitStuts;
        this.outRes = outRes;
        this.errRes = errRes;
    }

    public SSHResInfo() {
        super();
    }

    public int getExitStuts() {
        return exitStuts;
    }

    public void setExitStuts(int exitStuts) {
        this.exitStuts = exitStuts;
    }

    public String getOutRes() {
        return outRes;
    }

    public void setOutRes(String outRes) {
        this.outRes = outRes;
    }

    public String getErrRes() {
        return errRes;
    }

    public void setErrRes(String errRes) {
        this.errRes = errRes;
    }

    /**
     * 当exitStuts=0 && errRes="" &&outREs=""返回true
     *
     * @return
     */
    public boolean isEmptySuccess() {
        if (this.getExitStuts() == 0 && "".equals(this.getErrRes()) && "".equals(this.getOutRes())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SSHResInfo [exitStuts=" + exitStuts + ", outRes=" + outRes + ", errRes=" + errRes + "]";
    }

    public void clear() {
        exitStuts = 0;
        outRes = errRes = null;
    }
}
