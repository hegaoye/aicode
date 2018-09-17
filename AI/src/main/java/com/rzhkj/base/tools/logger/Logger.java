package com.rzhkj.base.tools.logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志收集与读取
 */
public enum Logger {
    Instance;

    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 将信息记录到日志文件
     *
     * @param projectName 项目名
     * @param msg         信息
     * @throws IOException
     */
    public void addLog(String projectName, String msg) throws IOException {
        if (projectName == null) {
            throw new IllegalStateException("logFile can not be null!");
        }
        File log = new File("/projectName.log");
        Writer txtWriter = new FileWriter(log, true);
        txtWriter.write("[" + dateFormat.format(new Date()) + "]\t" + msg + "\n");
        txtWriter.flush();
    }


    /**
     * 实时输出日志信息
     *
     * @param projectName      项目名
     * @param lastTimeFileSize 上次日志读取位置
     * @throws IOException
     */
    public Log viewLog(String projectName, long lastTimeFileSize) throws IOException {
        //指定文件可读可写
        File log = new File("/projectName.log");
        RandomAccessFile randomFile = new RandomAccessFile(log, "rw");
        //定位到最后一次 读取的位置
        randomFile.seek(lastTimeFileSize);
        String tmp = "";
        String msg = "";
        while ((tmp = randomFile.readLine()) != null) {
            msg = new String(tmp.getBytes("ISO8859-1"));
        }
        return new Log(msg, randomFile.length());
    }

}
