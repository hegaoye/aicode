package io.aicode.core.enums;

/**
 * 模块定义
 * Created by lixin on 2017/6/7.
 */
public enum ModuleEnum {
    po("持久对象层,可以看成是与数据库中的表相映射的java对象"),
    vo("值对象层,通常用于业务层之间的数据传递"),
    dao("数据访问对象层，此对象用于访问数据库"),
    ctrl("控制层"),
    facade("业务接口定义层"),
    service("业务接口实现层"),;

    private String val;

    ModuleEnum(String val) {
        this.val = val;
    }

    /**
     * 任务状态
     *
     * @param stateName
     * @return
     */
    public static ModuleEnum getEnum(String stateName) {
        for (ModuleEnum moduleEnum : ModuleEnum.values()) {
            if (moduleEnum.name().equalsIgnoreCase(stateName)) {
                return moduleEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
