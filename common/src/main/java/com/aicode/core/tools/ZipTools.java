/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */

package com.aicode.core.tools;


import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * ZIP 压缩，解压缩工具
 * Created by lixin on 2017/10/17.
 */
@Slf4j
public class ZipTools {

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
        log.error("ZIP 操作失败", e);
        }
    }

}
