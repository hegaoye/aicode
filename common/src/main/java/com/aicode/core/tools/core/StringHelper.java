package com.aicode.core.tools.core;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lixin
 * @email hegaoye@qq.com
 */
public class StringHelper {

    private static final Map<String, String> XML = new HashMap<String, String>();

    static {
        XML.put("apos", "'");
        XML.put("quot", "\"");
        XML.put("amp", "&");
        XML.put("lt", "<");
        XML.put("gt", ">");
    }

    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String escapedStr = getEscapedStringByChar(c);
            if (escapedStr == null) {
                sb.append(c);
            } else {
                sb.append(escapedStr);
            }
        }
        return sb.toString();
    }

    public static String escapeXml(String str, String escapeChars) {
        if (str == null) {

            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (escapeChars.indexOf(c) < 0) {
                sb.append(c);
                continue;
            }
            String escapedStr = getEscapedStringByChar(c);
            if (escapedStr == null) {
                sb.append(c);
            } else {
                sb.append(escapedStr);
            }
        }
        return sb.toString();
    }

    private static String getEscapedStringByChar(char c) {
        String escapedStr = null;
        for (String key : XML.keySet()) {
            String value = XML.get(key);
            if (c == value.charAt(0)) {
                escapedStr = "&" + key + ";";
            }
        }
        return escapedStr;
    }

    public static String removePrefix(String str, String prefix) {
        return removePrefix(str, prefix, false);
    }

    public static String removePrefix(String str, String prefix, boolean ignoreCase) {
        if (str == null) {
            return null;
        }
        if (prefix == null) {
            return str;
        }
        if (ignoreCase) {
            if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
                return str.substring(prefix.length());
            }
        } else {
            if (str.startsWith(prefix)) {
                return str.substring(prefix.length());
            }
        }
        return str;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean contains(String str, String... keywords) {
        if (str == null) {
            return false;
        }
        if (keywords == null) {
            throw new IllegalArgumentException("'keywords' must be not null");
        }

        for (String keyword : keywords) {
            if (str.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String makeAllWordFirstLetterUpperCase(String sqlName) {
        if (sqlName == null) {
            return null;
        }
        String[] strs = sqlName.toLowerCase().split("_");
        String result = "";
        String preStr = "";
        for (int i = 0; i < strs.length; i++) {
            if (preStr.length() == 1) {
                result += strs[i];
            } else {
                result += capitalize(strs[i]);
            }
            preStr = strs[i];
        }
        return result;
    }

    public static String toJavaVariableName(String str) {
        return uncapitalize(toJavaClassName(str));
    }

    /**
     * 转换为java类格式
     *
     * @param str 字符串，默认是tableName  以"_" 分割转化
     * @return String
     */
    public static String toJavaClassName(String str) {
        return makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(str));
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();
        int pos = 0;
        int index = inString.indexOf(oldPattern);
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));
        return sbuf.toString();
    }


    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }


    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     * copy from spring
     */
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    private static final Random RANDOM = new Random();

    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it
                        // in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting
                        // it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     *
     * @param filteredName the string containing original name
     * @return the converted name
     */
    public static String toUnderscoreName(String filteredName) {
        if (filteredName == null) {
            return null;
        }

        if (filteredName.indexOf("_") >= 0 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }
        if (filteredName.indexOf("_") == -1 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }

        StringBuffer result = new StringBuffer();
        if (filteredName != null && filteredName.length() > 0) {
            result.append(filteredName.substring(0, 1).toLowerCase());
            for (int i = 1; i < filteredName.length(); i++) {
                String preChart = filteredName.substring(i - 1, i);
                String c = filteredName.substring(i, i + 1);
                if (c.equals("_")) {
                    result.append("_");
                    continue;
                }
                if (preChart.equals("_")) {
                    result.append(c.toLowerCase());
                    continue;
                }
                if (c.matches("\\d")) {
                    result.append(c);
                } else if (c.equals(c.toUpperCase())) {
                    result.append("_");
                    result.append(c.toLowerCase());
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 将string转换为List<ColumnEnum> 格式为: "enumAlias(enumKey,enumDesc)"
     */
    static Pattern three = Pattern.compile("(.*)\\((.*),(.*)\\)");
    static Pattern two = Pattern.compile("(.*)\\((.*)\\)");

    public static String join(List list, String seperator) {
        return join(list.toArray(new Object[0]), seperator);
    }

    public static String replace(int start, int end, String str, String replacement) {
        String before = str.substring(0, start);
        String after = str.substring(end);
        return before + replacement + after;
    }

    public static String join(Object[] array, String seperator) {
        if (array == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i != array.length - 1) {
                result.append(seperator);
            }
        }
        return result.toString();
    }

    public static String getByRegex(String str, String regex) {
        return getByRegex(str, regex, 0);
    }

    public static String getByRegex(String str, String regex, int group) {
        if (regex == null) {
            throw new NullPointerException();
        }
        if (group < 0) {
            throw new IllegalArgumentException();
        }
        if (str == null) {
            return null;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(group);
        }
        return null;
    }
}
