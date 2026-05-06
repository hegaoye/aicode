package com.aicode.core.enums;

public enum Gender {
    Male("男"),
    Female("女"),
    Other("其他"),;
    public String val;

    Gender(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static Gender getEnum(String stateName) {
        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(stateName)) {
                return gender;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
