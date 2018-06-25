/*
 * ${copyright}
 */
package ${basePackage}.core.enums;

/**
 * true/false 枚举
 * Created by lixin on 2017/6/7.
 */
public enum YNEnum {
    Y(true), N(false);
    public boolean val;

    YNEnum(boolean val) {
        this.val = val;
    }

    //通过值获得性别
    public static YNEnum getYN(String yn) {
        for (YNEnum ynEnum : YNEnum.values()) {
            if (ynEnum.name().equalsIgnoreCase(yn)) {
                return ynEnum;
            }
        }
        return null;
    }


}
