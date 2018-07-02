/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.tools.excel;

import ${basePackage}.core.tools.DateTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * excel导入导出工具类
 * Created by lixin on 2017/6/16.
 */
public class ExcelUtils {

    private final static Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private final static String EXCEL2003 = "xls";
    private final static String EXCEL2007 = "xlsx";


    /**
     * 读取excel文件
     *
     * @param path 本地文件路径
     * @param cls  返回集合类型
     * @return
     */
    public static <T> List<T> readExcel(String path, Class<T> cls) {
        List<T> dataList = new ArrayList<>();

        Workbook workbook = null;
        try {
            if (path.endsWith(EXCEL2007)) {
                FileInputStream is = new FileInputStream(new File(path));
                workbook = new XSSFWorkbook(is);
            }
            if (path.endsWith(EXCEL2003)) {
                FileInputStream is = new FileInputStream(new File(path));
                workbook = new HSSFWorkbook(is);
            }
            if (workbook != null) {
                //类映射  注解 value-->bean columns
                Map<String, List<Field>> classMap = new HashMap<>();
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    if (annotation != null) {
                        String value = annotation.value();
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }
                        if (!classMap.containsKey(value)) {
                            classMap.put(value, new ArrayList<>());
                        }
                        field.setAccessible(true);
                        classMap.get(value).add(field);
                    }
                }
                //索引-->columns
                Map<Integer, List<Field>> reflectionMap = new HashMap<>();
                Sheet sheet = workbook.getSheetAt(0);//默认读取第一个sheet

                boolean firstRow = true;
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);

