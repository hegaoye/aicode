
package ${basePackage}.res.ctrl;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.annotation.Ignore;
import ${basePackage}.core.enums.SexEnum;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.redis.RedisUtils;
import com.google.common.collect.Maps;
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
            redisUtils.set(Enums.genCachekey(code), JSON.toJSONString(map));
            logger.info("通用枚举获取接口 : " + JSON.toJSONString(map));
            return map;
        } else {
            //2.缓存中获取
            String mapStr = redisUtils.get(cachekey).toString();
            logger.info("通用枚举获取接口 : " + mapStr);
            return JSON.parseObject(mapStr, Map.class);
        }
    }


    /**
     * 枚举管理器
     */
    private enum Enums {
        SexEnum(1001, SexEnum.class)/*性别枚举*/,;

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
