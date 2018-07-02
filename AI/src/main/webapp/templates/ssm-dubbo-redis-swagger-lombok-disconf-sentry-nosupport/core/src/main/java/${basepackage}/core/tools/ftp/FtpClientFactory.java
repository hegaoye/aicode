package ${basePackage}.core.tools.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.ftp.FtpLoginException;

import java.io.IOException;

/**
 * FTPClient工厂类，通过FTPClient工厂提供FTPClient实例的创建和销毁
 * Created by lixin on 2017/5/9.
 */
public class FtpClientFactory implements PoolableObjectFactory<FTPClient> {
    private final static Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);
    private static FTPClientConfigure config;

    //给工厂传入一个参数对象，方便配置FTPClient的相关参数
    public FtpClientFactory(FTPClientConfigure config) {
        this.config = config;
    }

    /* (non-Javadoc)
    * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
    */
    public FTPClient makeObject() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(config.getClientTimeout());
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                logger.warn("FTPServer refused connection");
                return null;
            }
            boolean result = ftpClient.login(config.getUsername(), config.getPassword());
            if (!result) {
                throw new FtpLoginException("ftpClient登陆失败! userName:" + config.getUsername() + " ; password:" + config.getPassword());
            }
            ftpClient.setFileType(config.getTransferFileType());
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding(config.getEncoding());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setRemoteVerificationEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }


    /* (non-Javadoc)
    * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
    */
    public void destroyObject(FTPClient ftpClient) throws Exception {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
    * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
    */
    public boolean validateObject(FTPClient ftpClient) {
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate client: " + e, e);
        }
    }

    public void activateObject(FTPClient ftpClient) throws Exception {
    }

    public void passivateObject(FTPClient ftpClient) throws Exception {

    }
}
