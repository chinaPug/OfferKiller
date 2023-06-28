package top.zhixingege.security.utils;

import top.zhixingege.common.utils.StringUtils;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class JwtTokenUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtils.class);
    // 有效时间，单位毫秒
//    public static final long EXPIRATION = 30 * 1000L;
    public static final long EXPIRATION = 40 * 60 * 1000L;
    public static final long TOKEN_REFRESH_DATE = 15 * 1000L;
    //    public static final long TOKEN_REFRESH_DATE = 20 * 60 * 1000L;
    public static final String TOKEN_REFRESH_DATE_STR = "TOKEN_REFRESH_DATE";
    //JWT密钥
    public static final String SECRET = "123654";

    public static final String BASIC_EMPTY = "Basic ";
    public static final String BASIC_EMPTY_ = "Basic%20";
    public static final String AUTHENTICATION = "Authorization";
    public static final String COOKIE_AUTHENTICATION_BASIC_EMPTY_ = "Authorization=Basic%20";
    public static final String COOKIE_SPLIT = ";";
    public static final String COOKIE = "Cookie";
    public static final String TOKEN_CREATED = "created";
    public static final String TOKEN_REFRESH_FLAG = "RefreshTokenFlag";
    public static final String TOKEN_REFRESH_YES = "1";
    public static final String TOKEN_REFRESH_NO = "0";

    public static final String COOKIE_VERIFY_CODE = "verifyCode=";
    public static final String USER_NAME = "userName";
    public static final String PASS_WORD = "passWord";

    public static final int LOGIN_ERROR_COUNT = 5;

    /**
     * 生成token令牌
     *
     * @param username 用户
     * @param payloads 令牌中携带的附加信息
     * @return 令token牌
     */
    public static String generateToken(String username, Map<String, Object> payloads) {
        int payloadSizes = payloads == null ? 0 : payloads.size();

        Map<String, Object> claims = new HashMap<>(payloadSizes + 2);
        claims.put(Claims.SUBJECT, username);
        claims.put(TOKEN_CREATED, new Date());

        if (payloadSizes > 0) {
            claims.putAll(payloads);
        }

        return generateToken(claims);
    }

    /**
     * 从claims生成令牌,如果看不懂就看谁调用它
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private static String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION);
        // 刷新token时间
        claims.put(JwtTokenUtils.TOKEN_REFRESH_DATE_STR, new Date(System.currentTimeMillis() + TOKEN_REFRESH_DATE));
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return false;
        }
        try {
            Claims claims = getClaimsFromToken(token);
            if (ObjectUtils.isEmpty(claims)) {
                return false;
            }
            Date expiration = claims.getExpiration();
            return new Date().before(expiration);
        } catch (Exception e) {
            LOGGER.error("判断令牌是否过期异常", e);
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            LOGGER.error("从令牌中获取用户名异常1", e);
            username = null;
        }
        return username;
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public static String refreshToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(TOKEN_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            LOGGER.error("刷新令牌异常", e);
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 从令牌中获取数据声明,如果看不懂就看谁调用它
     *
     * @param token 令牌
     * @return 数据声明
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            JwtParser jwtParser = Jwts.parser().setSigningKey(SECRET);
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            claims = claimsJws.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("从令牌中获取数据声明异常");
//            LOGGER.error("从令牌中获取数据声明异常", e);
            claims = null;
        }
        return claims;
    }

    public static String getCookieUsername(HttpHeaders headers) {
        String authentication = getCookieAuthentication(headers);
        if (ObjectUtils.isEmpty(authentication)) {
            return null;
        }
        return getUsernameFromToken(authentication);
    }

    public static String getCookieAuthentication(HttpHeaders headers) {
        String authentication = headers.getFirst(JwtTokenUtils.AUTHENTICATION);
        if (ObjectUtils.isEmpty(authentication)) {
            String cookieStr = headers.getFirst(JwtTokenUtils.COOKIE);
            if (!ObjectUtils.isEmpty(cookieStr)) {
                cookieStr = cookieStr.replaceAll(StringUtils.BLANK, StringUtils.EMPTY);
                String[] cookies = cookieStr.split(JwtTokenUtils.COOKIE_SPLIT);
                for (String c : cookies) {
                    if (!ObjectUtils.isEmpty(c) && c.startsWith(JwtTokenUtils.COOKIE_AUTHENTICATION_BASIC_EMPTY_)) {
                        authentication = c.replaceFirst(JwtTokenUtils.COOKIE_AUTHENTICATION_BASIC_EMPTY_, StringUtils.EMPTY);
                        break;
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(authentication)) {
            if (authentication.startsWith(JwtTokenUtils.BASIC_EMPTY_)) {
                authentication = authentication.replaceFirst(JwtTokenUtils.BASIC_EMPTY_, StringUtils.EMPTY);
            } else if (authentication.startsWith(JwtTokenUtils.BASIC_EMPTY)) {
                authentication = authentication.replaceFirst(JwtTokenUtils.BASIC_EMPTY, StringUtils.EMPTY);
            }
        }
        return authentication;
    }

    public static String getCookieVerifyCode(HttpHeaders headers) {
        String cookieStr = headers.getFirst(JwtTokenUtils.COOKIE);
        if (ObjectUtils.isEmpty(cookieStr)) {
            return null;
        }
        cookieStr = cookieStr.replaceAll(StringUtils.BLANK, StringUtils.EMPTY);
        String[] cookies = cookieStr.split(JwtTokenUtils.COOKIE_SPLIT);
        for (String c : cookies) {
            if (!ObjectUtils.isEmpty(c) && c.startsWith(JwtTokenUtils.COOKIE_VERIFY_CODE)) {
                return c.replaceFirst(JwtTokenUtils.COOKIE_VERIFY_CODE, StringUtils.EMPTY);
            }
        }
        return null;
    }

    private static final Map<String, Date> LOG_TOKEN_DATE_MAP = new ConcurrentHashMap<>();
    private static final ExecutorService POOL = java.util.concurrent.Executors.newFixedThreadPool(2);

    public static boolean checkTokenAndRefreshToken(HttpHeaders headers, String authentication) {
        boolean tokenExpired = false;
        Date now = new Date();
        if (ObjectUtils.isEmpty(authentication)) {
            return tokenExpired;
        }
        try {
            Claims claims = getClaimsFromToken(authentication);
            if (ObjectUtils.isEmpty(claims)) {
                return tokenExpired;
            }
            Date expiration = claims.getExpiration();
            if (now.after(expiration)) {
                return tokenExpired;
            }
            Date expirationTokenRefresh = claims.get(TOKEN_REFRESH_DATE_STR, Date.class);
            tokenExpired = now.before(expirationTokenRefresh);
        } catch (Exception e) {
            LOGGER.error("判断令牌是否过期异常", e);
            return false;
        }

        headers.set(TOKEN_REFRESH_FLAG, TOKEN_REFRESH_NO);
        if (tokenExpired) {
            // token有效
            POOL.execute(new LogTokenRunnable(LOG_TOKEN_DATE_MAP, authentication, now));
            return tokenExpired;
        } else {
            Date date = LOG_TOKEN_DATE_MAP.get(authentication);
            if (ObjectUtils.isEmpty(date)) {
                return tokenExpired;
            }
            Date expirationDate = new Date(date.getTime() + EXPIRATION);
            if (expirationDate.before(now)) {
                return tokenExpired;
            }
            String refreshToken = refreshToken(authentication);
            if (ObjectUtils.isEmpty(refreshToken)) {
                return tokenExpired;
            }
            headers.set(TOKEN_REFRESH_FLAG, TOKEN_REFRESH_YES);
            headers.set(AUTHENTICATION, BASIC_EMPTY + refreshToken);
            return true;
        }
    }

    // 记录token最后请求时间
    private static class LogTokenRunnable implements Runnable {
        private Map<String, Date> map;
        private String token;
        private Date now;

        LogTokenRunnable(Map<String, Date> map, String token, Date now) {
            this.map = map;
            this.token = token;
            this.now = now;
        }

        @Override
        public void run() {
            if (null == map || ObjectUtils.isEmpty(token) || ObjectUtils.isEmpty(now)) {
                return;
            }
            Date date = map.get(token);
            if (ObjectUtils.isEmpty(date) || now.after(date)) {
                map.put(token, now);
            }
        }
    }
}
