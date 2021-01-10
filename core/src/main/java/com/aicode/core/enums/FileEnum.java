package com.aicode.core.enums;

/**
 * 可上传文件类型枚举
 * Created by berton on 2018/9/18.
 */
public enum FileEnum {
    JPG("image/jpeg"), JPEG("image/jpeg"), PNG("image/png"), GIF("image/gif"), BMP("image/bmp"), RAR("application/octet-stream"), ZIP("application/zip"),
    DOC("application/msword"), DOCX("application/msword"),JSON("application/json"), XLS("application/vnd.ms-excel"), XLSX("application/vnd.ms-excel"), PPT("application/vnd.ms-powerpoint"), PPTX("application/vnd.ms-powerpoint"), FLV("application/octet-stream"), TXT("text/plain");
    public String value;

    FileEnum(String value) {
        this.value = value;
    }

    public static FileEnum getFileType(String value) {
        for (FileEnum fileType : FileEnum.values()) {
            if (fileType.value.equalsIgnoreCase(value)) {
                return fileType;
            }
        }
        return null;
    }

}
