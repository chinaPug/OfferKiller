package top.zhixingege.security.config;

import  top.zhixingege.common.utils.StringUtils;
import  top.zhixingege.security.entity.MyUserDetails;
import  top.zhixingege.security.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 请求认证过滤器，从表单获取参数，不用security的默认参数名username、password
@Configuration

public class MyServerFormLoginAuthenticationConverter extends ServerFormLoginAuthenticationConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServerFormLoginAuthenticationConverter.class);

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        LOGGER.info("请求认证过滤器----MyServerFormLoginAuthenticationConverter.........");
        String uri = exchange.getRequest().getURI().getPath();
        if (StringUtils.DEFAULT_LOGIN_URL.equals(uri)) {  //登录操作才对body做特殊操作，其他请求直接调用原有请求
            return this.apply(exchange);
        } else { //非登录操作，基本不用在网关里读取body，默认方法就行
            return super.convert(exchange);
        }
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        //获取本次ip
        final String ipAddr = IpUtils.getIpAddr(exchange.getRequest());
        return exchange.getFormData().map((data) -> MyUserDetails.createAuthentication(data));
    }

}
