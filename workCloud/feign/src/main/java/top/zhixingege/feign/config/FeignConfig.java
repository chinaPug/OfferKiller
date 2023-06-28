package top.zhixingege.feign.config;


import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;

// 服务之间调用时 把请求头带上
public class FeignConfig implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isEmpty(attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        String name;
        String authorization = null;
        //添加token
        while (headerNames.hasMoreElements()) {
            name = headerNames.nextElement();
            String header = request.getHeader(name);
            if ("Authorization".equals(name) || (null != name && name.toLowerCase(Locale.ROOT).equals("authorization"))) {
                authorization = header;
            }
            if (name.equalsIgnoreCase("content-length")){
                continue;
            }
            requestTemplate.header(name, header);
        }
        if (ObjectUtils.isEmpty(authorization)) {
            Cookie[] cookies = request.getCookies();
            if (null == cookies) {
                return;
            }
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName()) || "authorization".equals(cookie.getName().toLowerCase(Locale.ROOT))) {
                    requestTemplate.header("Authorization", cookie.getValue());
                    break;
                }
            }
        }
    }
}
