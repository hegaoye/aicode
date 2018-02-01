package ${basePackage}.core.tools.ftp;

/**
 * FTPClient配置类，封装了FTPClient的相关配置
 * Created by lixin on 2017/5/9.
 */
public class FTPClientConfigure {
    /**
     * Ftp服务器
     */
    private String host;
    /**
     * 连接端口，默认21
     */
    private int port = 21;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    private String passiveMode="true";
    /**
     * 编码
     */
    private String encoding = "UTF-8";
    /**
     * 超时时间
     */
    private int clientTimeout;

    private int threadNum;
    /**
     * 设置传输文件类型:FTP.BINARY_FILE_TYPE | FTP.ASCII_FILE_TYPE
     * 二进制文件或文本文件
     */
    private int transferFileType;

    private boolean renameUploaded;
    private int retryTimes;


    public FTPClientConfigure(String host, int port, String username, String password, int transferFileType) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.transferFileType = transferFileType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(String passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(int transferFileType) {
        this.transferFileType = transferFileType;
    }

    public boolean isRenameUploaded() {
        return renameUploaded;
    }

    public void setRenameUploaded(boolean renameUploaded) {
        this.renameUploaded = renameUploaded;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public String toString() {
        return "FTPClientConfig [host=" + host + "\n port=" + port + "\n username=" + username + "\n password=" + password + "\n passiveMode=" + passiveMode
                + "\n encoding=" + encoding + "\n clientTimeout=" + clientTimeout + "\n threadNum=" + threadNum + "\n transferFileType="
                + transferFileType + "\n renameUploaded=" + renameUploaded + "\n retryTimes=" + retryTimes + "]";
    }
}
