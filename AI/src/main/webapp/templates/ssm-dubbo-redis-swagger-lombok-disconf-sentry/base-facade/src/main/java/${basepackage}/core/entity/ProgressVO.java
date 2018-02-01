package ${basePackage}.core.entity;

/**
 * 进度条VO
 * Created by shangze on 2017/6/23.
 */
public class ProgressVO {
    private Integer currentNum = 1; //当前条数
    private Integer maxNum;         //最大条数
    private Integer errorNum;       //失败条数

    public ProgressVO() {
    }

    public ProgressVO(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public ProgressVO(Integer maxNum, Integer errorNum) {
        this.maxNum = maxNum;
        this.errorNum = errorNum;
    }

    public Integer getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }
}
