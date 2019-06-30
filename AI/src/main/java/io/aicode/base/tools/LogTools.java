package io.aicode.base.tools;

import io.aicode.base.core.model.java.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jiyan on 2018/9/16.
 */
public class LogTools {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 将信息记录到日志文件
     *
     * @param mesInfo 信息
     * @throws IOException
     */
    public static void logMsg(String mesInfo, String filePath) throws IOException {
        System.out.print(new HandleFuncs().getCurrentClassPath());
        File file = new File(filePath);
        BufferedWriter txtWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
        txtWriter.write(dateFormat.format(new Date()) + "\t" + mesInfo + "\n");
        txtWriter.flush();
        //txtWriter.close();
    }

    public static void main(String[] args) throws Exception {
        logMsg("aa", "a");
    }


    /**
     * 实时输出日志信息
     *
     * @throws IOException
     */
    public static Log realtimeShowLog(String filePath, long lastTimeFileSize) throws IOException {
        File file = new File(filePath);
        //指定文件可读可写
        RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
        randomFile.seek(lastTimeFileSize);
        String tmp;
        String logStr = null;
        while ((tmp = randomFile.readLine()) != null) {
            logStr += new String(tmp.getBytes("ISO8859-1"), "utf-8") + '\n';
        }
        Log log = new Log();
        log.setLogInfo(logStr);
        log.setLastTimeFileSize(randomFile.length());
        return log;
    }


    public static void info(String msg, String filePath) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH-mm-ss");
        String dateString = df.format(new Date());
        File file;
        try {
            file = new File(filePath);
            BufferedWriter oStreamWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            BufferedWriter writer = new BufferedWriter(oStreamWriter);
            String content = "[" + dateString + "] " + msg + "\r\n";
            writer.write(content);
            writer.write(content);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
