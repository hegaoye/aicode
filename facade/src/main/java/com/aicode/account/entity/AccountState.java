/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.entity;

/**
 * 账户 的实体类的状态
 *
 * @author hegaoye
 */
public enum AccountState implements java.io.Serializable {
    ;

    public String val;

    AccountState(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static AccountState getEnum(String stateName) {
        for (AccountState _accountState : AccountState.values()) {
            if (_accountState.name().equalsIgnoreCase(stateName)) {
                return _accountState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}
