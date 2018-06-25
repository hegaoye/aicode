/*
 * ${copyright}
 */
package ${basePackage}.core.tools.memcached;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface IScanFileToCached {


    /**
     * 根据指定路径获得所有路径
     * <p>
     * 迭代函数
     *
     * @param file 扫描路径
     * @return 子文件集合
     * @throws Exception
     */
    List<String> listFilesPath(File file) throws Exception;

    /**
     * @param path
     * @return
     * @throws Exception
     */
    List<File> listFiles(List<String> path) throws FileNotFoundException;

    /**
     * 获得文件
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    File getFile(String path) throws FileNotFoundException;

    /**
     * 缓存文件
     *
     * @param key  缓存键 可以是文件路径
     * @param file 被缓存的文件
     * @return
     * @throws Exception
     */
    boolean cacheFile(String key, File file) throws Exception;

    /**
     * 批量缓存文件
     *
     * @param files 文件集合
     * @return
     * @throws Exception
     */
    String cacheFiles(List<File> files) throws Exception;

}
