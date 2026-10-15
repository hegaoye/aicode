
package com.aicode.core.tools;

import lombok.extern.slf4j.Slf4j;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixin on 2017/9/13.
 */
@Slf4j
public class JwtToken {

    private static long expireMinutes = 60L;//60分钟
    private static String secret = "ShenzhenXiDeKeJi20180828@pi-top.com";//临时同步秘钥的设定，正常情况下应该设置到setting中可以随时进行更改


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
            if (jwt != null && jwt.getToken().equals(token)) {
                return true;
            } else {
                return false;
            }
        } catch (JWTVerificationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return false;
    }


    /**
     * 验证token
     *
     * @param token  需要验证的token文本
     * @param secret 秘钥
     * @return true/false
     */
    public static boolean verifier(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt != null && jwt.getToken().equals(token)) {
                return true;
            } else {
                return false;
            }
        } catch (JWTVerificationException exception) {
            log.error("JWT 处理失败", exception);
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
            Claim claim = jwt.getClaim(key);
            String value = claim.asString();
            return value;
        } catch (JWTVerificationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return null;
    }

    /**
     * 获取token 值
     *
     * @param token  token
     * @param secret 秘钥
     * @param key    验证值key
     * @return true/false
     */
    public static String getTokenValue(String token, String secret, String key) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Claim claim = jwt.getClaim(key);
            String value = claim.asString();
            return value;
        } catch (JWTVerificationException exception) {
            log.error("JWT 处理失败", exception);
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
            String token = JWT.create()
                    .withExpiresAt(expires)
                    .withClaim(key, value)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return null;
    }

    /**
     * 创建token
     *
     * @param builder       加密器
     * @param secret        加密秘钥
     * @param expireMinutes 过期时间
     * @return token
     */
    public static String createToken(JWTCreator.Builder builder, String secret, long expireMinutes) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expires = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(expireMinutes));
            String token = builder
                    .withExpiresAt(expires)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return null;
    }


    /**
     * 创建token
     *
     * @param key           token key
     * @param value         token 值
     * @param secret        加密秘钥
     * @param expireMinutes 过期时间
     * @return token
     */
    public static String createToken(String key, String value, String secret, long expireMinutes) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expires = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(expireMinutes));
            String token = JWT.create()
                    .withExpiresAt(expires)
                    .withClaim(key, value)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return null;
    }


    /**
     * 创建token
     *
     * @param expireMinutes 过期时间
     * @param secret        加密秘钥
     * @return token
     */
    public static String createToken(String secret, long expireMinutes) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date now = new Date();
            Date expires = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(expireMinutes));
            String token = JWT.create()
                    .withExpiresAt(expires)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("JWT 处理失败", exception);
        }
        return null;
    }


}
