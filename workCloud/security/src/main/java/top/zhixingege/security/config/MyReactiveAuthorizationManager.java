package top.zhixingege.security.config;

import  top.zhixingege.common.utils.StringUtils;
import top.zhixingege.common.entity.UserEntity;
import  top.zhixingege.security.service.UserService;
import  top.zhixingege.security.utils.IpUtils;
import  top.zhixingege.security.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 自定义的鉴权服务，通过鉴权的才能继续访问某个请求。反应式授权管理器接口
@Configuration
public class MyReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyReactiveAuthorizationManager.class);
    @Autowired
    private UserService userService;

    /**
     * 实现权限验证判断
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authenticationMono, AuthorizationContext authorizationContext) {
        LOGGER.info("---自定义的鉴权服务---MyReactiveAuthorizationManager---");
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        /**
         *        此处捕捉ip请求！！！！
         */

        String ipAddr = IpUtils.getIpAddr(request);
        // option请求默认放行，解决跨域问题
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            LOGGER.debug("---自定义的鉴权服务---MyReactiveAuthorizationManager---跨域放行");
            return Mono.just(new AuthorizationDecision(true));
        }
        //请求资源
        final String url = request.getURI().getPath();
        // 白名单放行，不用登陆就可以访问
        for (String requestRulWhite : StringUtils.REQUEST_RUL_WHITE_S) {
            if ((requestRulWhite.endsWith(StringUtils.WILDCARD) && url.startsWith(requestRulWhite.substring(0, requestRulWhite.length() - StringUtils.WILDCARD.length())))
                    || requestRulWhite.equals(url)) {
                LOGGER.debug("---自定义的鉴权服务---MyReactiveAuthorizationManager---白名单url放行");
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        final HttpHeaders requestHeaders = request.getHeaders();
        final HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        String authentication = JwtTokenUtils.getCookieAuthentication(requestHeaders);
        boolean tokenExpired = JwtTokenUtils.checkTokenAndRefreshToken(responseHeaders, authentication);
        if (!tokenExpired) {
            LOGGER.warn("token过期");
            return Mono.error(new CredentialsExpiredException("token过期，请重新登陆"));
        } else {
            LOGGER.debug("token有效");
        }
        return authenticationMono.map(auth -> new AuthorizationDecision(this.checkAuthorities(auth, url))
        ).defaultIfEmpty(
                new AuthorizationDecision(defaultIsToken(authentication, url))
//                new AuthorizationDecision(false)
        );
    }

    // 只有token情况下处理
    private boolean defaultIsToken(String token, String url) {
        if (ObjectUtils.isEmpty(token)) {
            return false;
        }
        String username = JwtTokenUtils.getUsernameFromToken(token);
        return this.checkAuthorities(username, url);
    }

    //权限校验，指定的url需要对应的角色，不指定的登陆成功就可以访问
    private boolean checkAuthorities(Authentication auth, String url) {
        if (ObjectUtils.isEmpty(auth)) {
            return false;
        }
        UserDetails principal = (UserDetails) auth.getPrincipal();
        if (ObjectUtils.isEmpty(principal)) {
            return false;
        }
        return this.checkAuthorities(principal.getUsername(), url);
    }

    //权限校验，指定的url需要对应的角色，不指定的登陆成功就可以访问
    private boolean checkAuthorities(String username, String url) {
        LOGGER.info("---自定义的鉴权服务---url:{}---" , url);
        if (ObjectUtils.isEmpty(username)) {
            return false;
        }
        UserEntity userEntity = userService.selectUserByEmail(username);
        if (ObjectUtils.isEmpty(userEntity)) {
            return false;
        }
        LOGGER.info("访问的URI是：{}，用户信息：{}" , url, username);
        String role = userEntity.getRole();
        //权限评估

        if ("/web/webLogin/user1".equals(url)) {
            return "3".equals(role);
        }
        if ("/web/webLogin/useri".equals(url)) {
            return "k".equals(role);
        }
        if ("/web/webLogin/usera".equals(url)) {
            return "c".equals(role) || "k".equals(role);
        }
        // 非指定接口，只要登陆都有权限
        return true;
    }
}
