package io.aicode.base;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.aicode.core.annotation.Ignore;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.tools.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixin on 2017/6/3.
 */
@RestController
@RequestMapping("/res")
@Slf4j
@Api(value = "枚举资源接口", description = "系统中的枚举查询接口通过此控制器可以获得，资源是从类映射到缓存中存放，最后通过接口返回")
public class EnumCtrl {

    @Resource
    public RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisUtils redisUtils;


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
            @ApiImplicitParam(name = "code", value = "枚举编码(可调用/res/enum/codes接口查找所需枚举编码)", required = true, paramType = "path")
    })
    @GetMapping("/enum/{code}")
    @ResponseBody
    public Map<String, String> commonEnum(@PathVariable Integer code) {
        Map<String, String> map;
        Assert.notNull(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        map = Enums.genMap(code);
        log.info("通用枚举获取接口 : " + JSON.toJSONString(map));
        return map;
    }

    /**
     * 枚举汇总
     *
     * @return
     */
    @ApiOperation(value = "枚举汇总", notes = "系统通用枚举 汇总查阅")
    @GetMapping("/enum/codes")
    @ResponseBody
    public List<Map<String, String>> getEnumCodes() {
        List<Map<String, String>> list = null;
        try {
            //1.检查缓存，不存在就缓存
            list = Enums.getEnumsDescs();
            return list;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return list;
    }

    /**
     * 枚举管理器
     */
    private enum Enums {
        ProjectStateEnum(1000, io.aicode.project.entity.ProjectStateEnum.class)/*项目状态*/,
        ProjectSqlStateEnum(1001, io.aicode.project.entity.ProjectSqlStateEnum.class)/*项目sql状态*/,
        ProjectRepositoryTypeEnum(1002, io.aicode.project.entity.ProjectRepositoryTypeEnum.class)/*仓库类型*/,
        ProjectRepositoryAccountStateEnum(1003, io.aicode.project.entity.ProjectRepositoryAccountStateEnum.class)/*项目仓库状态*/,
        ProjectJobStateEnum(1004, io.aicode.project.entity.ProjectJobStateEnum.class)/*任务状态*/,
        FrameworksStateEnum(1005, io.aicode.project.entity.FrameworksStateEnum.class)/*模板状态*/,
        FileTypeEnum(1006, io.aicode.project.entity.FileTypeEnum.class)/*文件类型*/,
        DisplayAttributeEnum(1007, io.aicode.project.entity.DisplayAttributeEnum.class)/*显示属性*/,
        YNEnum(1101, io.aicode.core.enums.YNEnum.class)/*是否*/,
        SexEnum(1102, io.aicode.core.enums.SexEnum.class)/*性别枚举*/,
        OperateEnum(1103, io.aicode.core.enums.OperateEnum.class)/*操作名称定义*/,
        ModuleEnum(1104, io.aicode.core.enums.ModuleEnum.class)/*模块定义*/,


        /**/;

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

        public static List<Map<String, String>> getEnumsDescs() {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (Enums enums : Enums.values()) {
                Map<String, String> map = new HashMap<>();
                map.put("枚举编码 [code = " + String.valueOf(enums.code) + "]", enums.name());
                list.add(map);
            }
            return list;
        }


    }


}
