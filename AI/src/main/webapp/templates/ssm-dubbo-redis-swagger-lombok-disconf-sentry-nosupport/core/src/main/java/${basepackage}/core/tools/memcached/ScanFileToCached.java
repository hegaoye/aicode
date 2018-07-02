package ${basePackage}.core.tools.memcached;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service("scanFileToCached")
public class ScanFileToCached implements IScanFileToCached {
    private final static Logger logger = LoggerFactory.getLogger(ScanFileToCached.class);

    private static String root = null;
    private static int css = 0;
    private static int js = 0;
    private static int html = 0;
    private static int jpg = 0;
    private static int png = 0;
    private static int ico = 0;
    private static int gif = 0;
    private List<String> paths = new ArrayList<String>();

    @Override
    public List<String> listFilesPath(File path) throws Exception {
        if (root == null) {
            root = path.getPath();
            logger.info("root路径:" + root);
        }
        File[] fs = path.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                try {
                    listFilesPath(fs[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (fs[i].isFile()) {
                String abpath = fs[i].getAbsolutePath();
                if (abpath.contains(".css")) {
                    css++;
                }
                if (abpath.contains(".js")) {
                    js++;
                }
                if (abpath.contains(".html")) {
                    html++;
                }
                if (abpath.contains(".jpg")) {
                    jpg++;
                }
                if (abpath.contains(".png")) {
                    png++;
                }
                if (abpath.contains(".ico")) {
                    ico++;
                }
                if (abpath.contains(".gif")) {
                    gif++;
                }
                paths.add(fs[i].getAbsolutePath());
                logger.info("AbsolutePath - " + abpath);
            }
        }
        return paths;
    }

    @Override
    public List<File> listFiles(List<String> paths) throws FileNotFoundException {
        if (paths == null || paths.isEmpty()) {
            throw new FileNotFoundException("扫描路径集合为空异常！");
        }
        List<File> fileList = new ArrayList<File>();
        for (String path : paths) {
            File file = getFile(path);
            if (file != null) {
                fileList.add(file);
            }
        }
        if (fileList.isEmpty()) {
            throw new FileNotFoundException("没有扫描到文件！");
        }
        return fileList;
    }

    @Override
    public File getFile(String path) throws FileNotFoundException {
        if (StringUtils.isBlank(path)) {
            throw new FileNotFoundException("路径为空！");
        }
        File file = new File(path);
        if (!file.exists()) {
            logger.info(file.getAbsolutePath());
            paths.remove(file.getAbsolutePath());
            throw new FileNotFoundException("文件不存在！");
        }
        return file;
    }

    @Override
    public boolean cacheFile(String key, File file) throws Exception {
        if (file == null) {
            throw new FileNotFoundException("文件不存在！无法缓存！");
        }
        MemCachedUtil memCached = MemCachedUtil.getInstance();
        Object fileObj = memCached.getObj(key);
        if (fileObj == null) {
            Object fileData = checkFile(file);
            if (fileData != null) {
                memCached.setCache(key, fileData, MemCachedUtil.TimeOut.oneWeek);//默认保存1周
                return true;
            }
        }
        return false;
    }

    @Override
    public String cacheFiles(List<File> files) throws Exception {
        int i = 0;
        for (File file : files) {
            String key = "";
            String abPath = file.getPath();
            String path = abPath.replace(root, "").replace("\\", "/");
            key = path.indexOf("/") == -1 ? "/" + path : path;
            logger.info(path);
            i = cacheFile(key, file) ? i++ : i;
        }
        logger.info("文件数 js:-" + js);
        logger.info("文件数 css:-" + css);
        logger.info("文件数 html:-" + html);
        logger.info("文件数 jpg:-" + jpg);
        logger.info("文件数 png:-" + png);
        logger.info("文件数 gif:-" + gif);
        logger.info("文件数 ico:-" + ico);
        String result = "文件数 js:" + js + " 文件数 css:" + css + " 文件数 html:" + html + " 文件数 jpg:" + jpg + " 文件数 png:" + png + " 文件数 gif:" + gif + " 文件数 ico:" + ico;
        return result;
    }

    /**
     * 获得文件
     *
     * @param file
     * @return
     */
    private Object checkFile(File file) throws IOException {
        Object data = null;
        String resPath = file.getAbsolutePath();
        if (resPath.contains(".jpg") || resPath.contains(".png") || resPath.contains(".gif") || resPath.contains(".ico")) {
            data = genImg(file);
        } else if (resPath.contains(".css") || resPath.contains(".js") || resPath.contains(".html")) {
            data = genFile(resPath);
        }
        return data;
    }

    /**
     * 读取文件
     *
     * @return
     * @throws IOException
     */
    private String genFile(String resPath) throws IOException {
        FileInputStream fis = new FileInputStream(resPath);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        String fileContent = "";
        while ((line = br.readLine()) != null) {
            fileContent += line;
        }
        logger.debug(fileContent);
        br.close();
        isr.close();
        fis.close();
        return fileContent;
    }

    /**
     * 获得图片对象
     *
     * @param img
     * @return 图片的字节流数据
     * @throws IOException
     */
    private byte[] genImg(File img) throws IOException {
        byte data[] = null;
        ImageInputStream input = ImageIO.createImageInputStream(img);
        data = new byte[(int) input.length()];
        input.read(data);
        input.close();
        return data;
    }
}
