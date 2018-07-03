/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */
package ${basePackage}.core.tools;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    /**
     * @param path
     * @return
     * @Enclosing_Method : filtPath
     * @Written by : czq
     * @Creation Date : Sep 20, 2010 9:37:33 PM
     * @version : v1.00
     * @Description : 过滤路径，把路径过滤成形如“/aa/bb”的形式
     */
    public static String filtPath(String path) {
        path = path.replace("\\", "/");
        path = path.replace("//", "/");

        if (path.lastIndexOf("/") == path.length()) {
            path = path.substring(path.length() - 1, path.length());
        }
        return path;
    }

    /**
     * @param sourceFileName 源文件名
     * @param targetFileName 目标文件名
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:13:47 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(String sourceFileName, String targetFileName)
            throws IOException {
        File sourceFile = new File(targetFileName);
        File targetFile = new File(sourceFileName);
        copyFile(sourceFile, targetFile);
    }

    /**
     * @param sourceFile     源文件
     * @param targetFileName 目标文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:13:18 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(File sourceFile, String targetFileName)
            throws IOException {
        File file = new File(targetFileName);
        copyFile(sourceFile, file);
    }

    /**
     * @param sourceFileName 源文件名
     * @param targetFile     目标文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:12:34 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(String sourceFileName, File targetFile)
            throws IOException {
        File file = new File(sourceFileName);
        copyFile(file, targetFile);
    }

    /**
     * @param input      源文件
     * @param targetFile 目录文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:08:04 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(FileInputStream input, File targetFile)
            throws IOException {
        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        //相互copy
        copyFile(input, output);
    }

    /**
     * @param input          源文件
     * @param targetFileName 目录文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:08:04 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(InputStream input, String targetFileName)
            throws IOException {
        // 新建文件输出流并对它进行缓冲
        File targetFile = new File(targetFileName);
        FileOutputStream output = new FileOutputStream(targetFile);
        //相互copy
        copyFile(input, output);
    }

    /**
     * @param sourceFile 源文件
     * @param targetFile 目录文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:08:04 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        //相互copy
        copyFile(input, output);
    }

    /**
     * @param input  源文件
     * @param output 目录文件
     * @throws IOException
     * @Enclosing_Method : copyFile
     * @Written by : czq
     * @Creation Date : Oct 23, 2010 5:08:04 PM
     * @version : v1.00
     * @Description : 复制文件
     */
    public static void copyFile(InputStream input, FileOutputStream output)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        BufferedInputStream inBuff = new BufferedInputStream(input);
        // 新建文件输出流并对它进行缓冲
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }


    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }


    /**
     * @param sourceDir 　源目录
     * @param targetDir 　目标目录
     * @throws IOException
     * @Enclosing_Method : copyDirectiory
     * @Written by        : czq
     * @Creation Date     : Oct 23, 2010 5:14:36 PM
     * @version : v1.00
     * @Description : 复制目录
     **/
    public static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir)
                        .getAbsolutePath()
                        + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 删除文件夹和其下所有文件
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件（不删除指定文件夹）
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean delFile(String path) {
        boolean flag = false;
        if (StringUtils.isNotBlank(path)) {
            File temp = new File(path);
            if (temp.exists() && temp.isFile()) {
                temp.delete();
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 下载文件
     *
     * @param remoteFilePath 远端文件路径
     * @param localFilePath  本地保存文件路径
     */
    public static void downloadFile(String remoteFilePath, String localFilePath) {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        try {
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
