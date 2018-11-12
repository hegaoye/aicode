/*
 *
 *                        http://www.aicode.io
 *
 *
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
