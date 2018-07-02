/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */
package ${basePackage}.core.exceptions;

import ${basePackage}.core.tools.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 基本异常类
 *
 * @author lixin 2017-08-03 17:46
 */
public class BaseException extends RuntimeException implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(BaseException.class);

    public BaseException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage.toString());
        logger.error("系统发生异常[{}]", exceptionMessage.toString());
    }

    public BaseException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage.toString());
        logger.error("系统发生异常[{}],参数为[{}]", exceptionMessage.toString(), JSON.toJSONString(params));
    }


    public BaseException(String message) {
        super(message);
        logger.error("系统发生异常[{}]", message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        logger.error("系统发生异常[{}],异常为[{}]", message, cause);
    }


    /*异常信息定义*/
    public enum BaseExceptionEnum {

        //-----------------系统异常定义 [9000~9499]--------------------------
        Server_Error(9999, "Server Error"),
        State_Error(9002, "State error"),
        Ilegal_Param(9003, "Parameter is ilegal"),
        Empty_Param(9004, "Parameter is empty"),
        Exists(9005, "There is already exists"),
        Result_Not_Exist(9006, "The query result does not exist"),
        Account_Error(9010, "There is Error of Account and Password."),

        //-----------------上传文件异常定义[9500~9599]--------------------------
        UPLOAD_FAILED(9500, "Upload failed！"),

        //-----------------Zoom异常定义[9600~9699]--------------------------
        No_Free(9600, "No free resources of zoom"),
        Zoom_Exist(9601, "There is zoom already exists"),
        Zoom_Ilegal_Result(9602, "Zoom Result is ilegal"),
        Zoom_Create_Failed(9603, "Zoom Create is Failed"),

        //-----------------Sproutvideo异常定义[9700~9799]--------------------------
        //-----------------paypal异常定义[9800~9899]--------------------------

        //-----------------notify异常定义[9900~9888]--------------------------
        No_OpenId_Error(9900, "There is no tutor's openId error."),
        No_TimeZone_Error(9901, "Tutor's timezone not setting error."),

        //-----------------学生异常定义[1000~1999]--------------------------
        Student_Exists(1000, "There is already exists"),
        Student_Not_Exists(1001, "There is no student"),


        //-----------------老师异常定义[2000~2999]--------------------------
        Tutor_Not_Exist(2000, "There is no Tutor"),
        Tutor_Add_Failed(2001, "Add the Tutor failed"),
        Tutor_Not_Be_Active(2002, "Tutor state have to be Actice"),
        Tutor_Wechat_Binded(2003, "The wechat already exists"),

        //-----------------助教异常定义[3000~3999]--------------------------
        Assistant_Not_Exist(3001, "Course does not exist"),


        //-----------------课程,课表,课堂异常定义[4000~4999]--------------------------
        Book_Unavailable(4000, "Course can not book"),
        Classroom_Started(4001, "The Lesson already started"),
        Course_Hour_Not_Enough(4002, "There is not Enough course hours"),
        Over_Booking_Time(4003, "It's over the reservation time"),
        Over_Allow_Cancel_Time(4004, "Timeout cannot be canceled"),
        Classroom_Not_Exist(4005, "The Lesson does not exist"),
        Classroom_Exist(4006, "The Lesson  exist"),
        Not_Enough_Course_Hour(4010, "There is no enough lessons"),
        Course_Exist(4006, already_exist("Course")),
        Course_Add_Failed(4007, add_failed("Course")),
        Course_Category_Not_Exist(4008, "course category does not exist"),
        Course_Not_Exist(4011, not_exist("Course")),
        Booked_Error(4012, "Course Timetable has booked"),
        Confirmed_Error(4013, "The Lesson already Confirmed"),

        //-----------------商品异常定义[6000~6999]--------------------------
        Goods_Not_Exist(6000, not_exist("Goods")),

        Goods_Exists(6001, already_exist("Goods")),

        Goods_Aadd_Failed(6002, add_failed("Goods")),

        //-----------------通知异常定义[7000~7999]--------------------------
        //-----------------订单异常定义[8000~8999]--------------------------
        Order_Not_Exist(8000, not_exist("Order")),
        Order_Add_Failed(8001, add_failed("Order")),
        Order_Payment_Failed(8002, "payment order failed"),
        Order_Payment_Execute_Failed(8003, "payment execute failed");

        public int code;//错误编码
        public String error;//错误信息

        //信息提示：不存在
        private static String not_exist(String arg) {
            return arg + " does not exist";
        }

        //信息提示：已经存在
        private static String already_exist(String arg) {
            return arg + " already exists";
        }

        //信息提示：添加失败
        private static String add_failed(String arg) {
            return "add " + arg + " failed";
        }

        BaseExceptionEnum(int code, String error) {
            this.code = code;
            this.error = error;
        }

        @Override
        public String toString() {
            return "{code:" + code + ", error:\"" + error + "\"}";
        }

        public static void main(String[] args) {
            System.out.println(BaseExceptionEnum.Order_Not_Exist.toString());
        }
    }
}
