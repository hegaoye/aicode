/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */


package com.rzhkj.setting.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * 设置对象
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class Setting extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long id;//数据库字段:id  属性显示:id
    private String k;//数据库字段:k  属性显示:键
    private String v;//数据库字段:v  属性显示:值
    private String summary;//数据库字段:summary  属性显示:说明

    //设置键枚举
    public enum Key {
        //--------Zoom 配置 Begin---------//
        Create_A_Meeting,//zoom 创建一个会议的 key
        Get_Meeting_Info,//zoom 获取一个会议的信息 key
        End_A_Meeting,//zoom 结束一个会议 key
        List_Meetings,//zoom 列表会议 key
        List_Live_Meetings,//zoom 列表正在进行的会议 key
        List_Recording,//zoom 列表视频记录 key
        Get_Recording,//zoom 查询一个视频记录 key
        Delete_Recording,//zoom 删除视频 路径 key
        DownloadDefaultPath,//zoom 下载zoom视频默认路径 一般是系统的路径比如：/data/zoom/videos/ key
        Zoom_Webhooks_Url,//zoom 回调接口
        //--------Zoom 配置 End---------//

        //--------Sprout Video 配置 Begin---------//
        Get_A_Single_Video, //sproutvideo 获得一个视频 key
        Delete_A_Video,//sproutvideo 删除一个视频 key
        Upload_A_New_Video,//sproutvideo 上传视频请求 key
        SproutVideo_Api_Key,//sproutvideo api_key key
        Sproutvideo_Webhooks_Url,//sproutvideo upload video wechat url key
        //--------Sprout Video 配置 End---------//

        //--------系统参数设置 配置 Begin---------//
        Advance_Book_Time,//系统课表提前预约时间 key
        Allow_Time_Limit_For_Cancelled_Courses,//系统允许取消预约课时时间限制 key
        Upper_Class_Limit,//系统课堂上课人数限制 key
        Custom_Upper_Limit,//系统课堂自定义上课选择老师限制 key
        Classroom_Overdue_Close_Time,//关闭过期未正常上课的课堂，时间单位：分钟，默认30分钟 key
        Classroom_Overtime_Close_Time,//关闭上课的课堂严重超时的时间限制，时间单位：分钟，默认30分钟 key
        Create_Zoom_Time,//创建zoom课堂，并占用资源的时间控制，单位：分钟 默认12小时=720分钟 key
        //--------系统参数设置 配置 End---------//


        //--------wechat 消息模板key 配置 Begin---------//
        Wechat_Origin_Id,//微信 原始Id
        Wechat_Api_Key,//微信 app key
        Wechat_Secret,//微信app key 密钥
        Wechat_Api_Token,//微信API Token key
        Wechat_Token_Url,//微信token key
        Wechat_Msg_tpl_Url,//微信模板通知地址 key
        Wechat_Attend_Course_Notification_Template,//上课时间提醒，大概在上课前30分钟定时进行提醒 消息模板key
        Wechat_Attend_Course_Notification_Template_Time,//上课时间提醒，提前的时间 key
        Wechat_Book_Notification_Template,//预约课程后通知老师 消息模板key
        Wechat_Course_Cancelled_Notification_Template,//课程取消通知 消息模板key
        Wechat_Class_Evaluation_Notification_Template,//课后学生评论提醒 消息模板key
        Wechat_Book_Notification_Template_LinkUrl,//预约通知消息的点击连接 key
        Wechat_Class_Evaluation_Notification_Template_LinkUrl,//课后学生评论通知消息的点击连接 key
        //--------wechat 消息模板key 配置 End---------//

        //--------Paypal 消息模板key 配置 Begin---------//
        Paypal_Cancel_Req,//取消支付請求
        Paypal_Cancel_Url,//取消支付页面
        Paypal_Success_Req,//支付成功請求
        Paypal_Success_Return_Url,//支付成功页面
        //--------Paypal 消息模板key 配置 End---------//


        // --------Ponddy Chinese 参数key 配置 Begin---------//
        PonddyLet_Booking_Url,//ponddy chinese检测完毕后自动跳转地址
        Student_Basic_Info_Url,//學生基本資訊
        Pondlet_Content_Url,//課文的相關內容
        Public_Student_Analytic_Page_Url,//單一學生的學習情形分析的開放網頁
        Message_Url,//訊息發送服務（會先以 Email 的方式寄出,  temporary dummy）
        New_Student_Url,//註冊一個新的學生帳號，並設定為 active 狀態
        Assign_Student_Level_Url,//天津內部老師在評估後指定一個層級班級, 一個學生可被指派並加入至多個不同的班級中
        Student_Analytic_Url,//單一學生的學習情形分析
        Delete_Student_Url,//在中央帳號管理完成前，先以欄位註記 ponddytutors 的使用者，ponddytutors只能inactive 來源是 PT 的使用者，無法更動 PC 的使用者。
        //--------Ponddy Chinese 参数key 配置 End---------//

        Custom_Course_Cost_Hour,//自定义课时需要扣除的课时数量
        Assessment_Url,//咨询课程Url
        Assessment_Code,//咨询课程id
        Consultation_Url,//评估课程Url
        Consultation_Code,//评估课程id


        // --------Ponddy JWT 参数key 配置 Begin---------//
        JWT_Client_ID,//用于访问ponddy chinese接口的 client id 参数
        JWT_Secret_Key,//用于访问ponddy chinese接口的 SECRET KEY 参数
        JWT_Expire_Minutes,//JWT 请求密钥过期时间限制 5分钟
        // --------Ponddy JWT 参数key 配置 end---------//

        ;

    }

}

