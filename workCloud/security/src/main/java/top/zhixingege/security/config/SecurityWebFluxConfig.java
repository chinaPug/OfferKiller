package top.zhixingege.security.config;

import  top.zhixingege.common.utils.StringUtils;
import  top.zhixingege.security.filter.MyWebSessionServerSecurityContextRepository;
import  top.zhixingege.security.handler.AuthEntryPointExceptionHandler;
import  top.zhixingege.security.handler.MyServerAuthenticationFailureHandler;
import  top.zhixingege.security.handler.MyServerAuthenticationSuccessHandler;
import  top.zhixingege.security.handler.MyWebFluxServerAccessDeniedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.WebFilter;

import java.util.Iterator;

@Configuration
@EnableWebFluxSecurity
public class SecurityWebFluxConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityWebFluxConfig.class);

    @Autowired
    //授权管理
    private MyReactiveAuthorizationManager reactiveAuthorizationManager;

    @Autowired
    //未登陆无访问权限的返回结果
    private AuthEntryPointExceptionHandler serverAuthenticationEntryPoint;

    @Autowired
    //登录成功时调用的自定义处理类
    private MyServerAuthenticationSuccessHandler myServerAuthenticationSuccessHandler;

    @Autowired
    //登录失败或其他异常访问调用的自定义处理类
    private MyServerAuthenticationFailureHandler myServerAuthenticationFailureHandler;

    @Autowired
    //无权限访问被拒绝时的自定义处理器
    private MyWebFluxServerAccessDeniedHandler myWebFluxServerAccessDeniedHandler;

    @Autowired
    //成功登出实现类
    private ServerLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    //重写存储认证信息，修改session默认时效和更新会话时间
    private MyWebSessionServerSecurityContextRepository myWebSessionServerSecurityContextRepository;
    @Autowired
    //请求认证过滤器，从表单获取参数，不用security的默认参数名username、password
    private MyServerFormLoginAuthenticationConverter myServerFormLoginAuthenticationConverter;
    @Autowired
    //自定义处理登陆
    private MyReactiveAuthenticationManager myReactiveAuthenticationManager;

    // 主要过滤配置类
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        LOG.info("加载security 权限配置....");
        http
                .csrf().disable()
                // 存储认证信息，这里修改session时效
                .securityContextRepository(myWebSessionServerSecurityContextRepository)
                // 设置登陆地址，如果是前后端分离，就不用设置，前端处理。
                .formLogin()
                //.loginPage(StringUtils.DEFAULT_LOGOUT_HTML_1)
                // 登陆请求方式和接口
                .requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,  StringUtils.DEFAULT_LOGIN_URL))
                // 处理登陆
                .authenticationManager(myReactiveAuthenticationManager)
                // 登录成功handler
                .authenticationSuccessHandler(myServerAuthenticationSuccessHandler)
                // 登陆失败handler
                .authenticationFailureHandler(myServerAuthenticationFailureHandler)
                // 关闭默认登录验证
                .and().httpBasic().disable()
                // 登出，设置登出请求类型和URL
                .logout().requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, StringUtils.DEFAULT_LOGOUT_URL_1))
                // 登出成功后自定义处理
                .logoutSuccessHandler(logoutSuccessHandler)

                // 未登陆无访问权限handler
                .and().exceptionHandling().authenticationEntryPoint(serverAuthenticationEntryPoint)
                // 登陆无访问权限
                .and().exceptionHandling().accessDeniedHandler(myWebFluxServerAccessDeniedHandler)

                // 自定义鉴权
                .and().authorizeExchange().anyExchange().access(reactiveAuthorizationManager);
        SecurityWebFilterChain chain = http.build();
        Iterator<WebFilter> weIterable = chain.getWebFilters().toIterable().iterator();
        while (weIterable.hasNext()) {
            WebFilter f = weIterable.next();
            if (f instanceof AuthenticationWebFilter) {
                AuthenticationWebFilter webFilter = (AuthenticationWebFilter) f;
                //将自定义的AuthenticationConverter添加到过滤器中
                webFilter.setServerAuthenticationConverter(myServerFormLoginAuthenticationConverter);
            }
        }
        return chain;
    }
}
