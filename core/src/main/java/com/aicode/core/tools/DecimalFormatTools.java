package com.aicode.core.tools;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DecimalFormatTools {
    Instance;

    /**
     * 格式化 小数
     * 自动保留两位小数，
     * 如果是 0 则自动保留为0.00的结果
     * 其余自动补足 .00或者 省略后面的小数
     * <p>
     * 根据需求需要，对大于2位的小数进行直接舍去，不进行四舍五入算法
     *
     * @param num 被格式化的小数
     * @return 保留两位小数的数字
     */
    public String floatFormat(float num) {
        String numStr = String.valueOf(num);
        Double d = Double.parseDouble(this.getDigit(numStr));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(d);
    }

    /**
     * 正则匹配提取
     * 自动提取 保留两位小数
     *
     * @param numStr 数字字符串
     * @return 两位小数的数字字符串或者整数字符串
     */
    private String getDigit(String numStr) {
        Pattern p = Pattern.compile("\\d*\\.{0,1}\\d{0,2}");
        Matcher m = p.matcher(numStr);
        boolean result = m.find();
        String find_result = null;
        if (result) {
            find_result = m.group();
        }
        return find_result;
    }

}
