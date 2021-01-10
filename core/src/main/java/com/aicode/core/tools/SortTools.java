package com.aicode.core.tools;

import java.util.Arrays;

public enum SortTools {
    Instance;

    /**
     * 排序
     *
     * @param text 需要排序的文本内容
     * @return 排序后文本
     */
    public String sort(String text) {
        if (!text.contains(",")) {
            return text;
        }

        String[] textArr = text.split(",");
        Arrays.sort(textArr);

        String sortResult = "";
        for (String str : textArr) {
            sortResult = sortResult + "," + str;

        }
        return sortResult.replaceFirst(",", "");

    }

}
