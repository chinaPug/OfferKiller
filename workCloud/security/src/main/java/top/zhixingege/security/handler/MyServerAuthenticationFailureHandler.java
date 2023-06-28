package top.zhixingege.security.handler;

import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// 登录失败或其他异常访问调用的自定义处理类
@Component
public class MyServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServerAuthenticationFailureHandler.class);

    private static final String USER_NOT_EXISTS = "用户不存在，请先注册！";

    private static final String USERNAME_PASSWORD_ERROR = "用户或密码错误！";

    private static final String USER_LOCKED = "用户锁定！";
    private static final String USER_ACCOUNT_EXPIRED = "账号已过期！";
    private static final String USER_CREDENTIALS_EXPIRE = "票据已过期！";

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        LOGGER.info("登录失败时调用的自定义处理类----MyServerAuthenticationFailureHandler.........");
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (exception instanceof UsernameNotFoundException) {
            return writeErrorMessage(response, USER_NOT_EXISTS);
        } else if (exception instanceof BadCredentialsException) {
            return writeErrorMessage(response, USERNAME_PASSWORD_ERROR);
        } else if (exception instanceof LockedException) {
            return writeErrorMessage(response, USER_LOCKED);
        } else if (exception instanceof AccountExpiredException) {
            return writeErrorMessage(response, USER_ACCOUNT_EXPIRED);
        } else if (exception instanceof CredentialsExpiredException) {
            return writeErrorMessage(response, USER_CREDENTIALS_EXPIRE);
        } else if (exception instanceof DisabledException) {
            return writeErrorMessage(response, "不可访问，" + exception.getMessage());
        }
        return writeErrorMessage(response, exception.getMessage());
    }

    private Mono<Void> writeErrorMessage(ServerHttpResponse response, String message) {
        String jsonString = String.format("{\"code\":200,\"status\":1,\"msg\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(jsonString.getBytes(CharsetUtil.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
