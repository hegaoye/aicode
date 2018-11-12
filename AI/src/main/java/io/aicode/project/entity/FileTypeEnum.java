/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

/**
 * 文件类型
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum FileTypeEnum implements java.io.Serializable {
    Java(".java"), Xml(".xml"), Property(".properties"),;

    public String val;

    FileTypeEnum(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static FileTypeEnum getEnum(String stateName) {
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (fileTypeEnum.name().equalsIgnoreCase(stateName)) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

}

