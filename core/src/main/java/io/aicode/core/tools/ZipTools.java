/*
 *
 *                        http://www.aicode.io
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package io.aicode.core.tools;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * ZIP 压缩，解压缩工具
 * Created by lixin on 2017/10/17.
 */
public class ZipTools {

    /**
     * 解压文件
     *
     * @param source      zip文件路径
     * @param destination 目标路径
     */
    public static void unzip(String source, String destination) {
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void zip(String destination, String srcDir) {
        ZipFile zipFile = null;
        try {
            if (!destination.contains(".zip")) {
                destination += ".zip";
            }
            zipFile = new ZipFile(destination);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFolder(srcDir, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ZipException {
        String source = "C:\\1.zip";
        String destination = "C:\\";

//        try {
//            ZipFile zipFile = new ZipFile(source);
//            zipFile.extractAll(destination);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
//
//        source = "folder/source.zip";
//        destination = "folder/source/";
//        String password = "password";
//
//        try {
//            ZipFile zipFile = new ZipFile(source);
//            if (zipFile.isEncrypted()) {
//                zipFile.setPassword(password);
//            }
//            zipFile.extractAll(destination);
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }


        ZipFile zipFile = new ZipFile("c:\\test4.zip");

        String folderToAdd = "C:\\workspaces\\AI-Code\\AI\\build\\libs\\exploded\\AI.war\\workspace\\announce";

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        zipFile.addFolder(folderToAdd, parameters);

    }

}
