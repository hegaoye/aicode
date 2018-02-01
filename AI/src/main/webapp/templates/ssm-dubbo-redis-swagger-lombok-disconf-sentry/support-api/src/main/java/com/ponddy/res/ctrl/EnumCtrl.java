/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */

package ${basePackage}.res.ctrl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import ${basePackage}.area.entity.Country;
import ${basePackage}.area.service.CountrySV;
import ${basePackage}.classroom.entity.Classroom;
import ${basePackage}.core.annotation.Ignore;
import ${basePackage}.core.enums.SexEnum;
import ${basePackage}.core.enums.YNEnum;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.redis.RedisUtils;
import ${basePackage}.course.entity.*;
import ${basePackage}.goods.entity.Goods;
import ${basePackage}.order.entity.Order;
import ${basePackage}.setting.entity.Setting;
import ${basePackage}.tutor.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lixin on 2017/6/3.
 */
@Controller
@RequestMapping("/res")
@Api(value = "枚举资源接口", description = "系统中的枚举查询接口通过此控制器可以获得，资源是从类映射到缓存中存放，最后通过接口返回")
public class EnumCtrl {
    private final static Logger logger = LoggerFactory.getLogger(EnumCtrl.class);
    @Resource
    public RedisTemplate<String, Object> redisTemplate;
    @Resource
    RedisUtils redisUtils;

    @Reference
    private CountrySV countrySV;

