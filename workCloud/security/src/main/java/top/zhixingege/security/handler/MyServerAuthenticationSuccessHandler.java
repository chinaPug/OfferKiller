package top.zhixingege.security.handler;

import  top.zhixingege.security.utils.JwtTokenUtils;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// 登录成功时调用的自定义处理类
@Component
public class MyServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServerAuthenticationSuccessHandler.class);

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        LOGGER.info("登录成功时调用的自定义处理类----MyServerAuthenticationSuccessHandler.........");
        // 登录成功后可以放入一些参数到session中
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        HttpHeaders headers = response.getHeaders();

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String username = principal.getUsername();

        String token = JwtTokenUtils.generateToken(username, null);
        headers.set(JwtTokenUtils.AUTHENTICATION, String.format("%s%s", JwtTokenUtils.BASIC_EMPTY, token));

        String jsonString = String.format("{\"code\":200,\"status\":0,\"msg\":\"%s您登陆成功！\"}", username);
        headers.setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory().wrap(jsonString.getBytes(CharsetUtil.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
