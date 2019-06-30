/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.目.
 */

package io.aicode.base.enums;

/**
 * 上传文件资料类型
 * Created by lixin on 2017/6/2.
 */
public enum UploadTypeEnum {
    BASIC("基础资料"),
    CONTRACT("合同资料"),
    ILLNESSCASE("病例资料"),
    INSPECT("体检资料"),
    NURSING("护理资料"),
    TYPELESS("其它资料");
    public String type;

    UploadTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static UploadTypeEnum getEnum(String type) {
        for (UploadTypeEnum uploadTypeEnum : UploadTypeEnum.values()) {
            if (uploadTypeEnum.name().toLowerCase().equals(type.toLowerCase())) {
                return uploadTypeEnum;
            }
        }
        return null;
    }


}
