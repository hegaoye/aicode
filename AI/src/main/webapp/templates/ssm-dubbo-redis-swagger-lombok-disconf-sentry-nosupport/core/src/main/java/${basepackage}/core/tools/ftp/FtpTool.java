/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.tools.ftp;

import ${basePackage}.core.Env;
import ${basePackage}.core.tools.ConfigUtil;
import ${basePackage}.core.tools.FileUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * ftp管理工具
 * 1.文件删除
 * 2.文件上传
 * 3.文件下载
 */
public class FtpTool {
    private final static Logger log = LoggerFactory.getLogger(FtpTool.class);
    public static final int BINARY_FILE_TYPE = FTP.BINARY_FILE_TYPE;
    public static final int ASCII_FILE_TYPE = FTP.ASCII_FILE_TYPE;
    private static FTPClientPool ftpClientPool = null;
    private FTPClient ftpClient;
    private static String ftpHost = null;
    private static Integer port = null;
    private static String username = null;
    private static String userpwd = null;
    private static String host_port = null;

    public static FtpTool instance;

    public static FtpTool getInstance() {
        return instance == null ? new FtpTool() : instance;
    }

    public FtpTool() {
        ftpClient = getFtpConnected();
    }

    static {
        switch (Env.env) {
            case DEVELOP:
                log.info("----------图片访问路径 开启开发环境-------------");
                ftpHost = (String) ConfigUtil.getValue("host_ip_dev", "upload_config.properties");
                username = (String) ConfigUtil.getValue("username_dev", "upload_config.properties");
                userpwd = (String) ConfigUtil.getValue("userpwd_dev", "upload_config.properties");
                host_port = (String) ConfigUtil.getValue("http_port_dev", "upload_config.properties");
                log.info("ftpHost::" + ftpHost);
                log.info("username::" + username);
                log.info("userpwd::" + userpwd);
                log.info("host_port::" + host_port);
                log.info("----------图片访问路径 开启开发环境-------------");
                break;
            case SANDBOX:
                log.info("----------图片访问路径 开启测试环境-------------");
                ftpHost = (String) ConfigUtil.getValue("host_ip_sandbox", "upload_config.properties");
                username = (String) ConfigUtil.getValue("username_sandbox", "upload_config.properties");
                userpwd = (String) ConfigUtil.getValue("userpwd_sandbox", "upload_config.properties");
                host_port = (String) ConfigUtil.getValue("http_port_sandbox", "upload_config.properties");
                log.info("ftpHost::" + ftpHost);
                log.info("username::" + username);
                log.info("userpwd::" + userpwd);
                log.info("host_port::" + host_port);
                log.info("----------图片访问路径 开启测试环境-------------");
                break;
            case PRODUCT:
                log.info("----------图片访问路径 开启生产环境-------------");
                ftpHost = (String) ConfigUtil.getValue("host_ip_product", "upload_config.properties");
                username = (String) ConfigUtil.getValue("username_product", "upload_config.properties");
                userpwd = (String) ConfigUtil.getValue("userpwd_product", "upload_config.properties");
                host_port = (String) ConfigUtil.getValue("http_port_product", "upload_config.properties");
                log.info("ftpHost::" + ftpHost);
                log.info("username::" + username);
                log.info("userpwd::" + userpwd);
                log.info("host_port::" + host_port);
                log.info("----------图片访问路径 开启生产环境-------------");
                break;
        }
        if (port == null) {
            port = Integer.parseInt(ConfigUtil.getValue("host_port", "upload_config.properties"));
        }
    }

