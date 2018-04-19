package ${basePackage}.core.tools.redis;

import ${basePackage}.core.tools.UuidTools;

/**
 * Created by lixin on 2017/6/8.
 */
public class RedisKey {

    /**
     * 后台管理sessionkey
     */
    public static String tutorSsesionKey() {
        return "SS:teacher:" + UuidTools.getUUIDString();   //生成sessionkey
    }

    public static String assitantSessionKey() {
        return "SS:assitant:" + UuidTools.getUUIDString();   //生成sessionkey
    }

    public static String studentSessionKey() {
        return "SS:student:" + UuidTools.getUUIDString();   //生成sessionkey
    }

    public static String taskSessionKey() {
        return "SS:task:" + UuidTools.getUUIDString();   //生成sessionkey
    }

    /**
     * 生成uploadkey
     *
     * @param uid uid
     * @return key
     */
    public static String genUploadKey(String uid) {
        return "Upload:" + uid;
    }

    /**
     * 生成临时上传uploadkey
     *
     * @param uid uid
     * @return key
     */
    public static String genUploadLocalKey(String uid) {
        return "Upload:Local:" + uid;
    }


    /**
     * 生成区域编码
     *
     * @param code 编码
     * @return
     */
    public static String genAreaKey(String code) {
        return "BasicArea:" + code;
    }


    /**
     * 生成上传进度数据编号
     *
     * @param uid uid
     * @return key
     */
    public static String genUploadProgressKey(String uid) {
        return "Upload:Progress:" + uid;
    }


    /**
     * 微信消息模板key
     *
     * @return 消息模板
     */
    public static String WXMsgToken(String code) {
        return "Wechat:PushMsgAccessToken:" + code;
    }

    /**
     * 生成zoom缓存key
     *
     * @param classroomCode 课堂编码
     * @return key
     */
    public static String genZoomMeetingRequestKey(String classroomCode) {
        return "ZoomMeetingRequest:" + classroomCode;
    }

    /**
     * 生成课堂确认缓存key
     *
     * @param classroomCode
     * @return
     */
    public static String genClassroomConfirmedKey(String classroomCode) {
        return "ClassroomConfirmed:" + classroomCode;
    }

    /**
     * 生成课堂视频缓存key
     *
     * @param classroomCode
     * @return
     */
    public static String genUploadVideoKey(String classroomCode) {
        return "ClassroomVideo:" + classroomCode;
    }
}
