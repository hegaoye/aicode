package ${basePackage}.core.tools;

import ${basePackage}.core.enums.SexEnum;
import ${basePackage}.core.tools.redis.RedisUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by borong on 2017/6/22.
 */
@Component
public class IdCardTools {
    private final static Logger logger = LoggerFactory.getLogger(IdCardTools.class);
    @Resource
    private RedisUtils redisUtils;
    private static Map<String, Object> map = null;

    /**
     * 检查身份证号（高级验证）含 前6位区域单独验证，次8位生日验证，末位校验码全18位验证
     * 可检查15位及18位身份证验证
     *
     * @param idCard
     * @return true 格式正确，false 格式不正确
     */
    public boolean checkIdCard(String idCard) {
        //调用工具类校验身份证号格式是否正确
        if (!StringTools.checkIdCard(idCard)) {
            return false;
        }
        String areaCode = idCard.substring(0, 6);   //取出前6位身份证号行政区域编码
        Map<String, Object> param = new HashMap<>();
        param.put("areaCode", areaCode);
        if (map == null) {
            //取得区域对象，检查对象是否存在
            String area_cache = (String) redisUtils.get("area_cache");
            if (!StringTools.isNotEmpty(area_cache)){
                return false;
            }
            map = JSON.parseObject(area_cache);
        }
        if (map == null) {
            return false;
        }

        //检查身份证号区域是否存在
        if (map.containsKey(areaCode)) {
            return true;
        }
        return false;
    }

    /**
     * 从身份证号中获取到行政区域代码
     *
     * @param idCard
     * @return 身份证号码中前6位行政区域代码
     */
    public static String getAreaCodeByIdCard(String idCard) {
        String areaCode = "";
        if (StringTools.isNotEmpty(idCard)) {
            areaCode = idCard.substring(0, 6);
        }
        return areaCode;
    }

    /**
     * 从身份证号中读取生日
     *
     * @param idCard
     * @return 返回日期格式 yyyy-MM-dd
     */
    public static Date getBirthdayFromIdCard(String idCard) {
        if (StringTools.isNotEmpty(idCard)) {
            String birthday = "";
            if (idCard.length() == 15) {
                birthday = "19" + idCard.substring(6, 12);
            } else if (idCard.length() == 18) {
                birthday = idCard.substring(6, 14);
            }

            DateFormat formatData = new SimpleDateFormat("yyyyMMdd");
            try {
                Date data = formatData.parse(birthday);
                return data;
            } catch (ParseException e) {
                logger.error("从身份证号中读取生日时，格式化错误");
                return null;
            }
        }
        return null;
    }

    /**
     * 从身份证号中获得人员的性别
     * 调用此方法，请务必调用 checkIdCard 方法验证身份证号格式的合法性
     *
     * @param idCard
     * @return
     */
    public static SexEnum getSexFromIdCard(String idCard) {
        int str17 = Integer.parseInt(idCard.substring(16, 17));
        return str17 % 2 == 0 ? SexEnum.FEMALE : SexEnum.MALE;
    }
}