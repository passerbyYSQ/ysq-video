package net.ysq.video.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JWT的工具类，包括签发、验证、获取信息
 *
 * @author passerbyYSQ
 * @create 2020-08-22 11:13
 */
public class JwtUtil {

    // 有效时间：7天
    public static final long EFFECTIVE_DURATION = 1000 * 60 * 60 * 24 * 7;
    // 发行者
    private static final String ISSUER = "net.ysq";
    // 默认密钥
    private static final String DEFAULT_SECRET = "ysqJYKL2010!";

    /**
     * 生成Jwt字符串
     *
     * @param claims    由于类库只支持基本类型的包装类、String、Date，我们最好使用String
     * @param secret    加密的密钥
     * @return
     */
    public static String generateJwt(Map<String, String> claims, String secret) {
        // 发行时间
        Date issueAt = new Date();
        // 过期时间
        Date expireAt = new Date(issueAt.getTime() + EFFECTIVE_DURATION);
        // 加密算法
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        JWTCreator.Builder builder = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(issueAt)
                .withExpiresAt(expireAt);

        // 设置Payload信息
        Set<String> keySet = claims.keySet();
        for (String key : keySet) {
            builder.withClaim(key, claims.get(key));
        }

        return builder.sign(algorithm);
    }

    public static String generateJwt(Map<String, String> claims) {
        return generateJwt(claims, DEFAULT_SECRET);
    }

    public static String generateJwt(String username, String secret) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", username);
        return generateJwt(claims, secret);
    }

    /**
     * 校验jwt是否合法
     * 对异常进行全局的统一捕获
     * 父类异常：JWTVerificationException
     *
     * 子类异常
     * 过期：TokenExpiredException
     * ...
     *
     * @param jwt
     * @param claims
     * @return
     */
    public static void verifyJwt(String jwt, Map<String, String> claims, String secret) {
        // 解密算法
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        Verification verification = JWT.require(algorithm).withIssuer(ISSUER);

        Set<String> keySet = claims.keySet();
        for (String key : keySet) {
            verification.withClaim(key, claims.get(key));
        }

        JWTVerifier verifier = verification.build();
        verifier.verify(jwt);
    }

    public static void verifyJwt(String jwt, Map<String, String> claims) {
        verifyJwt(jwt, claims, DEFAULT_SECRET);
    }

    public static void verifyJwt(String jwt, String username, String secret) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", username);
        verifyJwt(jwt, claims, secret);
    }

    /**
     * 根据key获取claim值
     *
     * @param jwt
     * @param key
     * @return
     */
    public static String getClaimByKey(String jwt, String key) {
        DecodedJWT decodedJwt = JWT.decode(jwt);
        return decodedJwt.getClaim(key).asString(); // 注意不要用toString
    }

    /**
     * 返回过期的时间
     *
     * @param jwt
     * @return
     */
    public static Date getExpireAt(String jwt) {
        DecodedJWT decodedJwt = JWT.decode(jwt);
        return decodedJwt.getExpiresAt();
    }
}
