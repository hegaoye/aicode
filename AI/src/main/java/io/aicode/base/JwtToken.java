/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于龐帝業務系统.
 *
 */

package io.aicode.base;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.aicode.base.tools.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixin on 2017/9/13.
 */
public class JwtToken {
    protected final static Logger logger = LoggerFactory.getLogger(JwtToken.class);

    private static long expireMinutes = 120L;//60分钟
    private static String secret = "hegaoye@qq.com/hegaoye@gmail.com/www.hegaoye.com";

    public static void main(String[] args) {
        String token = createToken("client_id", "c5f04615-7739-4267-877c-bedd9ab7fed3");
        boolean flag = verifier(token);
        System.out.println(flag);
    }


    /**
     * 验证token
     *
     * @param token
     * @return true/false
     */
    public static boolean verifier(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            logger.info("verifier token ==============================================");
            logger.info(jwt.getToken());
            logger.info(jwt.getHeader());
            logger.info(jwt.getPayload());
            logger.info(jwt.getSignature());
            if (jwt != null && jwt.getToken().equals(token)) {
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
        }
        return false;
    }


    /**
     * 获取token 值
     *
     * @param token
     * @param key   验证值key
     * @return true/false
     */
    public static String getTokenValue(String token, String key) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            logger.info("verifier token ==============================================");
            Claim claim = jwt.getClaim(key);
            logger.info(claim.asString());
            String value = claim.asString();
            return value;
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 创建token
     *
     * @param key   token key
     * @param value token 值
     * @return
     */
    public static String createToken(String key, String value) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expires = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(expireMinutes));
            logger.info(DateTools.yyyyMMddHHmmss(expires));
            String token = JWT.create()
                    .withExpiresAt(expires)
                    .withClaim(key, value)
                    .sign(algorithm);
            logger.info("create token==============================================");
            logger.info(token);
            logger.info("==============================================");
            return token;
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        }
        return null;
    }


}