    /**
     * 通用枚举获取接口
     * 1.检查缓存，不存在就缓存
     * 2.缓存中获取
     *
     * @param code 枚举编码
     * @return
     */
    @ApiOperation(value = "通用枚举获取接口", notes = "系统通用枚举自动获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "枚举编码", required = true, paramType = "path")
    })
    @GetMapping("/enum/{code}")
    @ResponseBody
    public Map<String, String> commonEnum(@PathVariable Integer code) {
        Map<String, String> map = null;
        Assert.notNull(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        String cachekey = Enums.genCachekey(code);
        //1.检查缓存，不存在就缓存
        if (!redisUtils.hasKey(cachekey)) {
            map = Enums.genMap(code);
            if (map == null) {
                map = this.cacheCountry(code);
            } else {
                redisUtils.set(Enums.genCachekey(code), JSON.toJSONString(map));
                logger.info("通用枚举获取接口 : " + JSON.toJSONString(map));
            }
            return map;
        } else {
            //2.缓存中获取
            String mapStr = redisUtils.get(cachekey).toString();
            logger.info("通用枚举获取接口 : " + mapStr);
            return JSON.parseObject(mapStr, Map.class);
        }
    }

    /**
     * 缓存国家代码
     */
    private int cacheCountryKey = 9999;// https://xxxx.com/res/enum/9999

    private Map<String, String> cacheCountry(int code) {
        if (code != cacheCountryKey) {
            return null;
        }

        Map<String, Object> map = Maps.newHashMap();
        List<Country> countryList = countrySV.queryList(map);
        if (countryList != null && !countryList.isEmpty()) {
            Map<String, String> countryMap = Maps.newHashMap();
            for (Country country : countryList) {
                countryMap.put(country.getCountryCode(), country.getCountry());
            }
            redisUtils.set(Enums.genCachekey(cacheCountryKey), JSON.toJSONString(countryMap));
            return countryMap;
        }
        return null;
    }

    /**
     * 枚举管理器
     */
    private enum Enums {
        SexEnum(1001, SexEnum.class)/*性别枚举*/,
        CourseHourType(1002, CourseHourTrace.CourseHourType.class)/*课时类型*/,
        ClassroomState(1003, Classroom.State.class)/*课堂状态*/,
        CourseTimetableState(1004, CourseTimetable.State.class)/*课表状态*/,
        AmPm(1005, CourseTimetable.AmPm.class)/*上午或下午*/,
        CourseCategoryState(1006, CourseCategory.State.class)/*课程类别*/,
        CourseState(1007, Course.State.class)/*课程*/,
        Week(1008, CourseTimetable.Week.class)/*上午或下午*/,
        GoodsState(1009, Goods.State.class)/*商品状态*/,
        GoodsAudit(1010, Goods.Audit.class)/*商品审核*/,
        GoodsPromotionType(1011, Goods.PromotionType.class)/*促销类型*/,
        OrderState(1012, Order.State.class)/*订单状态*/,
        OrderDeleteState(1013, Order.DeleteState.class)/*删除状态*/,
        OrderOrderSource(1014, Order.OrderSource.class)/*订单来源*/,
        OrderRefundType(1015, Order.RefundType.class)/*退款模式*/,
        AssistantManageState(1016, Assistant.ManageState.class)/*管理状态*/,
        TutorState(1017, Tutor.State.class)/*教师状态*/,
        AssistantTutorsState(1018, AssistantTutors.State.class)/*助教的老师状态*/,
        SettingKey(1019, Setting.Key.class)/*设置的key*/,
        StudentType(1020, Student.Type.class)/*学生类型*/,
        TutorWorktimeState(1021, TutorWorktime.State.class)/*学生类型*/,
        CourseHourShareStudentState(1022, CourseHourShareStudent.State.class)/*分享课时的学生状态*/,
        CourseWareState(1023, CourseWare.State.class)/*课件状态*/,
        CourseWareType(1024, CourseWare.Type.class)/*文件类型*/,
        StudentChineseLevel(1025, Student.ChineseLevel.class)/*汉语级别默认-1，0，1，2，3，4，5，6*/,
        Language(1026, Country.Language.class)/*语言 English，Chinese*/,
        StudyType(1027, Student.StudyType.class)/*学习经历类型 Never,Learn*/,
        YNEnum(1028, YNEnum.class)/*true/false 枚举*/,
        CourseType(1029, Course.CourseType.class)/*课程类型 */,;

        public int code;
        public Class clazz;

        Enums(int code, Class clazz) {
            this.code = code;
            this.clazz = clazz;
        }

        public static Enums getEnums(Integer code) {
            for (Enums enums : Enums.values()) {
                if (enums.code == code) {
                    return enums;
                }
            }
            return null;
        }

        /**
         * 反射获取枚举键值关系
         *
         * @param code 枚举编号
         * @return
         * @throws InstantiationException
         */
        public static Map<String, String> genMap(Integer code) {
            try {
                Enums enums = getEnums(code);
                if (enums == null) {
                    return null;
                }
                Class clazz = enums.clazz;

                Map<String, String> map = Maps.newHashMap();
                if (clazz.isEnum()) {
                    Field[] fs = clazz.getDeclaredFields();//获得类的所有属性

                    //收集被忽略集合
                    List<String> ignoreKeys = new ArrayList<>();
                    for (Field field : fs) {
                        Ignore ignore = field.getAnnotation(Ignore.class);//判断是否具备注解
                        if (ignore != null) {
                            ignoreKeys.add(field.getName());//被忽略的属性
                        }
                    }

                    for (Object obj : clazz.getEnumConstants()) {
                        String type = obj.toString();
                        String classType = obj.getClass().getTypeName();
                        classType = classType.substring(classType.lastIndexOf(".") + 1);
                        for (int i = 0; i < fs.length; i++) {
                            Field f = fs[i];
                            f.setAccessible(true);
                            String fType = f.getType().getTypeName();

                            if (!fType.contains(classType)) {
                                Object val = f.get(obj);
                                map.put(type, val == null ? type : val.toString());
//                            } else {
//                                map.put(type, type);
                            }
                        }
                    }

                    //忽略键
                    ignoreKeys.forEach(ignoreKey -> map.remove(ignoreKey));
                }
                return map;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 生成枚举缓存key
         *
         * @param code 枚举编号
         * @return key
         */
        public static String genCachekey(Integer code) {
            return "ENUMS:" + code;
        }
    }


}