    /**
     * 链接ftp
     *
     * @return ftp对象
     */
    public static FTPClient getFtpConnected() {
        FTPClient ftpClient = null;
        try {
            if (ftpClientPool == null) {
                ftpClientPool = new FTPClientPool(ftpHost, port, username, userpwd, BINARY_FILE_TYPE);
            }
            ftpClient = ftpClientPool.borrowObject();
            if (!ftpClient.isConnected()) {
                throw new FTPConnectionClosedException("服务器链接失败!");
            }
        } catch (FTPConnectionClosedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ftpClient;
    }

    /**
     * 上传文件到ftp服务器
     * 在进行上传和下载文件的时候，设置文件的类型最好是：
     * ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE)
     * localFilePath:本地文件路径和名称
     * remoteFileName:服务器文件名称
     */
    public boolean uploadFile(String localFilePath, String remoteFileName)
            throws IOException {
        boolean flag = false;
        InputStream iStream = null;
        try {
            iStream = new FileInputStream(localFilePath);
            flag = ftpClient.storeFile(remoteFileName, iStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * 上传文件到ftp服务器
     * 在进行上传和下载文件的时候，设置文件的类型最好是：
     * ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE)
     * localFilePath:本地文件路径和名称
     * remoteFileName:服务器文件名称
     */
    public boolean uploadFile(String localFilePath, String remoteFileName, FTPClient ftpClient)
            throws IOException {
        log.info("====> " + localFilePath);
        log.info("====> " + remoteFileName);
        boolean flag = false;
        InputStream iStream = null;
        try {
            if (ftpClient == null || !ftpClient.isConnected()) {
                ftpClient = getFtpConnected();
            }

            String remotePath = remoteFileName.substring(0, remoteFileName.lastIndexOf("/"));
            this.mkDir(remotePath);
            iStream = new FileInputStream(localFilePath);
            flag = ftpClient.storeFile(remoteFileName, iStream);
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    /**
     * 上传文件到ftp服务器，上传新的文件名称和原名称一样
     *
     * @param fileName：文件名称
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String fileName) throws IOException {
        return uploadFile(fileName, fileName);
    }

    /**
     * 上传文件到ftp服务器
     *
     * @param iStream 输入流
     * @param newName 新文件名称
     * @return
     * @throws IOException
     */
    public boolean uploadFile(InputStream iStream, String newName)
            throws IOException {
        boolean flag = false;
        try {
            flag = ftpClient.storeFile(newName, iStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }


    /**
     * 转移到FTP服务器工作目录
     *
     * @param path
     * @return
     * @throws IOException
     */
    public boolean changeDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }


    /**
     * 在服务器上创建目录
     *
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean createDirectory(String pathName) throws IOException {
        return ftpClient.makeDirectory(pathName);
    }

    /**
     * @param pathName  路径
     * @param ftpClient ftp链接对象
     * @return
     * @throws IOException
     */
    public boolean createDirectory(String pathName, FTPClient ftpClient) throws IOException {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(pathName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @throws IOException
     */
    public void mkDir(String path) throws IOException {
        // 过滤路径中的特殊字符
        path = FileUtil.filtPath(path);

        String[] paths = path.split("/");
        // 返回到最上层目录
        this.changeDirectory("/");
        //遍历目录，如果不存在，则创建
        for (String p : paths) {
            if ("".equals(p)) {
                continue;
            }
            if (!existDirectory(p)) {
                this.createDirectory(p);
            }
            this.changeDirectory(p);
        }
    }

    /**
     * 在服务器上删除目录
     *
     * @param path
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }

    /**
     * 删除所有文件和目录
     *
     * @param path
     * @param isAll true:删除所有文件和目录
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path, boolean isAll)
            throws IOException {

        if (!isAll) {
            return removeDirectory(path);
        }

        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDirectory(path);
        }
        //
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                System.out.println("* [sD]Delete subPath [" + path + "/" + name + "]");
                removeDirectory(path + "/" + name, true);
            } else if (ftpFile.isFile()) {
                System.out.println("* [sF]Delete file [" + path + "/" + name + "]");
                deleteFile(path + "/" + name);
            } else if (ftpFile.isSymbolicLink()) {

            } else if (ftpFile.isUnknown()) {

            }
        }
        return ftpClient.removeDirectory(path);
    }

    /**
     * 检查目录在服务器上是否存在 true：存在  false：不存在
     *
     * @param path
     * @return
     * @throws IOException
     */
    public boolean existDirectory(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 得到文件列表,listFiles返回包含目录和文件，它返回的是一个FTPFile数组
     * listNames()：只包含目录的字符串数组
     * String[] fileNameArr = ftpClient.listNames(path);
     *
     * @param path:服务器上的文件目录:/DF4
     */
    public List<String> getFileList(String path) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        List<String> retList = new ArrayList<String>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }

    /**
     * 删除服务器上的文件
     *
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean deleteFile(String pathName) throws IOException {
        return ftpClient.deleteFile(pathName);
    }


    /**
     * 从ftp服务器上下载文件到本地
     *
     * @param remoteFileName：ftp服务器上文件名称
     * @param localFileName：本地文件名称
     * @return
     * @throws IOException
     */
    public boolean download(String remoteFileName, String localFileName)
            throws IOException {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            flag = ftpClient.retrieveFile(remoteFileName, oStream);
        } catch (IOException e) {
            flag = false;
            return flag;
        } finally {
            oStream.close();
        }
        return flag;
    }

    /**
     * 从ftp服务器上下载文件到本地
     *
     * @param sourceFileName：服务器资源文件名称
     * @return InputStream 输入流
     * @throws IOException
     */
    public InputStream downFile(String sourceFileName) throws IOException {
        return ftpClient.retrieveFileStream(sourceFileName);
    }

    /**
     * 关闭连接池
     *
     * @throws IOException
     */
    public void close() throws IOException {
        ftpClientPool.close();
    }


    /**
     * 返回绝对路径
     *
     * @param path 相对路径
     * @return
     * @throws Exception
     */
    public String combImgUrl(String path) throws Exception {

        if (path == null) {
            throw new Exception("参数错误");
        }
        // 替换路径中的分隔符为“/”
        path = FileUtil.filtPath(path);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String url = String.format("http://%s%s", host_port, path);
        return url;
    }
}
