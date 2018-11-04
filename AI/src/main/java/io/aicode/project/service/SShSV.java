/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import io.aicode.base.tools.WSTools;
import io.aicode.project.entity.SSh;

import java.io.IOException;

/**
 *
 */
public interface SShSV {

    boolean close(String key);

    boolean close(String key, WSTools wsTools);

    void sftpUpload(String codes, String fileName, String path, SSh sSh) throws JSchException, SftpException;

    void shell(SSh sSh, String cmd, String key, WSTools wsTools) throws JSchException, IOException;

}
