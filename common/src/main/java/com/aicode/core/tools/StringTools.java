package com.aicode.core.tools;

import com.alibaba.fastjson2.JSON;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTools {

    //身份证号码验证 begin

    //第18位校验码，余数对应正确的末尾数字
    private final static Map<String, String> checkCodeMap = new HashMap<>();
    //权重值，身份证的每一位，与对应次序的数字进行相乘
    private final static Integer[] factorArr = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    //省份编码
    private final static String cityCode[] = {"11", "12", "13", "14", "15", "21", "22",
            "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
            "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63",
            "64", "65", "71", "81", "82", "91"};

    static {
        //初始化 各位与权重计算所得余数对应正确的末尾数字
        checkCodeMap.put("0", "1");
        checkCodeMap.put("1", "0");
        checkCodeMap.put("2", "Xx");
        checkCodeMap.put("3", "9");
        checkCodeMap.put("4", "8");
        checkCodeMap.put("5", "7");
        checkCodeMap.put("6", "6");
        checkCodeMap.put("7", "5");
        checkCodeMap.put("8", "4");
        checkCodeMap.put("9", "3");
        checkCodeMap.put("10", "2");
    }

    /**
     * 判断字符串不为空
     *
     * @param strs
     * @return boolean
     */
    public static boolean isNotEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }
        boolean flag = false;
        for (String str : strs) {
            if (StringUtils.isNotBlank(str)) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 判断字符串为空
     *
     * @param strs
     * @return boolean
     */
    public static boolean isEmpty(String... strs) {
        return !isNotEmpty(strs);
    }

    public static List<String> getByPattern(String str, String patternStr) {
        // null 安全：Pattern.matcher(null) 会抛 NPE（"Cannot invoke
        // CharSequence.length() because this.text is null"）。
        //
        // 该路径是真实缺陷的入口：ProjectServiceImpl.parse() 对每一列无条件调
        // getStateOrType(column.getRemarks())，而 remarks 在用户没写 COMMENT 时为 null
        // → 整个逆解析失败。2026-10-18 由新增的 InitChainIT（真实 SQL 走一遍）抓到。
        //
        // 语义：null 即「没有可匹配的内容」→ 返回空列表，与空串一致。
        if (str == null) {
            return new ArrayList<>();
        }
        Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    /**
     * 获取给定字符串可以转换为 key-value的形式
     *
     * @param text 文本
     * @return 结果
     */
    public static Map<String, Object> getStateOrType(String text) {
        //正则表达式 匹配 【汉字 英文】 或 【英文 汉字】或【英文 数字】 或 【数字 英文】的格式
        String reg = "[\\u4e00-\\u9fa5]+\\s?\\w+|\\w+\\s?[\\u4e00-\\u9fa5]+|[a-zA-Z]+\\s?\\-?\\d+|\\-?\\d+\\s?[a-zA-Z]+";
        //中文
        String regCn = "[\\u4e00-\\u9fa5]+";
        //英文
        String regEn = "[a-zA-z]+";
        //数字
        String regDigit = "\\-?\\d+";
        List<String> matchList = StringTools.getByPattern(text, reg);
        Map<String, Object> map = new HashMap<>();
        for (String str : matchList) {
            List<String> cnList = StringTools.getByPattern(str, regCn);
            String cn = null;
            if (cnList != null && !cnList.isEmpty()) {
                cn = cnList.get(0);
            }

            List<String> enList = StringTools.getByPattern(str, regEn);
            String en = null;
            if (enList != null && !enList.isEmpty()) {
                en = enList.get(0);
            }

            List<String> digitList = StringTools.getByPattern(str, regDigit);
            String d = null;
            if (digitList != null && !digitList.isEmpty()) {
                d = digitList.get(0);
            }
            String key = null;
            Object value = null;

            if (StringUtils.isNotBlank(cn) && StringUtils.isNotBlank(en)) {
                key = en;
                value = cn;
            } else if (StringUtils.isNotBlank(en) && StringUtils.isNotBlank(d)) {
                key = en;
                value = d;
            }
            if (key != null) {
                map.put(key, value);
            }

        }
        return map;
    }

    /***
     * 驼峰命名转为“-”命名
     *
     * @param para 驼峰命名的字符串
     */
    public static String humpToLine(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//定位
        for (int i = 0; i < para.length(); i++) {
            if (Character.isUpperCase(para.charAt(i))) {
                sb.insert(i + temp, "-");
                temp += 1;
            }
        }
        String result = sb.toString();
        if (result.startsWith("-")) {
            result = result.replaceFirst("-", "");
        }
        return result.toLowerCase();
    }


}
