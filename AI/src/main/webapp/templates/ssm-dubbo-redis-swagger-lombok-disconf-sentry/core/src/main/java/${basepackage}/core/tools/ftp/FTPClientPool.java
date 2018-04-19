package ${basePackage}.core.tools.ftp;

import ${basePackage}.core.tools.JSON;
import ${basePackage}.core.tools.PathTools;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 实现了一个FTPClient连接池
 * Created by lixin on 2017/5/9.
 */
public class FTPClientPool implements ObjectPool<FTPClient> {
    private static final int DEFAULT_POOL_SIZE = 1;
    private static BlockingQueue<FTPClient> pool;
    private static FtpClientFactory factory;

    public static void main(String[] args) throws IOException {
        test();
    }

    //TODO test
    public static void test() throws IOException {
        //创建ftpClient对象
        FTPClient ftpClient = new FTPClient();
        //创建ftp链接，默认是21端口
        ftpClient.connect("47.91.246.115", 21);

        //登录ftp服务器，使用用户名和密码
        ftpClient.login("ftpimg", "img0-tutors.ponddy.com");
        System.out.printf(JSON.toJSONString(ftpClient.listFiles()));

        //上传文件
        //读取本地文件
        FileInputStream inputStream = new FileInputStream(new File("C:\\1.PNG"));

        //设置上传的路径
        ftpClient.changeWorkingDirectory("/");

        //修改上传格式
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        //第一个参数：服务器端文档名
        //第二个参数，上传文档的inputStream
        ftpClient.storeFile("1111.png", inputStream);
        //关闭链接
        ftpClient.logout();

    }


    public FTPClientPool(String host, int port, String username, String password, int transferFileType) throws Exception {
        this.factory = new FtpClientFactory(new FTPClientConfigure(host, port, username, password, transferFileType));
    }


    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     *
     * @param factory
     * @throws Exception
     */
    public FTPClientPool(FtpClientFactory factory) throws Exception {
        this(DEFAULT_POOL_SIZE, factory);
    }

    /**
     * @param factory
     * @throws Exception
     */
    public FTPClientPool(int poolSize, FtpClientFactory factory) throws Exception {
        this.factory = factory;
        pool = new ArrayBlockingQueue<FTPClient>(poolSize * 3);
        initPool(poolSize);
    }

    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     *
     * @param maxPoolSize
     * @throws Exception
     */
    private void initPool(int maxPoolSize) throws Exception {
        for (int i = 0; i < maxPoolSize; i++) {
            //往池中添加对象
            addObject();
        }

    }

    /* (non-Javadoc)
    * @see org.apache.commons.pool.ObjectPool#borrowObject()
    */
    public FTPClient borrowObject() throws Exception, InterruptedException {
        FTPClient client = factory.makeObject();
//        FTPClient client = pool.take();
//        if (client == null) {
//            client = factory.makeObject();
//            addObject();
//        } else if (!factory.validateObject(client)) {//验证不通过
//            //使对象在池中失效
//            invalidateObject(client);
//            //制造并添加新对象到池中
//            client = factory.makeObject();
//            addObject();
//        }
        return client;

    }

    /* (non-Javadoc)
    * @see org.apache.commons.pool.ObjectPool#returnObject(java.lang.Object)
    */
    public void returnObject(FTPClient client) throws Exception {
        if ((client != null) && !pool.offer(client, 3, TimeUnit.SECONDS)) {
            try {
                factory.destroyObject(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void invalidateObject(FTPClient client) throws Exception {
        //移除无效的客户端
        pool.remove(client);
    }

    /* (non-Javadoc)
    * @see org.apache.commons.pool.ObjectPool#addObject()
    */
    public void addObject() throws Exception {
        //插入对象到队列
        pool.offer(factory.makeObject(), 3, TimeUnit.SECONDS);
    }


    @Override
    public int getNumIdle() {
        return 0;
    }

    @Override
    public int getNumActive() {
        return 0;
    }

    public void clear() throws Exception {

    }

    public void close() {
        try {
            while (pool.iterator().hasNext()) {
                FTPClient client = null;
                client = pool.take();
                factory.destroyObject(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
