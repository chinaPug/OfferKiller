package top.zhixingege.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;
import top.zhixingege.common.entity.UserEntity;
import top.zhixingege.security.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Nullable
    public UserEntity selectUserByEmail(String email){
        if ("1431105872@qq.com".equals(email)) return new UserEntity("1431105872@qq.com",passwordEncoder.encode("123"),"admin");
        return null;
    }
}