                    if (firstRow) {//首行  提取注解
                        for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j);
                            String cellValue = getCellValue(cell);
                            if (classMap.containsKey(cellValue)) {
                                reflectionMap.put(j, classMap.get(cellValue));
                            }
                        }
                        firstRow = false;
                    } else {
                        if (row == null) {//忽略空白行
                            continue;
                        }

                        try {
                            T t = cls.newInstance();
                            boolean allBlank = true;//判断是否为空白行
                            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                                if (reflectionMap.containsKey(j)) {
                                    Cell cell = row.getCell(j);
                                    String cellValue = getCellValue(cell);
                                    if (StringUtils.isNotBlank(cellValue)) {
                                        allBlank = false;
                                    }
                                    List<Field> fieldList = reflectionMap.get(j);
                                    for (Field field : fieldList) {
                                        try {
                                            handleField(t, cellValue, field);
                                        } catch (Exception e) {
                                            log.error(String.format("reflect field:%s value:%s exception!", field.getName(), cellValue), e);
                                        }
                                    }
                                }
                            }
                            if (!allBlank) {
                                dataList.add(t);
                            } else {
                                log.warn(String.format("row:%s is blank ignore!", i));
                            }
                        } catch (Exception e) {
                            log.error(String.format("parse row:%s exception!", i), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("parse excel exception!"), e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                }
            }
        }
        return dataList;
    }

    /**
     * 数据过滤,内部分使用
     *
     * @param t
     * @param value
     * @param field
     * @param <T>
     * @throws Exception
     */
    private static <T> void handleField(T t, String value, Field field) throws Exception {
        Class<?> type = field.getType();
        if (type == null || type == void.class || StringUtils.isBlank(value)) {
            return;
        }
        if (type == Object.class) {
            field.set(t, value);
        } else if (type.getSuperclass() == null || type.getSuperclass() == Number.class) {//数字类型
            if (type == int.class || type == Integer.class) {
                field.set(t, NumberUtils.toInt(value));
            } else if (type == long.class || type == Long.class) {
                field.set(t, NumberUtils.toLong(value));
            } else if (type == byte.class || type == Byte.class) {
                field.set(t, NumberUtils.toByte(value));
            } else if (type == short.class || type == Short.class) {
                field.set(t, NumberUtils.toShort(value));
            } else if (type == double.class || type == Double.class) {
                field.set(t, NumberUtils.toDouble(value));
            } else if (type == float.class || type == Float.class) {
                field.set(t, NumberUtils.toFloat(value));
            } else if (type == char.class || type == Character.class) {
                field.set(t, CharUtils.toChar(value));
            } else if (type == boolean.class) {
                field.set(t, BooleanUtils.toBoolean(value));
            } else if (type == BigDecimal.class) {
                field.set(t, new BigDecimal(value));
            }
        } else if (type == Boolean.class) {
            field.set(t, BooleanUtils.toBoolean(value));
        } else if (type == Date.class) {
            field.set(t, DateTools.stringToDate(value));
        } else if (type == String.class) {
            field.set(t, value);
        } else {
            Constructor<?> constructor = type.getConstructor(String.class);
            field.set(t, constructor.newInstance(value));
        }
    }

    /**
     * 获取列值,内部使用
     *
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return DateTools.format((DateUtil.getJavaDate(cell.getNumericCellValue())));
            } else {
                return new BigDecimal(cell.getNumericCellValue()).toString();
            }
        } else if (cell.getCellType() == cell.CELL_TYPE_STRING) {
            return StringUtils.trimToEmpty(cell.getStringCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_FORMULA) {
            return StringUtils.trimToEmpty(cell.getCellFormula());
        } else if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
            return "";
        } else if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_ERROR) {
            return "ERROR";
        } else {
            return cell.toString().trim();
        }

    }

    /**
     * 导出excel
     *
     * @param path      文件生成路径
     * @param dataList  要导出的数据
     * @param cls       对应集合类型
     * @param sheetName sheet名称
     */
    public static <T> void writeExcel(String path, List<T> dataList, Class<T> cls, String sheetName) {
        Field[] fields = cls.getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields).filter(field -> {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null && annotation.col() > 0) {
                field.setAccessible(true);
                return true;
            }
            return false;
        }).sorted(Comparator.comparing(field -> {
            int col = 0;
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
                col = annotation.col();
            }
            return col;
        })).collect(Collectors.toList());


        Workbook wb = new XSSFWorkbook();
        String name = StringUtils.isBlank(sheetName) ? "数据信息" : sheetName;
        Sheet sheet = wb.createSheet(name);
        AtomicInteger ai = new AtomicInteger();

        {
            Row row = sheet.createRow(ai.getAndIncrement());
            AtomicInteger aj = new AtomicInteger();

            //写入头部
            fieldList.forEach(field -> {
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                String columnName = "";
                int col = 0, width = 0;
                if (annotation != null) {
                    columnName = annotation.value();
                    col = annotation.col();
                    width = annotation.width();
                }
                sheet.setColumnWidth(col - 1, 256 * width + 184);//设置列宽
                Cell cell = row.createCell(aj.getAndIncrement());

                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
                cellStyle.setBorderRight(BorderStyle.THIN);//右边框
                cellStyle.setLeftBorderColor(HSSFColor.GREY_40_PERCENT.index);
                cellStyle.setRightBorderColor(HSSFColor.GREY_40_PERCENT.index);
                Font font = wb.createFont();
                font.setBold(true);
                cellStyle.setFont(font);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(columnName);
            });
        }

        if (CollectionUtils.isNotEmpty(dataList)) {
            dataList.forEach(t -> {
                Row row = sheet.createRow(ai.getAndIncrement());
                AtomicInteger aj = new AtomicInteger();

                fieldList.forEach(field -> {
                    Class<?> type = field.getType();
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    int color = annotation.color(); //字体颜色
                    Object value = "";
                    try {
                        value = field.get(t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Cell cell = row.createCell(aj.getAndIncrement());

                    if (value != null) {
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        if (color != HSSFColor.BLACK.index) {
                            Font font = wb.createFont();
                            font.setColor((short) color);
                            cellStyle.setFont(font);
                        }
                        if (type == Date.class) {
                            cell.setCellValue(DateTools.format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                        cell.setCellStyle(cellStyle);
                    }
                });
            });

        }
        //冻结窗格
        wb.getSheet(name).createFreezePane(0, 1, 0, 1);

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}