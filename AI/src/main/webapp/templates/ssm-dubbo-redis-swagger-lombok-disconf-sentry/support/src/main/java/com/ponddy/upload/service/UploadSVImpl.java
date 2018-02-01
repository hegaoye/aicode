/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

package ${basePackage}.upload.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.entity.UploadBasicEntity;
import ${basePackage}.core.enums.ExceptionMsgEnum;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.exceptions.UploadException;
import ${basePackage}.core.tools.redis.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lixin on 2017/8/17.
 */
@Component
@Service
public class UploadSVImpl extends BaseMybatisSVImpl<UploadBasicEntity, Long> implements UploadSV {
    protected final static Logger logger = LoggerFactory.getLogger(UploadSVImpl.class);

    /**
     * 更新上传信息
     * 1.检测缓存
     * 2.初始态 -> 变更态
     * 3.更新缓存数据
     *
     * @param uniqueCode 商品编码
     * @param uid        编码
     * @param paths      路径集合
     * @return UploadBasicEntity
     */
    @Override
    public UploadBasicEntity upload(String uniqueCode, String uid, List<String> paths) throws UploadException {
        //1.检测缓存
        UploadBasicEntity uploadBasicEntity = null;
        String uidKey = RedisKey.genUploadKey(uid);
        logger.info(uidKey);

        //2.初始态 -> 变更态
        Object json = redisUtils.get(uidKey);
        if (json == null)  {
            throw new UploadException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        uploadBasicEntity = JSON.parseObject(json.toString(), UploadBasicEntity.class);
        if (uploadBasicEntity == null)  {
            throw new UploadException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        if (uploadBasicEntity.getState().equals(UploadBasicEntity.UploadState.Init.name())) {
            if (uniqueCode != null) {
                uploadBasicEntity.setUniqueCode(uniqueCode);
                uploadBasicEntity.setState(UploadBasicEntity.UploadState.Changed.name());//变更态
            } else {
                uploadBasicEntity.setPaths(paths);
                uploadBasicEntity.setState(UploadBasicEntity.UploadState.Changed.name());//变更态
            }

            redisUtils.set(uidKey, JSON.toJSONString(uploadBasicEntity));//更新缓存
            return uploadBasicEntity;
        }

        //3.更新缓存数据
        uploadBasicEntity.setPaths(paths != null ? paths : uploadBasicEntity.getPaths());
        uploadBasicEntity.setUniqueCode(uniqueCode != null ? uniqueCode : uploadBasicEntity.getUniqueCode());
        uploadBasicEntity.setState(UploadBasicEntity.UploadState.Overdue.name());
        redisUtils.set(RedisKey.genUploadKey(uid), JSON.toJSONString(uploadBasicEntity));

        return uploadBasicEntity;
    }


    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return null;
    }

}
