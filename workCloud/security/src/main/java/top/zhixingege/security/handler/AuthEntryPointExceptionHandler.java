package top.zhixingege.security.handler;

import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// 未登陆无访问权限的返回结果
@Component
public class AuthEntryPointExceptionHandler extends HttpBasicServerAuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointExceptionHandler.class);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        LOGGER.info("未登陆无访问权限---{}--AuthEntryPointExceptionHandler.........", exchange.getRequest().getURI().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        String jsonString = "{\"code\":200,\"status\":4,\"msg\":\"您未登陆或登陆已过期，请先登陆！\"}";
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(jsonString.getBytes(CharsetUtil.UTF_8));
        return exchange.getResponse().writeWith(Flux.just(wrap));
    }
}