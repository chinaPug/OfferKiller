package top.zhixingege.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

// 重写存储认证信息，修改session默认时效和更新会话时间
@Configuration
public class MyWebSessionServerSecurityContextRepository extends WebSessionServerSecurityContextRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSessionServerSecurityContextRepository.class);
    @Value("${session.timeout}")
    private Long timeout;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // 只有登陆时执行，并且在load()执行之后
        LOGGER.info("存储认证信息---save---url：{}", exchange.getRequest().getURI().getPath());
        return exchange.getSession()
                .doOnNext(session -> {
                    if (context == null) {
                        session.getAttributes().remove(super.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
                    } else {
                        session.getAttributes().put(super.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, context);
                        // 在这里设置过期时间 单位使用Duration类中的定义  有秒、分、天等
                        session.setMaxIdleTime(Duration.ofSeconds(timeout));
                    }
                })
                .flatMap(session -> session.changeSessionId());
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        LOGGER.info("存储认证信息---load---url：{}", url);
        return exchange.getSession().flatMap((session) -> {
            SecurityContext context = session.getAttribute(super.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
            if (context == null) {
                session.getAttributes().remove(super.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
            } else {
                session.getAttributes().put(super.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, context);
                // 在这里设置过期时间 单位使用Duration类中的定义  有秒、分、天等
                session.setMaxIdleTime(Duration.ofSeconds(timeout));
            }
            return Mono.justOrEmpty(context);
        });
    }

}
