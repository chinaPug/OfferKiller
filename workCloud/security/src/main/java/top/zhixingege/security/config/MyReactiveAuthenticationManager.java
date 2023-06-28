package top.zhixingege.security.config;

import  top.zhixingege.common.utils.StringUtils;
import  top.zhixingege.security.entity.MyUserDetails;
import top.zhixingege.common.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import top.zhixingege.security.service.UserService;

// 自定义处理登陆
@Configuration
public class MyReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyReactiveAuthenticationManager.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        LOGGER.info("自定义处理登陆----MyReactiveAuthenticationManager.........");
        //判断是不是自己定义的 MyUserDetails，是的话就是走下面
        if (authentication instanceof MyUserDetails) {
            MyUserDetails myUserDetails = (MyUserDetails) authentication;
            //如果email不是空
            if (!ObjectUtils.isEmpty(myUserDetails)) {
                //如果不是空就
                return this.authenticateEmail(myUserDetails);
            }
        }
        //后期改CodeMsg对象
        return Mono.error(new DisabledException("输入错误"));
    }


    public Mono<Authentication> authenticateEmail(MyUserDetails myUserDetails) {
        LOGGER.info("自定义处理登陆----邮箱登陆.........");

        UserDetails user;
        UserEntity userEntity;
        //根据邮箱查找用户信息
        userEntity = userService.selectUserByEmail(myUserDetails.getUsername());
        if (ObjectUtils.isEmpty(userEntity)) {
            return Mono.error(new UsernameNotFoundException("系统无此邮箱用户"));
        }
        if(!passwordEncoder.matches(myUserDetails.getPassword(), userEntity.getPassword())){
            return Mono.error(new BadCredentialsException("密码错误:" + myUserDetails.getUsername()));
        }
        User.UserBuilder userBuilder = User.builder().passwordEncoder(passwordEncoder::encode)
                .username(userEntity.getEmail())
                .password(userEntity.getPassword());
        String role = userEntity.getRole();
        if (!ObjectUtils.isEmpty(role)) {
            userBuilder.roles(role);
        }
        user = userBuilder.build();
        final Authentication authentication1 = new UsernamePasswordAuthenticationToken(user, userEntity.getPassword(), user.getAuthorities());
        // TODO WebFlux方式默认没有放到context中，需要手动放入
        SecurityContextHolder.getContext().setAuthentication(authentication1);
        return Mono.just(authentication1);
    }

}
