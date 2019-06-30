/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.目.
 */
package io.aicode.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class BaseCtrl implements Serializable {
    private static final long serialVersionUID = 6357869213649815390L;
    protected final static Logger logger = LoggerFactory.getLogger(BaseCtrl.class);
    @Autowired
    protected HttpServletRequest request;
}
