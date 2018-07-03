package ${basePackage}.core.base;

import ${basePackage}.core.tools.HandleFuncs;
import ${basePackage}.core.tools.redis.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * Created by bobai on 2017/6/1.
 */
public class    BaseUploadCtrl extends BaseCtrl {
    @Resource
    public RedisTemplate<String, Object> redisTemplate;
    @Resource
    public RedisUtils redisUtils;

    /**
     * 生成图片名称
     *
     * @param uid 贷款单编号
     * @return
     */
    public String genCacheImgName(String uid) {
        if (StringUtils.isBlank(uid)){
            return new HandleFuncs().uuidGenerate();
        }
        // 纸质合同(多个文件，后缀名不定)：{loan}-{n}，{n为正整数，从1开始编号}
        int imgNameNUM = 1;
        String imgName = uid + "-";
        String imgNameCacheKey = "imgName:" + uid;
        if (redisUtils.get(imgNameCacheKey) != null) {
            imgNameNUM = (int) redisUtils.get(imgNameCacheKey);
            imgNameNUM += 1;
        }
        if (imgNameNUM < 10) {
            imgName += "00" + imgNameNUM;
        } else {
            imgName += imgNameNUM;
        }
        redisUtils.set(imgNameCacheKey, imgNameNUM, 1200/*20分钟*/);
        return imgName;
    }


}
