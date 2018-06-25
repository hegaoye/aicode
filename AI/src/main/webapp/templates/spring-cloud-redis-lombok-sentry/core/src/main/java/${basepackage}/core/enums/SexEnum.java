/*
 * ${copyright}
 */
package ${basePackage}.core.enums;

/**
 * 性别枚举
 * Created by lixin on 2017/6/7.
 */
public enum SexEnum {
    MALE("男"), FEMALE("女"), OTHER("无");
    public String val;

    SexEnum(String val) {
        this.val = val;
    }

    //通过值获得性别
    public static SexEnum getSex(String sex) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.name().equalsIgnoreCase(sex)) {
                return sexEnum;
            }
        }
        return null;
    }


}
