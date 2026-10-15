package com.aicode.core.tools.core.typemapping;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Lixin
 * @email hegaoye@qq.com
 */
public class DatabaseDataTypesUtils {

    // 五个 isXxx 判定方法均对 null 入参返回 false。
    // 背景：H2 2.x 移除了 INFORMATION_SCHEMA.COLUMNS.TYPE_NAME，colum.xml 改用 DATA_TYPE；
    // 若查询未用 AS 显式指定别名，Column.typeName 会静默为 null，原实现直接调用
    // javaType.endsWith(...) 抛 NPE —— 把 SQL 层的错误伪装成空指针，极难定位。
    // 回归守护见 aicode 的 TableMapperIT。

    private final static Map<String, String> _preferredJavaTypeForSqlType = new HashMap<>();

        public static boolean isFloatNumber(String javaType) {
        if (javaType == null) {
            return false;
        }
        if (javaType.endsWith("Float") || javaType.endsWith("Double") || javaType.endsWith("BigDecimal") || javaType.endsWith("BigInteger")) {
            return true;
        }
        if (javaType.endsWith("float") || javaType.endsWith("double") || javaType.endsWith("BigDecimal") || javaType.endsWith("BigInteger")) {
            return true;
        }
        return false;
    }

        public static boolean isIntegerNumber(String javaType) {
        if (javaType == null) {
            return false;
        }
        if (javaType.endsWith("Long") || javaType.endsWith("Integer") || javaType.endsWith("Short") || javaType.endsWith("Byte")) {
            return true;
        }
        if (javaType.endsWith("long") || javaType.endsWith("int") || javaType.endsWith("short") || javaType.endsWith("byte")) {
            return true;
        }
        return false;
    }

        public static boolean isPrimaryKey(String javaType) {
        if (javaType == null) {
            return false;
        }
        if (javaType.contains("PRI")
                || javaType.endsWith("pri")
                || javaType.equalsIgnoreCase("PRI")
                || javaType.equalsIgnoreCase("NO")) {
            return true;
        }

        return false;
    }

        public static boolean isDate(String javaType) {
        if (javaType == null) {
            return false;
        }
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
        if (javaType == null) {
            return false;
        }
        if (javaType.contains("state") || javaType.contains("State")) {
            return true;
        }
        if (javaType.contains("status") || javaType.contains("Status")) {
            return true;
        }
        return false;
    }

    /**
     * sqlType 转成java类型
     *
     * @param sqlType sql类型
     * @return String
     */
    public static String getPreferredJavaType(String sqlType) {
        if (sqlType == null) {
            return "java.lang.Object";
        }
        String result = _preferredJavaTypeForSqlType.get(sqlType.toLowerCase());
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
