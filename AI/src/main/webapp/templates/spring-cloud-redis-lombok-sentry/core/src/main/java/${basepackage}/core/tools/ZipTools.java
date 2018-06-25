package ${basePackage}.core.tools;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * ZIP 压缩，解压缩工具
 * Created by lixin on 2017/10/17.
 */
public class ZipTools {

    public static void unzip(String source, String destination) {
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String source = "C:\\1.zip";
        String destination = "C:\\";

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        source = "folder/source.zip";
        destination = "folder/source/";

        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword("password");
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

}
