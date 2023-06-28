package top.zhixingege.security.handler;

import  top.zhixingege.common.utils.StringUtils;
import  top.zhixingege.security.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

// 无权限访问被拒绝时的自定义处理器。如不自己处理，默认返回403错误<br>
@Component
public class MyWebFluxServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebFluxServerAccessDeniedHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        LOGGER.info("无权限访问被拒绝时的自定义处理器----MyAccessDeniedHandlerWebFlux.........");
        String username = JwtTokenUtils.getCookieUsername(exchange.getRequest().getHeaders());
        if (ObjectUtils.isEmpty(username)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!ObjectUtils.isEmpty(authentication)) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                if (!ObjectUtils.isEmpty(userDetails)) {
                    username = userDetails.getUsername();
                }
            }
        }
        if (null == username) {
            username = StringUtils.EMPTY;
        }

        String jsonString = String.format("{\"code\":200,\"status\":3,\"msg\":\"%s您无此资源的访问权限！\"}" , username, exchange.getRequest().getURI().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeAndFlushWith(Flux.just(Flux.just(response.bufferFactory().wrap(jsonString.getBytes(StandardCharsets.UTF_8)))));
    }
}
