package ${basePackage}.core.tools.memcached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * 工具类：利用yuicompressor来对js、css文件进行压缩
 *
 * @author lixin
 */
public class CompressorUtil {
    private final static Logger logger = LoggerFactory.getLogger(CompressorUtil.class);
    /**
     * main方法
     *
     * @param args
     */
    private static String rootPath = null;

    public static void main(String[] args) {
        /**
         * java -jar c:\yuicompressor-2.4.2.jar --type js --charset utf-8 d:\test01.js -o d:\test-min.js
         * 压缩时，将这里的：
         * yuiPath替换成你机器上yuicompress文件的路径；
         * filePath替换成要压缩的文件或文件夹路径；
         * fileType替换成要压缩的文件类型(扩展名)，只能是js、css两种类型；
         * encoding替换成你要读取文件的编码格式
         */
        String yuiPath = "E:\\borong_workspace\\dtb_portal\\src\\com\\rzhkj\\rbac\\common\\memcached\\yuicompressor-2.4.2.jar";//压缩jar的位置
        String filePath = "E:\\borong_workspace\\dtb_portal\\WebRoot\\js";//压缩文件根目录
        String fileType = "js,css";
        String encoding = "utf-8";
        createMinDir(filePath);
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        compressFile(yuiPath, sb, file, encoding);
        String[] res = sb.toString().split("/n");
        Runtime runTime = Runtime.getRuntime();
        Date startTime = new Date();
        Long count = 0L;
        for (String cmd : res) {
            if (!cmd.trim().equals("")) {
                logger.info(cmd);
                try {
                    runTime.exec(cmd);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Date endTime = new Date();
        Long cost = endTime.getTime() - startTime.getTime();

        logger.info(fileType.toUpperCase() + "压缩完成，耗时：" + cost + "ms，" + cost / 1000 / 60 + "秒，共压缩文件个数：" + count);
    }


    /**
     * 递归压缩文件
     */
    public static void compressFile(String yuiPath, StringBuilder sb, File f, String encoding) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            // 如果某个文件夹是空文件夹，则跳过
            if (files == null) {
                return;
            }
            for (File file : files) {
                compressFile(yuiPath, sb, file, encoding);
            }
        } else {
            String fileName = f.getName();
            if (fileName.endsWith(".js") && !fileName.contains("min.js")) {
                sb.append("java -jar ");
                sb.append(yuiPath);
                sb.append(" --type js --charset ");
                sb.append(encoding + " " + f.getPath());
                sb.append(" -o " + f.getPath().replace(rootPath, rootPath + "_min"));
                sb.append("/n");
            } else if (fileName.endsWith(".css") && !fileName.contains("min.css")) {
                sb.append("java -jar ");
                sb.append(yuiPath);
                sb.append(" --type css --charset ");
                sb.append(encoding + " " + f.getPath());
                sb.append(" -o " + f.getPath().replace(rootPath, rootPath + "_min"));
                sb.append("/n");
            } else {
                copy(f, f.getPath().replace(rootPath, rootPath + "_min"));
            }
        }
    }


    /**
     * 递归创建目录
     *
     * @param path
     */
    public static void createMinDir(String path) {
        if (rootPath == null) {
            rootPath = path;
        }
        File fileSrc = new File(path);
        File fileDes = new File(path.replace(rootPath, rootPath + "_min"));
        if (!fileDes.exists()) {
            fileDes.mkdir();
        }
        File[] files = fileSrc.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                createMinDir(file.getPath());
            }
        }
    }

    /**
     * 复制文件
     *
     * @param oldfile
     * @param newPath
     */
    public static void copy(File oldfile, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
