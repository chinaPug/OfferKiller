package top.zhixingege.security.handler;

import  top.zhixingege.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;

import java.net.URI;

// 成功登出实现类
@Configuration
public class MyRedirectServerLogoutSuccessHandler implements ServerLogoutSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyRedirectServerLogoutSuccessHandler.class);
    private URI logoutSuccessUrl = URI.create(StringUtils.DEFAULT_LOGOUT_SUCCESS_URL);
    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    public MyRedirectServerLogoutSuccessHandler() {
    }

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        LOGGER.info("成功登出实现类----MyRedirectServerLogoutSuccessHandler.........");
        return this.redirectStrategy.sendRedirect(exchange.getExchange(), this.logoutSuccessUrl);
    }

}
