package com.aicode.base.core.typemapping;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Lixin
 * @email hegaoye@qq.com
 */
public class DatabaseDataTypesUtils {

    public static void main(String[] args) {
        System.out.println(getPreferredJavaType("varchar", 0, 0));
    }

    private final static Map<String, String> _preferredJavaTypeForSqlType = new HashMap<>();

    public static boolean isFloatNumber(String javaType) {
        if (javaType.endsWith("Float") || javaType.endsWith("Double") || javaType.endsWith("BigDecimal") || javaType.endsWith("BigInteger")) {
            return true;
        }
        if (javaType.endsWith("float") || javaType.endsWith("double") || javaType.endsWith("BigDecimal") || javaType.endsWith("BigInteger")) {
            return true;
        }
        return false;
    }

    public static boolean isIntegerNumber(String javaType) {
        if (javaType.endsWith("Long") || javaType.endsWith("Integer") || javaType.endsWith("Short") || javaType.endsWith("Byte")) {
            return true;
        }
        if (javaType.endsWith("long") || javaType.endsWith("int") || javaType.endsWith("short") || javaType.endsWith("byte")) {
            return true;
        }
        return false;
    }

    public static boolean isPrimaryKey(String javaType) {
        if (javaType.contains("PRI") || javaType.endsWith("pri") || javaType.equalsIgnoreCase("PRI")) {
            return true;
        }

        return false;
    }

    public static boolean isDate(String javaType) {
        if (javaType.endsWith("Date") || javaType.endsWith("Timestamp") || javaType.endsWith("Time")) {
            return true;
        }
        if (javaType.equalsIgnoreCase("date")
                || javaType.equalsIgnoreCase("datetime")
                || javaType.equalsIgnoreCase("timestamp")
                || javaType.equalsIgnoreCase("time")) {
            return true;
        }
        return false;
    }

    public static boolean isStateOrType(String javaType) {
        if (javaType.contains("state") || javaType.contains("State")) {
            return true;
        }
        if (javaType.contains("status") || javaType.contains("Status")) {
            return true;
        }
        return false;
    }


    public static boolean isString(String javaType) {
        if (javaType.endsWith("String")) {
            return true;
        }
        return false;
    }

    /**
     * sqlType 转成java类型
     *
     * @param sqlType       sql类型
     * @param size          大小
     * @param decimalDigits 几位数字
     * @return String
     */
    public static String getPreferredJavaType(String sqlType, int size,
                                              int decimalDigits) {
        if ((sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric"))
                && decimalDigits == 0) {
            if (size == 1) {
                // https://sourceforge.net/tracker/?func=detail&atid=415993&aid=662953&group_id=36044
                return "java.lang.Boolean";
            } else if (size < 3) {
                return "java.lang.Byte";
            } else if (size < 5) {
                return "java.lang.Short";
            } else if (size < 10) {
                return "java.lang.Integer";
            } else if (size < 19) {
                return "java.lang.Long";
            } else {
                return "java.math.BigDecimal";
            }
        }
        String result = _preferredJavaTypeForSqlType.get(sqlType);
        if (result == null) {
            result = "java.lang.Object";
        }
        return result;
    }

    /**
     * sqlType 转成java类型
     *
     * @param sqlType sql类型
     * @return String
     */
    public static String getPreferredJavaType(String sqlType) {
        String result = _preferredJavaTypeForSqlType.get(sqlType);
        if (result == null) {
            result = "java.lang.Object";
        }
        return result;
    }

    static {
        //常用类型
        _preferredJavaTypeForSqlType.put("tinyint", "java.lang.Byte");
        _preferredJavaTypeForSqlType.put("smallint", "java.lang.Short");
        _preferredJavaTypeForSqlType.put("integer", "java.lang.Integer");
        _preferredJavaTypeForSqlType.put("int", "java.lang.Integer");
        _preferredJavaTypeForSqlType.put("bigint", "java.lang.Long");
        _preferredJavaTypeForSqlType.put("real", "java.lang.Float");
        _preferredJavaTypeForSqlType.put("float", "java.lang.Float");
        _preferredJavaTypeForSqlType.put("double", "java.lang.Double");
        _preferredJavaTypeForSqlType.put("decimal", "java.math.BigDecimal");
        _preferredJavaTypeForSqlType.put("numeric", "java.math.BigDecimal");
        _preferredJavaTypeForSqlType.put("bit", "java.lang.Boolean");
        _preferredJavaTypeForSqlType.put("boolean", "java.lang.Boolean");
        _preferredJavaTypeForSqlType.put("char", "java.lang.String");
        _preferredJavaTypeForSqlType.put("text", "java.lang.String");
        _preferredJavaTypeForSqlType.put("varchar", "java.lang.String");
        _preferredJavaTypeForSqlType.put("enum", "java.lang.String");
        // according to resultset.gif, we should use java.io.Reader, but String is more convenient for EJB

        _preferredJavaTypeForSqlType.put("longvarchar", "java.lang.String");
        _preferredJavaTypeForSqlType.put("binary", "byte[]");
        _preferredJavaTypeForSqlType.put("varbinary", "byte[]");
        _preferredJavaTypeForSqlType.put("longvarbinary", "byte[]");
        _preferredJavaTypeForSqlType.put("date", "java.util.Date");
//        _preferredJavaTypeForSqlType.put("date", "java.sql.Date");
        _preferredJavaTypeForSqlType.put("time", "java.util.Date");
//        _preferredJavaTypeForSqlType.put("time", "java.sql.Time");
        _preferredJavaTypeForSqlType.put("datetime", "java.util.Date");
//        _preferredJavaTypeForSqlType.put("datetime", "java.sql.Date");
        _preferredJavaTypeForSqlType.put("timestamp", "java.util.Date");
//        _preferredJavaTypeForSqlType.put("timestamp", "java.sql.Timestamp");
        _preferredJavaTypeForSqlType.put("clob", "java.sql.Clob");
        _preferredJavaTypeForSqlType.put("blob", "java.sql.Blob");
        _preferredJavaTypeForSqlType.put("array", "java.sql.Array");
        _preferredJavaTypeForSqlType.put("ref", "java.sql.Ref");
        _preferredJavaTypeForSqlType.put("struct", "java.lang.Object");
        _preferredJavaTypeForSqlType.put("java_object", "java.lang.Object");
    }


}
