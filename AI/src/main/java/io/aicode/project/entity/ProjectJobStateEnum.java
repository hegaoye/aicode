package io.aicode.project.entity;

/**
 * 任务状态: 创建[Create] , 执行中[Executing], 完成[Completed] ,失败[Error] 警告 [Waring]
 * Created by lixin on 2017/12/18.
 */
public enum ProjectJobStateEnum {
    Create("创建"),
    Executing("执行中"),
    Completed("完成"),
    Error("失败"),
    Waring("警告");
    public String val;

    ProjectJobStateEnum(String val) {
        this.val = val;
    }

    /**
     * 任务状态
     *
     * @param stateName
     * @return
     */
    public static ProjectJobStateEnum getEnum(String stateName) {
        for (ProjectJobStateEnum projectJobStateEnum : ProjectJobStateEnum.values()) {
            if (projectJobStateEnum.name().equalsIgnoreCase(stateName)) {
                return projectJobStateEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
