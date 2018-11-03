/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import io.aicode.project.entity.SSh;

import java.io.PrintWriter;

/**
 *
 */
public interface SShSV {

    boolean close(PrintWriter printWriter, Channel channel);

    void sftpUpload(String codes, String fileName, String path, SSh sSh) throws JSchException, SftpException;

}
