/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.目.
 */
package ${basePackage}.core.base;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.common.Constants;
import ${basePackage}.core.tools.CookieTools;
import ${basePackage}.core.tools.IPGetter;
import ${basePackage}.core.tools.redis.RedisKey;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.tutor.entity.Assistant;
import ${basePackage}.tutor.entity.Student;
import ${basePackage}.tutor.entity.Tutor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BaseCtrl implements Serializable {
    private static final long serialVersionUID = 6357869213649815390L;
    protected final static Logger logger = LoggerFactory.getLogger(BaseCtrl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, Object> hashOperations;
    @Resource
    protected RedisUtils redisUtils;
    @Autowired
    protected HttpServletRequest request;


    /**
     * 教师 sesion生成
     *
     * @param tutor 教师对象
     */
    protected boolean tutorSession(Tutor tutor, HttpServletResponse response) {
        //初始化session
        String sesionKey = RedisKey.tutorSsesionKey();   //生成sessionkey
        if (redisTemplate.hasKey(sesionKey)) {    //如果已经有此key，则删除
            redisTemplate.delete(sesionKey);
        }

        setSessionVal(sesionKey, "sessionCode", tutor.getTutorCode());
        setSessionVal(sesionKey, "email", tutor.getEmail());
        setSessionVal(sesionKey, "name", tutor.getName());
        setSessionVal(sesionKey, "firstName", tutor.getName());
        setSessionVal(sesionKey, "lastName", tutor.getName());
        setSessionVal(sesionKey, "userId", tutor.getUserId());
        setSessionVal(sesionKey, "tutorCode", tutor.getTutorCode());
        setSessionVal(sesionKey, "countryCode", tutor.getCountryCode());
        setSessionVal(sesionKey, "timeZone", tutor.getTimeZone());
        setSessionVal(sesionKey, "state", tutor.getState());
        setSessionVal(sesionKey, "type", "teacher");
        setSessionVal(sesionKey, "ip", IPGetter.getAccessIp());

        redisTemplate.expire(sesionKey, (Long) Constants.SS_TIMEOUT.val, TimeUnit.MILLISECONDS);   //设置session过期时间
        CookieTools.INSTANCE.addCookie((String) Constants.sessionid.val, sesionKey, Constants.Domain, response);    //写入cookie
        CookieTools.INSTANCE.addCookie(Constants.Tutor.name(), Constants.Tutor.name(), Constants.Domain, response);    //写入cookie
        return true;
    }


    /**
     * 学生 session 生成
     *
     * @param student 学生
     */
    protected boolean studentSession(Student student, HttpServletResponse response) {
        //初始化session
        String sesionKey = RedisKey.studentSessionKey();   //生成sessionkey
        if (redisTemplate.hasKey(sesionKey)) {    //如果已经有此key，则删除
            redisTemplate.delete(sesionKey);
        }

        setSessionVal(sesionKey, "sessionCode", student.getStudentCode());
        setSessionVal(sesionKey, "email", student.getEmail());
        setSessionVal(sesionKey, "name", student.getName());
        setSessionVal(sesionKey, "userId", student.getUserId());
        setSessionVal(sesionKey, "firstName", student.getFirstName());
        setSessionVal(sesionKey, "lastName", student.getLastName());
        setSessionVal(sesionKey, "studentCode", student.getStudentCode());
        setSessionVal(sesionKey, "timeZone", student.getTimeZone());
        setSessionVal(sesionKey, "state", student.getState());
        setSessionVal(sesionKey, "type", "student");
        setSessionVal(sesionKey, "ip", IPGetter.getAccessIp());

        redisTemplate.expire(sesionKey, (Long) Constants.SS_TIMEOUT.val, TimeUnit.MILLISECONDS);   //设置session过期时间
        CookieTools.INSTANCE.addCookie((String) Constants.sessionid.val, sesionKey, Constants.Domain, response);    //写入cookie
        CookieTools.INSTANCE.addCookie(Constants.Student.name(), Constants.Student.name(), Constants.Domain, response);    //写入cookie
        return true;
    }


    /**
     * 助教 session 生成
     *
     * @param assistant 助教
     */
    protected boolean assitantSession(Assistant assistant, HttpServletResponse response) {
        //初始化session
        String sesionKey = RedisKey.assitantSessionKey();   //生成sessionkey
        if (redisTemplate.hasKey(sesionKey)) {    //如果已经有此key，则删除
            redisTemplate.delete(sesionKey);
        }

        setSessionVal(sesionKey, "sessionCode", assistant.getAssistantCode());
        setSessionVal(sesionKey, "email", assistant.getEmail());
        setSessionVal(sesionKey, "name", assistant.getName());
        setSessionVal(sesionKey, "assistantCode", assistant.getAssistantCode());
        setSessionVal(sesionKey, "timeZone", assistant.getTimeZone());
        setSessionVal(sesionKey, "state", assistant.getState());
        setSessionVal(sesionKey, "ip", IPGetter.getAccessIp());

        redisTemplate.expire(sesionKey, (Long) Constants.SS_TIMEOUT.val, TimeUnit.MILLISECONDS);   //设置session过期时间
        CookieTools.INSTANCE.addCookie((String) Constants.AssistantSessionId.val, sesionKey, Constants.AssistantDomain, response);    //写入cookie
        CookieTools.INSTANCE.addCookie(Constants.Assitant.name(), Constants.Assitant.name(), Constants.Domain, response);    //写入cookie
        return true;
    }

    /**
     * 获得助教session
     *
     * @return 助教
     */
    protected Assistant getsAssitantSession() {
        String sessionCode = this.getAssitantSessionVal("sessionCode");
        Assistant assitant = null;
        if (StringUtils.isNotBlank(sessionCode)) {
            String assitantCode = getAssitantSessionVal("assistantCode");
            String email = getAssitantSessionVal("email");
            String name = getAssitantSessionVal("name");
            String timeZone = getAssitantSessionVal("timeZone");
            String state = getAssitantSessionVal("state");
            assitant = new Assistant(assitantCode, email, name, timeZone, state);
        }
        return assitant;
    }

    /**
     * 获得学生session
     *
     * @return 学生
     */
    protected Student getStudentSession() {
        String sessionCode = getSessionVal("sessionCode");
        Student student = null;
        if (StringUtils.isNotBlank(sessionCode)) {
            String studentCode = getSessionVal("studentCode");
            String email = getSessionVal("email");
            String userId = getSessionVal("userId");
            String firstName = getSessionVal("firstName");
            String lastName = getSessionVal("lastName");
            String timeZone = getSessionVal("timeZone");
            String state = getSessionVal("state");
            student = new Student(studentCode, email, userId, firstName, lastName, timeZone, state);
        }
        return student;
    }


    /**
     * 获得教师 sesion
     */
    protected Tutor getTutorSession() {
        //初始化session
        String sessionCode = getSessionVal("sessionCode");
        Tutor tutor = null;
        if (StringUtils.isNotBlank(sessionCode)) {
            String tutorCode = getSessionVal("tutorCode");
            String email = getSessionVal("email");
            String userId = getSessionVal("userId");
            String name = getSessionVal("name");
            String timeZone = getSessionVal("timeZone");
            String state = getSessionVal("state");
            String countryCode = getSessionVal("countryCode");

            tutor = new Tutor(tutorCode, email, userId, name, timeZone, state, countryCode);
        }
        return tutor;
    }


    /**
     * 向session中赋值
     *
     * @param name  键
     * @param value 值
     */
    public void setSessionVal(String name, Object value) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            setSessionVal(sesionKey, name, value);
        }
    }

    /**
     * 向session中赋值
     *
     * @param sesionKey sesionKey
     * @param name      键
     * @param value     值
     */
    public void setSessionVal(String sesionKey, String name, Object value) {
        if (StringUtils.isNotBlank(sesionKey)) {
            hashOperations.put(sesionKey, name, value);
        }
    }

    /**
     * 从session中取值
     *
     * @param name 键值
     * @return String类型的值
     */
    public String getSessionVal(String name) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            return (String) hashOperations.get(sesionKey, name);
        }
        return null;
    }

    /**
     * 从session中取值
     *
     * @param name 键值
     * @return String类型的值
     */
    public String getAssitantSessionVal(String name) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.AssistantSessionId.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            return (String) hashOperations.get(sesionKey, name);
        }
        return null;
    }

    /**
     * 从session中取值
     *
     * @param name  键值
     * @param clazz 转换的对象的class
     * @param <T>
     * @return
     */
    public <T> T getSessionVal(String name, Class<T> clazz) {
        return JSON.parseObject(getSessionVal(name), clazz);
    }

    /**
     * 从session中取得List类型的值
     *
     * @param name  键值
     * @param clazz 转换的对象的class
     * @param <T>
     * @return
     */
    public <T> List<T> getSessionAryVal(String name, Class<T> clazz) {
        return JSON.parseArray(getSessionVal(name), clazz);
    }

    /**
     * 注销session
     */
    public void invalidateSession(HttpServletResponse response) {
        String sesionKey = CookieTools.INSTANCE.getCode((String) Constants.sessionid.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(sesionKey)) {
            CookieTools.INSTANCE.delCookie((String) Constants.sessionid.val, response);
            CookieTools.INSTANCE.delCookie(Constants.Student.name(), response);
            CookieTools.INSTANCE.delCookie(Constants.Tutor.name(), response);
            redisTemplate.delete(sesionKey);    //删除session
        }
        String AssistantSessionIdKey = CookieTools.INSTANCE.getCode((String) Constants.AssistantSessionId.val, request);    //获取sessionKey
        if (StringUtils.isNotBlank(AssistantSessionIdKey)) {
            CookieTools.INSTANCE.delCookie((String) Constants.AssistantSessionId.val, response);
            CookieTools.INSTANCE.delCookie(Constants.Assitant.name(), response);
            redisTemplate.delete(AssistantSessionIdKey);    //删除session
        }
    }
}
